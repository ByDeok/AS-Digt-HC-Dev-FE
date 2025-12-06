package vibe.digthc.as_digt_hc_dev_fe.domain.family.service;

import vibe.digthc.as_digt_hc_dev_fe.domain.family.enums.BoardRole;
import vibe.digthc.as_digt_hc_dev_fe.domain.family.enums.FamilyBoardPermission;
import vibe.digthc.as_digt_hc_dev_fe.domain.family.exception.IllegalOperationException;
import vibe.digthc.as_digt_hc_dev_fe.domain.family.repository.FamilyBoardMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PermissionService {

    private final FamilyBoardMemberRepository memberRepository;

    /**
     * 권한 검증 (Exception 발생)
     */
    public void requirePermission(UUID userId, UUID boardId, FamilyBoardPermission permission) {
        if (!checkPermission(userId, boardId, permission)) {
            throw new IllegalOperationException("해당 작업을 수행할 권한이 없습니다: " + permission);
        }
    }

    /**
     * 권한 확인 (Boolean 반환)
     */
    public boolean checkPermission(UUID userId, UUID boardId, FamilyBoardPermission permission) {
        return memberRepository.findByBoardIdAndMemberId(boardId, userId)
                .map(member -> hasPermission(member, permission))
                .orElse(false);
    }

    private boolean hasPermission(FamilyBoardMember member, FamilyBoardPermission permission) {
        if (!member.isActive()) {
            return false;
        }

        BoardRole role = member.getBoardRole();

        // ADMIN은 모든 권한 보유
        if (role == BoardRole.ADMIN) {
            return true;
        }

        return switch (permission) {
            case MANAGE_MEMBERS -> false; // ADMIN only
            case EDIT_BOARD -> role == BoardRole.EDITOR;
            case VIEW_BOARD -> true; // All active members
        };
    }

    /**
     * 멤버의 현재 역할 조회
     */
    public BoardRole getMemberRole(UUID userId, UUID boardId) {
        return memberRepository.findByBoardIdAndMemberId(boardId, userId)
                .map(FamilyBoardMember::getBoardRole)
                .orElse(null);
    }
}
















