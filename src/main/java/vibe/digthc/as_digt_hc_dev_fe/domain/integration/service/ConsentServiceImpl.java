package vibe.digthc.as_digt_hc_dev_fe.domain.integration.service;

import vibe.digthc.as_digt_hc_dev_fe.domain.integration.dto.ConsentRes;
import vibe.digthc.as_digt_hc_dev_fe.domain.integration.entity.ConsentRecord;
import vibe.digthc.as_digt_hc_dev_fe.domain.integration.entity.DeviceLink;
import vibe.digthc.as_digt_hc_dev_fe.domain.integration.entity.PortalConnection;
import vibe.digthc.as_digt_hc_dev_fe.domain.integration.enums.ConsentSubjectType;
import vibe.digthc.as_digt_hc_dev_fe.domain.integration.exception.ConsentNotFoundException;
import vibe.digthc.as_digt_hc_dev_fe.domain.integration.repository.ConsentRecordRepository;
import vibe.digthc.as_digt_hc_dev_fe.domain.integration.repository.DeviceLinkRepository;
import vibe.digthc.as_digt_hc_dev_fe.domain.integration.repository.PortalConnectionRepository;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.User;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConsentServiceImpl implements ConsentService {

    private final ConsentRecordRepository consentRepository;
    private final DeviceLinkRepository deviceRepository;
    private final PortalConnectionRepository portalRepository;
    private final UserRepository userRepository;

    @Override
    public List<ConsentRes> getConsents(UUID userId) {
        List<ConsentRecord> consents = consentRepository.findByUserIdOrderByConsentedAtDesc(userId);
        return consents.stream()
                .map(consent -> {
                    String subjectName = getSubjectName(consent.getSubjectType(), consent.getSubjectId());
                    return ConsentRes.from(consent, subjectName);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ConsentRes grantConsent(UUID userId, ConsentSubjectType subjectType, UUID subjectId, Map<String, Object> scope) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        ConsentRecord consent;
        switch (subjectType) {
            case DEVICE -> consent = ConsentRecord.grantDeviceConsent(user, subjectId, scope);
            case PORTAL -> consent = ConsentRecord.grantPortalConsent(user, subjectId, scope);
            case FAMILY_BOARD -> consent = ConsentRecord.grantFamilyBoardConsent(user, subjectId, scope);
            default -> throw new IllegalArgumentException("지원하지 않는 동의 타입입니다.");
        }

        ConsentRecord saved = consentRepository.save(consent);
        String subjectName = getSubjectName(subjectType, subjectId);
        return ConsentRes.from(saved, subjectName);
    }

    @Override
    @Transactional
    public void revokeConsent(UUID userId, UUID consentId, String reason) {
        ConsentRecord consent = consentRepository.findById(consentId)
                .filter(c -> c.getUser().getId().equals(userId))
                .orElseThrow(() -> new ConsentNotFoundException("동의를 찾을 수 없습니다."));

        consent.revoke(reason);
        consentRepository.save(consent);

        // 관련 연동 해제 처리
        if (consent.getSubjectType() == ConsentSubjectType.DEVICE) {
            deviceRepository.findById(consent.getSubjectId())
                    .ifPresent(device -> {
                        device.revoke();
                        deviceRepository.save(device);
                    });
        } else if (consent.getSubjectType() == ConsentSubjectType.PORTAL) {
            portalRepository.findById(consent.getSubjectId())
                    .ifPresent(portal -> {
                        portal.revoke();
                        portalRepository.save(portal);
                    });
        }
    }

    @Override
    public boolean checkConsent(UUID userId, ConsentSubjectType subjectType, UUID subjectId) {
        return consentRepository.existsActiveConsent(userId, subjectType, subjectId);
    }

    // ========================================
    // Private Methods
    // ========================================

    private String getSubjectName(ConsentSubjectType subjectType, UUID subjectId) {
        return switch (subjectType) {
            case DEVICE -> {
                Optional<DeviceLink> device = deviceRepository.findById(subjectId);
                yield device.map(d -> d.getVendor() + " " + d.getDeviceType())
                        .orElse("알 수 없는 디바이스");
            }
            case PORTAL -> {
                Optional<PortalConnection> portal = portalRepository.findById(subjectId);
                yield portal.map(PortalConnection::getPortalName)
                        .orElse("알 수 없는 포털");
            }
            case FAMILY_BOARD -> "가족 보드";
            default -> "알 수 없음";
        };
    }
}

