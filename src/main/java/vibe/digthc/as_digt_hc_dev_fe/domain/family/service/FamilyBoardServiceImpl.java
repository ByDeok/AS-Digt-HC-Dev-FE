package vibe.digthc.as_digt_hc_dev_fe.domain.family.service;

import vibe.digthc.as_digt_hc_dev_fe.domain.family.dto.FamilyBoardRes;
import vibe.digthc.as_digt_hc_dev_fe.domain.family.dto.MemberRes;
import vibe.digthc.as_digt_hc_dev_fe.domain.family.dto.SettingsUpdateReq;
import vibe.digthc.as_digt_hc_dev_fe.domain.family.entity.FamilyBoard;
import vibe.digthc.as_digt_hc_dev_fe.domain.family.entity.FamilyBoardMember;
import vibe.digthc.as_digt_hc_dev_fe.domain.family.enums.BoardRole;
import vibe.digthc.as_digt_hc_dev_fe.domain.family.enums.FamilyBoardPermission;
import vibe.digthc.as_digt_hc_dev_fe.domain.family.exception.BoardNotFoundException;
import vibe.digthc.as_digt_hc_dev_fe.domain.family.exception.IllegalOperationException;
import vibe.digthc.as_digt_hc_dev_fe.domain.family.exception.MemberNotFoundException;
import vibe.digthc.as_digt_hc_dev_fe.domain.family.repository.FamilyBoardMemberRepository;
import vibe.digthc.as_digt_hc_dev_fe.domain.family.repository.FamilyBoardRepository;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.entity.User;
import vibe.digthc.as_digt_hc_dev_fe.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 가족 보드 서비스 구현체
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FamilyBoardServiceImpl implements FamilyBoardService {

    private final FamilyBoardRepository boardRepository;
    private final FamilyBoardMemberRepository memberRepository;
    private final UserRepository userRepository;
    private final PermissionService permissionService;

    /**
     * 시니어의 가족 보드 조회 또는 생성
     */
    @Override
    @Transactional
    public FamilyBoardRes getOrCreateBoard(UUID seniorId) {
        return boardRepository.findBySeniorId(seniorId)
                .map(FamilyBoardRes::from)
                .orElseGet(() -> createBoardForSenior(seniorId));
    }

    /**
     * 시니어 ID로 보드 조회 (없으면 예외)
     */
    @Override
    public FamilyBoardRes getBoardBySenior(UUID seniorId) {
        FamilyBoard board = boardRepository.findBySeniorId(seniorId)
                .orElseThrow(() -> new BoardNotFoundException("가족 보드를 찾을 수 없습니다."));
        return FamilyBoardRes.from(board);
    }

    /**
     * 멤버의 가족 보드 조회
     */
    @Override
    public FamilyBoardRes getBoardByMember(UUID memberId) {
        FamilyBoard board = boardRepository.findByActiveMemberId(memberId)
                .orElseThrow(() -> new BoardNotFoundException("참여 중인 가족 보드가 없습니다."));
        return FamilyBoardRes.from(board);
    }

    /**
     * 보드 멤버 목록 조회
     */
    @Override
    public List<MemberRes> getBoardMembers(UUID userId) {
        FamilyBoard board = findBoardByUser(userId);
        
        // 권한 검증 (조회 권한)
        permissionService.requirePermission(userId, board.getId(), FamilyBoardPermission.VIEW_BOARD);
        
        List<FamilyBoardMember> members = memberRepository
                .findActiveMembersByBoardId(board.getId());
        
        return members.stream()
                .map(MemberRes::from)
                .toList();
    }

    /**
     * 멤버 역할 변경
     */
    @Override
    @Transactional
    public MemberRes updateMemberRole(UUID userId, UUID memberId, BoardRole newRole) {
        FamilyBoard board = findBoardByUser(userId);
        
        // 권한 검증 (ADMIN만 가능)
        permissionService.requirePermission(userId, board.getId(), FamilyBoardPermission.MANAGE_MEMBERS);
        
        FamilyBoardMember member = memberRepository
                .findByBoardIdAndMemberId(board.getId(), memberId)
                .orElseThrow(() -> new MemberNotFoundException("멤버를 찾을 수 없습니다."));
        
        // 자기 자신의 ADMIN 역할은 변경 불가
        if (member.getMember().getId().equals(userId) && member.isAdmin()) {
            throw new IllegalOperationException("자신의 ADMIN 역할은 변경할 수 없습니다.");
        }
        
        member.changeRole(newRole);
        memberRepository.save(member);
        
        board.updateLastActivity();
        boardRepository.save(board);
        
        return MemberRes.from(member);
    }

    /**
     * 멤버 제거
     */
    @Override
    @Transactional
    public void removeMember(UUID userId, UUID memberId) {
        FamilyBoard board = findBoardByUser(userId);
        
        // 권한 검증
        permissionService.requirePermission(userId, board.getId(), FamilyBoardPermission.MANAGE_MEMBERS);
        
        FamilyBoardMember member = memberRepository
                .findByBoardIdAndMemberId(board.getId(), memberId)
                .orElseThrow(() -> new MemberNotFoundException("멤버를 찾을 수 없습니다."));
        
        // 시니어(보드 소유자)는 제거 불가
        if (board.getSenior().getId().equals(memberId)) {
            throw new IllegalOperationException("보드 소유자는 제거할 수 없습니다.");
        }
        
        member.remove();
        memberRepository.save(member);
        
        board.updateLastActivity();
        boardRepository.save(board);
    }

    /**
     * 보드 설정 변경
     */
    @Override
    @Transactional
    public FamilyBoardRes updateBoardSettings(UUID userId, SettingsUpdateReq req) {
        FamilyBoard board = findBoardByUser(userId);
        
        // 권한 검증 (편집 권한 필요)
        permissionService.requirePermission(userId, board.getId(), FamilyBoardPermission.EDIT_BOARD);
        
        board.updateSettings(req.settings());
        
        return FamilyBoardRes.from(board);
    }

    // ========================================
    // Private Methods
    // ========================================

    private FamilyBoardRes createBoardForSenior(UUID seniorId) {
        User senior = userRepository.findById(seniorId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다.")); // UserNotFoundException 필요
        
        // 보드 생성
        FamilyBoard board = FamilyBoard.create(senior);
        FamilyBoard savedBoard = boardRepository.save(board);
        
        // 시니어를 ADMIN으로 등록
        FamilyBoardMember adminMember = FamilyBoardMember.createAdmin(savedBoard, senior);
        memberRepository.save(adminMember);
        
        return FamilyBoardRes.from(savedBoard);
    }

    private FamilyBoard findBoardByUser(UUID userId) {
        // 먼저 시니어로 조회
        return boardRepository.findBySeniorId(userId)
                .orElseGet(() -> 
                    // 시니어가 아니면 멤버로 조회
                    boardRepository.findByActiveMemberId(userId)
                            .orElseThrow(() -> new BoardNotFoundException("가족 보드를 찾을 수 없습니다."))
                );
    }
}
















