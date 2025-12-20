package vibe.digthc.as_digt_hc_dev_fe.domain.family.service;

import vibe.digthc.as_digt_hc_dev_fe.domain.family.dto.InvitationRes;
import vibe.digthc.as_digt_hc_dev_fe.domain.family.dto.InviteReq;
import vibe.digthc.as_digt_hc_dev_fe.domain.family.dto.MemberRes;
import vibe.digthc.as_digt_hc_dev_fe.domain.family.entity.BoardInvitation;
import vibe.digthc.as_digt_hc_dev_fe.domain.family.entity.FamilyBoard;
import vibe.digthc.as_digt_hc_dev_fe.domain.family.entity.FamilyBoardMember;
import vibe.digthc.as_digt_hc_dev_fe.domain.family.enums.FamilyBoardPermission;
import vibe.digthc.as_digt_hc_dev_fe.domain.family.exception.BoardNotFoundException;
import vibe.digthc.as_digt_hc_dev_fe.domain.family.exception.IllegalOperationException;
import vibe.digthc.as_digt_hc_dev_fe.domain.family.exception.InvalidInvitationException;
import vibe.digthc.as_digt_hc_dev_fe.domain.family.repository.BoardInvitationRepository;
import vibe.digthc.as_digt_hc_dev_fe.domain.family.repository.FamilyBoardMemberRepository;
import vibe.digthc.as_digt_hc_dev_fe.domain.family.repository.FamilyBoardRepository;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.User;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InvitationService {

    private final BoardInvitationRepository invitationRepository;
    private final FamilyBoardRepository boardRepository;
    private final FamilyBoardMemberRepository memberRepository;
    private final UserRepository userRepository;
    private final PermissionService permissionService;

    /**
     * 초대 생성
     */
    @Transactional
    public InvitationRes createInvitation(UUID userId, InviteReq req) {
        FamilyBoard board = findBoardByUser(userId);
        
        // 권한 검증
        permissionService.requirePermission(userId, board.getId(), FamilyBoardPermission.MANAGE_MEMBERS);
        
        User inviter = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        String code = generateInviteCode();
        
        BoardInvitation invitation = BoardInvitation.create(
            board, inviter, code, req.inviteeEmail(), req.intendedRole()
        );
        
        invitationRepository.save(invitation);
        
        // TODO: 이메일 발송 로직 연동 (NotificationService)
        
        return InvitationRes.from(invitation);
    }

    /**
     * 초대 수락
     */
    @Transactional
    public MemberRes acceptInvitation(UUID userId, String inviteCode) {
        BoardInvitation invitation = invitationRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new InvalidInvitationException("유효하지 않은 초대 코드입니다."));
        
        if (!invitation.isValid()) {
            throw new InvalidInvitationException("만료되었거나 이미 처리된 초대입니다.");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        // 이미 멤버인지 확인
        if (memberRepository.findByBoardIdAndMemberId(invitation.getBoard().getId(), userId).isPresent()) {
             throw new IllegalOperationException("이미 보드의 멤버입니다.");
        }

        // 초대 상태 변경
        invitation.accept();
        
        // 멤버 생성
        FamilyBoardMember member = FamilyBoardMember.createMember(
            invitation.getBoard(), user, invitation.getIntendedRole()
        );
        
        memberRepository.save(member);
        
        return MemberRes.from(member);
    }

    /**
     * 초대 코드로 정보 조회
     */
    public InvitationRes getInvitationByCode(String code) {
        BoardInvitation invitation = invitationRepository.findByInviteCode(code)
                .orElseThrow(() -> new InvalidInvitationException("초대를 찾을 수 없습니다."));
        return InvitationRes.from(invitation);
    }

    private String generateInviteCode() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[4]; // 8 hex chars
        random.nextBytes(bytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    
    private FamilyBoard findBoardByUser(UUID userId) {
        return boardRepository.findBySeniorId(userId)
                .orElseGet(() -> 
                    boardRepository.findByActiveMemberId(userId)
                            .orElseThrow(() -> new BoardNotFoundException("가족 보드를 찾을 수 없습니다."))
                );
    }
}

































