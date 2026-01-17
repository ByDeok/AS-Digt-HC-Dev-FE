package vibe.digthc.as_digt_hc_dev_fe.domain.family.service;

import vibe.digthc.as_digt_hc_dev_fe.domain.family.dto.FamilyBoardRes;
import vibe.digthc.as_digt_hc_dev_fe.domain.family.dto.MemberRes;
import vibe.digthc.as_digt_hc_dev_fe.domain.family.dto.SettingsUpdateReq;
import vibe.digthc.as_digt_hc_dev_fe.domain.family.enums.BoardRole;

import java.util.List;
import java.util.UUID;

public interface FamilyBoardService {
    FamilyBoardRes getOrCreateBoard(UUID seniorId);
    FamilyBoardRes getBoardBySenior(UUID seniorId);
    FamilyBoardRes getBoardByMember(UUID memberId);
    List<MemberRes> getBoardMembers(UUID userId);
    MemberRes updateMemberRole(UUID userId, UUID memberId, BoardRole role);
    void removeMember(UUID userId, UUID memberId);
    FamilyBoardRes updateBoardSettings(UUID userId, SettingsUpdateReq req);
}





































