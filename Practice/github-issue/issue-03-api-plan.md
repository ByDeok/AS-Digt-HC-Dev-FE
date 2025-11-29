`studio/Tasks/BE_issue/issue-03-auth-user.md`의 요구사항을 바탕으로, **이메일 기반 회원가입(Sign-up)** 기능을 위한 상세 API 명세서를 작성했습니다.

프로젝트의 개발 가이드라인(Java Spring Boot, JPA, Security)을 고려하여 작성하였으며, 바로 이슈에 업데이트하거나 위키에 등록할 수 있는 포맷입니다.

---

### 📝 [API Spec] 이메일 회원가입 (Email Sign-up)

#### 1. 기본 정보 (Basic Info)
| 항목 | 내용 |
| :--- | :--- |
| **API 명** | 사용자 이메일 회원가입 |
| **Endpoint** | `POST /api/v1/auth/signup` |
| **설명** | 새로운 사용자가 이메일과 비밀번호를 사용하여 서비스에 가입합니다. |
| **인증 필요 여부** | `Anonymous` (인증 없이 접근 가능) |
| **난이도** | **하 (Low)** - 표준적인 CRUD 및 암호화 로직 |

#### 2. Request Body (입력 데이터)
`Content-Type: application/json`

| 필드명 | 타입 | 필수 | 설명 | 유효성 검사 (Validation) |
| :--- | :--- | :---: | :--- | :--- |
| `email` | String | Y | 사용자 이메일 (ID 역할) | `@Email` 형식 준수, `@NotBlank` |
| `password` | String | Y | 사용자 비밀번호 | `@Pattern`: 8~20자, 영문/숫자/특수문자 조합 |
| `name` | String | Y | 사용자 실명 또는 닉네임 | `@Length(min=2, max=20)` |
| `role` | Enum | N | 사용자 권한 | 기본값: `USER` (또는 `SENIOR`/`CAREGIVER` 허용 시 유효값 검사) |
| `agreements`| Object | Y | 약관 동의 정보 | 필수 약관 동의 여부 체크 |
| └ `termsService` | Boolean | Y | 서비스 이용약관 동의 | `true` 필수 |
| └ `privacyPolicy` | Boolean | Y | 개인정보 처리방침 동의 | `true` 필수 |

#### 3. Response Body (응답 데이터)
**성공 시 (HTTP 201 Created)**
```json
{
  "status": "SUCCESS",
  "message": "회원가입이 완료되었습니다.",
  "data": {
    "userId": 101,
    "email": "user@example.com",
    "name": "홍길동",
    "createdAt": "2023-10-27T10:00:00"
  }
}
```

**실패 시 (예시)**
| HTTP Code | Error Code | 메시지 | 원인 |
| :--- | :--- | :--- | :--- |
| `400` | `ERR_VALIDATION` | "비밀번호 형식이 올바르지 않습니다." | Request Body 유효성 검사 실패 |
| `409` | `ERR_EMAIL_DUPLICATE` | "이미 가입된 이메일입니다." | 중복된 이메일로 가입 시도 |

#### 4. 로직 처리 순서 (Logic Steps)
1.  **DTO Validation**:
    *   Request Body의 필드 형식 검사 (Email 포맷, Password 복잡도 등).
    *   실패 시 `MethodArgumentNotValidException` 핸들링 -> 400 에러 반환.
2.  **중복 검사 (Duplicate Check)**:
    *   DB에서 입력된 `email`로 기존 회원 존재 여부 조회 (`existsByEmail`).
    *   존재할 경우 `DuplicateEmailException` 발생 -> 409 에러 반환.
3.  **비밀번호 암호화 (Encoding)**:
    *   `PasswordEncoder` (BCrypt)를 사용하여 평문 비밀번호를 해시(Hash) 처리.
4.  **엔티티 생성 및 변환**:
    *   DTO -> `User` Entity 변환.
    *   기본 Role(`ROLE_USER`) 및 가입일시 등 메타데이터 설정.
5.  **저장 (Transaction)**:
    *   `UserRepository.save()` 호출.
    *   필요 시 `Profile` 테이블에 기본 레코드 생성 (1:1 관계).
6.  **응답 반환**:
    *   민감 정보(비밀번호 등)를 제외한 `UserResponseDTO` 구성 후 반환.

---

### 💡 구현 시 참고사항 (Notes)
*   **Security Config**: `/api/v1/auth/**` 경로는 `permitAll()`로 설정되어야 함.
*   **Password Encoder**: Spring Security 설정 빈(`BCryptPasswordEncoder`) 주입 받아 사용.
*   **Transactional**: 서비스 레이어의 메서드는 `@Transactional` 어노테이션 필수.