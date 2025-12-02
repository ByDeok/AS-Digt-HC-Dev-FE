package vibe.digthc.as_digt_hc_dev_fe.domain.integration.service;

import vibe.digthc.as_digt_hc_dev_fe.domain.integration.dto.PortalConnectReq;
import vibe.digthc.as_digt_hc_dev_fe.domain.integration.dto.PortalConnectionRes;
import vibe.digthc.as_digt_hc_dev_fe.domain.integration.dto.SyncResultRes;
import vibe.digthc.as_digt_hc_dev_fe.domain.integration.entity.PortalConnection;
import vibe.digthc.as_digt_hc_dev_fe.domain.integration.enums.ConsentSubjectType;
import vibe.digthc.as_digt_hc_dev_fe.domain.integration.enums.PortalStatus;
import vibe.digthc.as_digt_hc_dev_fe.domain.integration.exception.ConsentNotFoundException;
import vibe.digthc.as_digt_hc_dev_fe.domain.integration.repository.ConsentRecordRepository;
import vibe.digthc.as_digt_hc_dev_fe.domain.integration.repository.PortalConnectionRepository;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.User;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.repository.UserRepository;
import vibe.digthc.as_digt_hc_dev_fe.infrastructure.integration.PortalDataProvider;
import vibe.digthc.as_digt_hc_dev_fe.infrastructure.integration.PortalProviderFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PortalConnectionServiceImpl implements PortalConnectionService {

    private final PortalConnectionRepository portalRepository;
    private final ConsentRecordRepository consentRepository;
    private final UserRepository userRepository;
    private final PortalProviderFactory providerFactory;
    private final ConsentService consentService;

    @Override
    public List<PortalConnectionRes> getConnections(UUID userId) {
        List<PortalConnection> connections = portalRepository.findAllByUserId(userId);
        return connections.stream()
                .map(PortalConnectionRes::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PortalConnectionRes connectPortal(UUID userId, PortalConnectReq req) {
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 포털 지원 여부 확인
        PortalDataProvider provider;
        try {
            provider = providerFactory.getProvider(req.portalType());
        } catch (IllegalArgumentException e) {
            // 미지원 포털인 경우 UNSUPPORTED 상태로 저장
            PortalConnection connection = PortalConnection.create(user, req.portalType(), req.portalId());
            connection.markUnsupported();
            PortalConnection saved = portalRepository.save(connection);
            return PortalConnectionRes.from(saved);
        }

        // 포털 인증
        var authResult = provider.authenticate(req.credentials());
        
        if (!authResult.success()) {
            PortalConnection connection = PortalConnection.create(user, req.portalType(), req.portalId());
            connection.markFailed("AUTH_FAILED", "포털 인증에 실패했습니다.");
            PortalConnection saved = portalRepository.save(connection);
            return PortalConnectionRes.from(saved);
        }

        // PortalConnection 생성
        PortalConnection connection = PortalConnection.create(user, req.portalType(), req.portalId());
        connection.setPortalName(authResult.portalName());
        connection.setPortalUserId(authResult.portalUserId());
        connection.setCredentials(authResult.token());
        connection.markActive();

        PortalConnection saved = portalRepository.save(connection);

        // 동의 기록 생성
        Map<String, Object> scopeMap = new HashMap<>();
        scopeMap.put("dataTypes", List.of("checkup", "medical"));
        scopeMap.put("frequency", "monthly");
        scopeMap.put("retentionPeriod", "5years");

        consentService.grantConsent(userId, ConsentSubjectType.PORTAL, saved.getId(), scopeMap);

        // 최근 6개월 데이터 조회
        try {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusMonths(6);
            
            var checkupRecords = provider.getCheckupRecords(authResult.token(), startDate, endDate);
            var medicalRecords = provider.getMedicalRecords(authResult.token(), startDate, endDate);
            
            // TODO: 검진 결과 및 진료 기록 저장 로직 구현 필요
            log.debug("포털 데이터 조회 완료: 검진={}건, 진료={}건", checkupRecords.size(), medicalRecords.size());
            
            connection.markSynced();
            portalRepository.save(connection);
        } catch (Exception e) {
            log.warn("포털 데이터 조회 실패: {}", e.getMessage());
        }

        return PortalConnectionRes.from(saved);
    }

    @Override
    @Transactional
    public void disconnectPortal(UUID userId, UUID portalId) {
        PortalConnection connection = portalRepository.findByUserIdAndId(userId, portalId)
                .orElseThrow(() -> new ConsentNotFoundException("포털을 찾을 수 없습니다."));

        // 동의 철회
        consentRepository.findActiveConsent(userId, ConsentSubjectType.PORTAL, portalId)
                .ifPresent(consent -> {
                    consent.revoke("사용자 요청");
                    consentRepository.save(consent);
                });

        // 연동 해제
        connection.revoke();
        portalRepository.save(connection);
    }

    @Override
    @Transactional
    public SyncResultRes syncPortal(UUID userId, UUID portalId) {
        PortalConnection connection = portalRepository.findByUserIdAndId(userId, portalId)
                .orElseThrow(() -> new ConsentNotFoundException("포털을 찾을 수 없습니다."));

        if (connection.getStatus() != PortalStatus.ACTIVE) {
            throw new IllegalStateException("동기화할 수 없는 상태입니다.");
        }

        try {
            PortalDataProvider provider = providerFactory.getProvider(connection.getPortalType());
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = connection.getLastSyncAt() != null
                    ? connection.getLastSyncAt().toLocalDate()
                    : endDate.minusMonths(6);

            var checkupRecords = provider.getCheckupRecords(connection.getCredentials(), startDate, endDate);
            var medicalRecords = provider.getMedicalRecords(connection.getCredentials(), startDate, endDate);

            // TODO: 검진 결과 및 진료 기록 저장 로직 구현 필요

            connection.markSynced();
            portalRepository.save(connection);

            return new SyncResultRes(
                    checkupRecords.size() + medicalRecords.size(),
                    LocalDateTime.now(),
                    "SUCCESS",
                    Collections.emptyList()
            );
        } catch (Exception e) {
            log.error("포털 동기화 실패: portalId={}", portalId, e);
            connection.markFailed("SYNC_FAILED", e.getMessage());
            portalRepository.save(connection);

            return new SyncResultRes(
                    0,
                    LocalDateTime.now(),
                    "FAILED",
                    List.of(e.getMessage())
            );
        }
    }
}

