# [BE] Issue-07: 가족 보드(Family Board) 및 권한 관리 API 명세서

## 📋 개요

**SRS REQ-FUNC-015~018**에 따라 시니어와 보호자 간의 데이터 공유, 역할 위임, 초대 기능을 위한 API 명세서입니다.

**기술 스택**: Java 17+, Spring Boot 3.4+, MySQL 9.x, Jakarta EE 10

---

## 👨‍👩‍👧 1. 내 가족 보드 조회 (Get My Board)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `GET /api/v1/family-board` |
| **설명** | 현재 사용자가 참여 중인 가족 보드 조회 (시니어/보호자 모두) |
| **인증** | `Bearer Token` 필수 |
| **난이도** | **하 (Low)** |

### Request Header

| 헤더명 | 값 | 필수 |
|:---|:---|:---:|
| `Authorization` | `Bearer {accessToken}` | ✅ |

### Response Body

**✅ 성공 (200 OK)**

```json
{
  "success": true,
  "message": "조회 성공",
  "data": {
    "boardId": "550e8400-e29b-41d4-a716-446655440000",
    "name": "홍길동님의 가족 보드",
    "description": "가족과 함께 건강 정보를 공유하세요",
    "senior": {
      "userId": "550e8400-e29b-41d4-a716-446655440000",
      "name": "홍길동",
      "email": "senior@example.com"
    },
    "memberCount": 3,
    "myRole": "ADMIN",
    "lastActivityAt": "2025-01-15T14:30:00"
  }
}
```

**❌ 실패**

| HTTP | type | title | 원인 |
|:---|:---|:---|:---|
| `404` | `/errors/board-not-found` | 보드 없음 | 참여 중인 가족 보드 없음 |

### Logic Steps

```
1. [Find Board] 사용자의 가족 보드 조회
   - 시니어인 경우: FamilyBoardRepository.findBySeniorId()
   - 멤버인 경우: FamilyBoardRepository.findByActiveMemberId()

2. [Response] 보드 정보 반환
   - FamilyBoardRes.from(board)
```

---

## 👥 2. 보드 멤버 목록 조회 (Get Board Members)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `GET /api/v1/family-board/members` |
| **설명** | 가족 보드의 멤버 목록 조회 |
| **인증** | `Bearer Token` 필수 |
| **권한** | 모든 역할 (VIEWER, EDITOR, ADMIN) |
| **난이도** | **하 (Low)** |

### Request Header

| 헤더명 | 값 | 필수 |
|:---|:---|:---:|
| `Authorization` | `Bearer {accessToken}` | ✅ |

### Response Body

**✅ 성공 (200 OK)**

```json
{
  "success": true,
  "message": "조회 성공",
  "data": [
    {
      "membershipId": 1,
      "user": {
        "userId": "550e8400-e29b-41d4-a716-446655440000",
        "name": "홍길동",
        "email": "senior@example.com"
      },
      "role": "ADMIN",
      "status": "ACTIVE",
      "joinedAt": "2025-01-10T09:00:00"
    },
    {
      "membershipId": 2,
      "user": {
        "userId": "660e8400-e29b-41d4-a716-446655440001",
        "name": "홍길순",
        "email": "caregiver@example.com"
      },
      "role": "VIEWER",
      "status": "ACTIVE",
      "joinedAt": "2025-01-12T10:00:00"
    }
  ]
}
```

### Logic Steps

```
1. [Find Board] 사용자의 가족 보드 조회

2. [Find Members] 보드 멤버 목록 조회
   - FamilyBoardMemberRepository.findActiveMembersByBoardId()

3. [Response] 멤버 목록 반환
   - MemberRes.from() 변환
```

---

## 📧 3. 멤버 초대 (Invite Member)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `POST /api/v1/family-board/invite` |
| **설명** | 가족 보드에 새 멤버 초대 (초대 코드 생성) |
| **인증** | `Bearer Token` 필수 |
| **권한** | `ADMIN` 역할 필수 |
| **난이도** | **중 (Medium)** |

### Request Header

| 헤더명 | 값 | 필수 |
|:---|:---|:---:|
| `Authorization` | `Bearer {accessToken}` | ✅ |

### Request Body

| 필드명 | 타입 | 필수 | 설명 | Validation |
|:---|:---|:---:|:---|:---|
| `inviteeEmail` | `String` | ✅ | 초대받을 사용자 이메일 | `@Email`, `@NotBlank` |
| `intendedRole` | `Enum` | ✅ | 예정 역할 | `VIEWER`, `EDITOR` (ADMIN 불가) |

#### Request Body 예시

```json
{
  "inviteeEmail": "caregiver@example.com",
  "intendedRole": "VIEWER"
}
```

### Response Body

**✅ 성공 (201 Created)**

```json
{
  "success": true,
  "message": "초대가 발송되었습니다.",
  "data": {
    "invitationId": "550e8400-e29b-41d4-a716-446655440000",
    "inviteCode": "ABC12345",
    "inviteeEmail": "caregiver@example.com",
    "intendedRole": "VIEWER",
    "status": "PENDING",
    "expiresAt": "2025-01-22T10:00:00"
  }
}
```

**❌ 실패**

| HTTP | type | title | 원인 |
|:---|:---|:---|:---|
| `403` | `/errors/access-denied` | 권한 없음 | ADMIN 역할이 아님 |
| `400` | `/errors/invalid-role` | 잘못된 역할 | ADMIN 역할은 초대 불가 |
| `409` | `/errors/member-exists` | 멤버 존재 | 이미 보드 멤버인 사용자 |

### Logic Steps

```
1. [Find Board] 사용자의 가족 보드 조회

2. [Check Permission] 초대 권한 확인
   - PermissionService.canInvite(userId, boardId)
   - ADMIN만 초대 가능

3. [Check Existing] 기존 멤버 확인
   - FamilyBoardMemberRepository.existsByBoardIdAndMemberId()
   - 이미 멤버인 경우 409 반환

4. [Generate Code] 초대 코드 생성
   - 8자리 랜덤 코드 생성
   - 유니크 확인 후 저장

5. [Create Invitation] 초대 생성
   - BoardInvitation.create()
   - expiresAt = now + 7일

6. [Send Email] 초대 이메일 발송
   - NotificationService.sendInvitationEmail()

7. [Save] 초대 저장
   - BoardInvitationRepository.save(invitation)

8. [Response] 초대 정보 반환
```

---

## ✅ 4. 초대 수락 (Accept Invitation)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `POST /api/v1/family-board/accept` |
| **설명** | 초대 코드로 가족 보드 초대 수락 |
| **인증** | `Bearer Token` 필수 |
| **난이도** | **중 (Medium)** |

### Request Header

| 헤더명 | 값 | 필수 |
|:---|:---|:---:|
| `Authorization` | `Bearer {accessToken}` | ✅ |

### Request Body

| 필드명 | 타입 | 필수 | 설명 | Validation |
|:---|:---|:---:|:---|:---|
| `inviteCode` | `String` | ✅ | 초대 코드 | `@NotBlank`, 8자리 |

#### Request Body 예시

```json
{
  "inviteCode": "ABC12345"
}
```

### Response Body

**✅ 성공 (200 OK)**

```json
{
  "success": true,
  "message": "초대가 수락되었습니다.",
  "data": {
    "membershipId": 2,
    "user": {
      "userId": "660e8400-e29b-41d4-a716-446655440001",
      "name": "홍길순",
      "email": "caregiver@example.com"
    },
    "role": "VIEWER",
    "status": "ACTIVE",
    "joinedAt": "2025-01-15T10:00:00"
  }
}
```

**❌ 실패**

| HTTP | type | title | 원인 |
|:---|:---|:---|:---|
| `400` | `/errors/invalid-invitation` | 유효하지 않은 초대 | 만료/이미 수락/거절된 초대 |
| `404` | `/errors/invitation-not-found` | 초대 없음 | 초대 코드가 존재하지 않음 |
| `409` | `/errors/member-exists` | 멤버 존재 | 이미 보드 멤버인 사용자 |

### Logic Steps

```
1. [Find Invitation] 초대 코드로 초대 조회
   - BoardInvitationRepository.findByInviteCode()

2. [Validate Invitation] 초대 유효성 검증
   - invitation.isValid() 확인
   - 만료 여부, 상태 확인

3. [Check Member] 기존 멤버 확인
   - 이미 멤버인 경우 409 반환

4. [Accept Invitation] 초대 수락 처리
   - invitation.accept()

5. [Create Member] 보드 멤버 생성
   - FamilyBoardMember.createMember()
   - role = invitation.intendedRole

6. [Save] 초대 및 멤버 저장
   - BoardInvitationRepository.save(invitation)
   - FamilyBoardMemberRepository.save(member)

7. [Response] 멤버 정보 반환
```

---

## 🔄 5. 멤버 역할 변경 (Update Member Role)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `PUT /api/v1/family-board/members/{memberId}/role` |
| **설명** | 보드 멤버의 역할 변경 (VIEWER ↔ EDITOR) |
| **인증** | `Bearer Token` 필수 |
| **권한** | `ADMIN` 역할 필수 |
| **난이도** | **중 (Medium)** |

### Request Header

| 헤더명 | 값 | 필수 |
|:---|:---|:---:|
| `Authorization` | `Bearer {accessToken}` | ✅ |

### Path Parameters

| 파라미터명 | 타입 | 필수 | 설명 |
|:---|:---|:---:|:---|
| `memberId` | `UUID` | ✅ | 멤버 사용자 ID |

### Request Body

| 필드명 | 타입 | 필수 | 설명 | Validation |
|:---|:---|:---:|:---|:---|
| `newRole` | `Enum` | ✅ | 새 역할 | `VIEWER`, `EDITOR` (ADMIN 불가) |

#### Request Body 예시

```json
{
  "newRole": "EDITOR"
}
```

### Response Body

**✅ 성공 (200 OK)**

```json
{
  "success": true,
  "message": "역할이 변경되었습니다.",
  "data": {
    "membershipId": 2,
    "user": {
      "userId": "660e8400-e29b-41d4-a716-446655440001",
      "name": "홍길순"
    },
    "role": "EDITOR",
    "status": "ACTIVE",
    "joinedAt": "2025-01-12T10:00:00"
  }
}
```

**❌ 실패**

| HTTP | type | title | 원인 |
|:---|:---|:---|:---|
| `403` | `/errors/access-denied` | 권한 없음 | ADMIN 역할이 아님 |
| `400` | `/errors/invalid-role` | 잘못된 역할 | ADMIN 역할은 변경 불가 |
| `400` | `/errors/cannot-change-self` | 자기 자신 변경 불가 | 자신의 ADMIN 역할 변경 불가 |
| `404` | `/errors/member-not-found` | 멤버 없음 | 멤버를 찾을 수 없음 |

### Logic Steps

```
1. [Find Board] 사용자의 가족 보드 조회

2. [Check Permission] 권한 확인
   - PermissionService.requirePermission(userId, boardId, MANAGE_MEMBERS)
   - ADMIN만 가능

3. [Find Member] 대상 멤버 조회
   - FamilyBoardMemberRepository.findByBoardIdAndMemberId()

4. [Validate Change] 역할 변경 유효성 확인
   - 자기 자신의 ADMIN 역할 변경 불가
   - ADMIN 역할로 변경 불가

5. [Change Role] 역할 변경
   - member.changeRole(newRole)

6. [Update Activity] 보드 활동 시간 갱신
   - board.updateLastActivity()

7. [Save] 멤버 및 보드 저장

8. [Log Activity] 활동 로그 기록
   - BoardActivityLogService.log(ROLE_CHANGED, ...)

9. [Response] 변경된 멤버 정보 반환
```

---

## 🗑️ 6. 멤버 제거 (Remove Member)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `DELETE /api/v1/family-board/members/{memberId}` |
| **설명** | 가족 보드에서 멤버 제거 |
| **인증** | `Bearer Token` 필수 |
| **권한** | `ADMIN` 역할 필수 |
| **난이도** | **하 (Low)** |

### Request Header

| 헤더명 | 값 | 필수 |
|:---|:---|:---:|
| `Authorization` | `Bearer {accessToken}` | ✅ |

### Path Parameters

| 파라미터명 | 타입 | 필수 | 설명 |
|:---|:---|:---:|:---|
| `memberId` | `UUID` | ✅ | 멤버 사용자 ID |

### Response Body

**✅ 성공 (204 No Content)**

**❌ 실패**

| HTTP | type | title | 원인 |
|:---|:---|:---|:---|
| `403` | `/errors/access-denied` | 권한 없음 | ADMIN 역할이 아님 |
| `400` | `/errors/cannot-remove-owner` | 소유자 제거 불가 | 시니어(보드 소유자)는 제거 불가 |
| `404` | `/errors/member-not-found` | 멤버 없음 | 멤버를 찾을 수 없음 |

### Logic Steps

```
1. [Find Board] 사용자의 가족 보드 조회

2. [Check Permission] 권한 확인
   - PermissionService.requirePermission(userId, boardId, MANAGE_MEMBERS)

3. [Find Member] 대상 멤버 조회

4. [Validate Removal] 제거 유효성 확인
   - 시니어(보드 소유자)는 제거 불가

5. [Remove Member] 멤버 제거 처리
   - member.remove()
   - status = REMOVED

6. [Update Activity] 보드 활동 시간 갱신

7. [Save] 멤버 및 보드 저장

8. [Log Activity] 활동 로그 기록

9. [Response] 204 No Content 반환
```

---

## 📅 7. 보드 이벤트 목록 조회 (Get Events)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `GET /api/v1/family-board/events` |
| **설명** | 가족 보드의 이벤트(일정, 알림) 목록 조회 |
| **인증** | `Bearer Token` 필수 |
| **권한** | 모든 역할 (VIEWER, EDITOR, ADMIN) |
| **난이도** | **하 (Low)** |

### Request Header

| 헤더명 | 값 | 필수 |
|:---|:---|:---:|
| `Authorization` | `Bearer {accessToken}` | ✅ |

### Query Parameters

| 파라미터명 | 타입 | 필수 | 설명 |
|:---|:---|:---:|:---|
| `start` | `LocalDate` | ❌ | 시작 날짜 (기본값: 오늘) |
| `end` | `LocalDate` | ❌ | 종료 날짜 (기본값: 30일 후) |

### Response Body

**✅ 성공 (200 OK)**

```json
{
  "success": true,
  "message": "조회 성공",
  "data": [
    {
      "eventId": 1,
      "eventType": "MEDICATION",
      "title": "오전 약 복용",
      "description": "고혈압 약을 복용하세요",
      "eventTime": "2025-01-15T09:00:00",
      "isRecurring": true,
      "status": "ACTIVE"
    },
    {
      "eventId": 2,
      "eventType": "CHECKUP",
      "title": "정기 검진 예약",
      "description": "내과 정기 검진",
      "eventTime": "2025-01-20T14:00:00",
      "isRecurring": false,
      "status": "ACTIVE"
    }
  ]
}
```

### Logic Steps

```
1. [Find Board] 사용자의 가족 보드 조회

2. [Find Events] 이벤트 목록 조회
   - BoardEventRepository.findByBoardIdAndEventTimeBetween()

3. [Response] 이벤트 목록 반환
   - EventRes.from() 변환
```

---

## ➕ 8. 이벤트 생성 (Create Event)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `POST /api/v1/family-board/events` |
| **설명** | 가족 보드에 이벤트(일정, 알림) 생성 |
| **인증** | `Bearer Token` 필수 |
| **권한** | `EDITOR`, `ADMIN` 역할 필수 |
| **난이도** | **중 (Medium)** |

### Request Header

| 헤더명 | 값 | 필수 |
|:---|:---|:---:|
| `Authorization` | `Bearer {accessToken}` | ✅ |

### Request Body

| 필드명 | 타입 | 필수 | 설명 | Validation |
|:---|:---|:---:|:---|:---|
| `eventType` | `Enum` | ✅ | 이벤트 타입 | `SCHEDULE`, `MEDICATION`, `CHECKUP`, `ALERT` |
| `title` | `String` | ✅ | 이벤트 제목 | `@NotBlank`, max 100자 |
| `description` | `String` | ❌ | 이벤트 설명 | max 500자 |
| `eventTime` | `LocalDateTime` | ✅ | 이벤트 시각 | `@NotNull`, `@Future` |
| `isRecurring` | `Boolean` | ❌ | 반복 여부 | 기본값: false |
| `recurrenceRule` | `Object` | ❌ | 반복 규칙 | isRecurring이 true일 때 필수 |

#### Request Body 예시

```json
{
  "eventType": "MEDICATION",
  "title": "오전 약 복용",
  "description": "고혈압 약을 복용하세요",
  "eventTime": "2025-01-15T09:00:00",
  "isRecurring": true,
  "recurrenceRule": {
    "frequency": "DAILY",
    "interval": 1,
    "endDate": "2025-12-31"
  }
}
```

### Response Body

**✅ 성공 (201 Created)**

```json
{
  "success": true,
  "message": "이벤트가 생성되었습니다.",
  "data": {
    "eventId": 1,
    "eventType": "MEDICATION",
    "title": "오전 약 복용",
    "description": "고혈압 약을 복용하세요",
    "eventTime": "2025-01-15T09:00:00",
    "isRecurring": true,
    "status": "ACTIVE"
  }
}
```

**❌ 실패**

| HTTP | type | title | 원인 |
|:---|:---|:---|:---|
| `403` | `/errors/access-denied` | 권한 없음 | VIEWER는 이벤트 생성 불가 |
| `400` | `/errors/validation` | 입력값 검증 실패 | 잘못된 형식의 데이터 |

### Logic Steps

```
1. [Find Board] 사용자의 가족 보드 조회

2. [Check Permission] 권한 확인
   - PermissionService.requirePermission(userId, boardId, EDIT_EVENTS)
   - EDITOR, ADMIN만 가능

3. [Create Event] 이벤트 생성
   - BoardEvent.create(board, userId, eventType, ...)

4. [Save] 이벤트 저장
   - BoardEventRepository.save(event)

5. [Update Activity] 보드 활동 시간 갱신

6. [Log Activity] 활동 로그 기록

7. [Response] 생성된 이벤트 정보 반환
```

---

## 📊 API 요약 테이블

| # | Method | Endpoint | 설명 | 인증 | 권한 | 난이도 |
|:---:|:---|:---|:---|:---:|:---:|:---:|
| 1 | `GET` | `/api/v1/family-board` | 내 가족 보드 조회 | ✅ | ALL | 하 |
| 2 | `GET` | `/api/v1/family-board/members` | 멤버 목록 조회 | ✅ | ALL | 하 |
| 3 | `POST` | `/api/v1/family-board/invite` | 멤버 초대 | ✅ | ADMIN | 중 |
| 4 | `POST` | `/api/v1/family-board/accept` | 초대 수락 | ✅ | - | 중 |
| 5 | `PUT` | `/api/v1/family-board/members/{id}/role` | 역할 변경 | ✅ | ADMIN | 중 |
| 6 | `DELETE` | `/api/v1/family-board/members/{id}` | 멤버 제거 | ✅ | ADMIN | 하 |
| 7 | `GET` | `/api/v1/family-board/events` | 이벤트 목록 조회 | ✅ | ALL | 하 |
| 8 | `POST` | `/api/v1/family-board/events` | 이벤트 생성 | ✅ | EDITOR+ | 중 |

---

## 🏗️ 구현 참고사항

### 1. 권한 검증 서비스 예시

```java
@Service
@RequiredArgsConstructor
public class PermissionService {
    
    private final FamilyBoardMemberRepository memberRepository;
    
    public void requirePermission(UUID userId, UUID boardId, Permission permission) {
        FamilyBoardMember member = memberRepository
            .findByBoardIdAndMemberId(boardId, userId)
            .orElseThrow(() -> new AccessDeniedException("보드 멤버가 아닙니다."));
        
        if (!member.isActive()) {
            throw new AccessDeniedException("비활성 멤버입니다.");
        }
        
        if (!hasPermission(member.getBoardRole(), permission)) {
            throw new AccessDeniedException("권한이 없습니다.");
        }
    }
    
    private boolean hasPermission(BoardRole role, Permission permission) {
        return switch (role) {
            case ADMIN -> true; // 모든 권한
            case EDITOR -> permission != Permission.MANAGE_MEMBERS;
            case VIEWER -> permission == Permission.VIEW_BOARD;
        };
    }
}
```

### 2. DTO 예시

```java
public record InviteReq(
    @Email @NotBlank String inviteeEmail,
    @NotNull BoardRole intendedRole
) {}

public record AcceptReq(
    @NotBlank @Size(min = 8, max = 8) String inviteCode
) {}

public record RoleUpdateReq(
    @NotNull BoardRole newRole
) {}

public record FamilyBoardRes(
    UUID boardId,
    String name,
    String description,
    UserSummaryRes senior,
    int memberCount,
    BoardRole myRole,
    LocalDateTime lastActivityAt
) {
    public static FamilyBoardRes from(FamilyBoard board, UUID userId) {
        return new FamilyBoardRes(
            board.getId(),
            board.getName(),
            board.getDescription(),
            UserSummaryRes.from(board.getSenior()),
            board.getActiveMemberCount(),
            getMyRole(board, userId),
            board.getLastActivityAt()
        );
    }
}
```

---

## 📝 참고 자료

- [BE] Issue-07: 가족 보드(Family Board) 및 권한 관리 구현 (`studio/Tasks/BE_issue/issue-07-family-board.md`)
- SRS REQ-FUNC-015~018
- SRS 6.2.5 FamilyBoard & AccessRole
- Java Spring Boot 3.x Cursor Rules (`.cursor/rules/300-java-spring-cursor-rules.mdc`)

