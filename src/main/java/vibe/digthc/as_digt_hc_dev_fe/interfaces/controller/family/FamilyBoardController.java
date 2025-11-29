package vibe.digthc.as_digt_hc_dev_fe.interfaces.controller.family;

import vibe.digthc.as_digt_hc_dev_fe.domain.family.dto.*;
import vibe.digthc.as_digt_hc_dev_fe.domain.family.service.FamilyBoardService;
import vibe.digthc.as_digt_hc_dev_fe.domain.family.service.InvitationService;
import vibe.digthc.as_digt_hc_dev_fe.infrastructure.security.CurrentUserId;
import vibe.digthc.as_digt_hc_dev_fe.interfaces.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/family-board")
@RequiredArgsConstructor
public class FamilyBoardController {

    private final FamilyBoardService familyBoardService;
    private final InvitationService invitationService;

    /**
     * 내 가족 보드 조회 (시니어인 경우 생성 포함)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<FamilyBoardRes>> getMyBoard(@CurrentUserId UUID userId) {
        FamilyBoardRes board = familyBoardService.getOrCreateBoard(userId);
        return ResponseEntity.ok(ApiResponse.success(board));
    }

    /**
     * 보드 멤버 목록 조회
     */
    @GetMapping("/members")
    public ResponseEntity<ApiResponse<List<MemberRes>>> getBoardMembers(@CurrentUserId UUID userId) {
        List<MemberRes> members = familyBoardService.getBoardMembers(userId);
        return ResponseEntity.ok(ApiResponse.success(members));
    }

    /**
     * 멤버 초대 (ADMIN 전용)
     */
    @PostMapping("/invite")
    public ResponseEntity<ApiResponse<InvitationRes>> inviteMember(
            @CurrentUserId UUID userId,
            @RequestBody @Valid InviteReq req) {
        InvitationRes invitation = invitationService.createInvitation(userId, req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("초대가 발송되었습니다.", invitation));
    }

    /**
     * 초대 수락
     */
    @PostMapping("/accept")
    public ResponseEntity<ApiResponse<MemberRes>> acceptInvitation(
            @CurrentUserId UUID userId,
            @RequestBody @Valid AcceptReq req) {
        MemberRes member = invitationService.acceptInvitation(userId, req.inviteCode());
        return ResponseEntity.ok(ApiResponse.success("가족 보드에 참여했습니다.", member));
    }

    /**
     * 멤버 역할 변경 (ADMIN 전용)
     */
    @PutMapping("/members/{memberId}/role")
    public ResponseEntity<ApiResponse<MemberRes>> updateMemberRole(
            @CurrentUserId UUID userId,
            @PathVariable UUID memberId,
            @RequestBody @Valid RoleUpdateReq req) {
        MemberRes member = familyBoardService.updateMemberRole(userId, memberId, req.newRole());
        return ResponseEntity.ok(ApiResponse.success("멤버 역할이 변경되었습니다.", member));
    }

    /**
     * 멤버 제거 (ADMIN 전용)
     */
    @DeleteMapping("/members/{memberId}")
    public ResponseEntity<ApiResponse<Void>> removeMember(
            @CurrentUserId UUID userId,
            @PathVariable UUID memberId) {
        familyBoardService.removeMember(userId, memberId);
        return ResponseEntity.ok(ApiResponse.success("멤버가 제거되었습니다."));
    }

    /**
     * 보드 설정 변경 (EDITOR+ 전용)
     */
    @PutMapping("/settings")
    public ResponseEntity<ApiResponse<FamilyBoardRes>> updateSettings(
            @CurrentUserId UUID userId,
            @RequestBody SettingsUpdateReq req) {
        FamilyBoardRes board = familyBoardService.updateBoardSettings(userId, req);
        return ResponseEntity.ok(ApiResponse.success("보드 설정이 변경되었습니다.", board));
    }
}

