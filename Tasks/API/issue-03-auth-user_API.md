먼저 SRS 문서와 관련 파일들을 확인하여 상세 요구사항을 파악하겠습니다.

[3 tools called]

SRS 문서와 기존 작업 파일들을 확인했습니다. 이제 이메일 기반 회원가입 로직을 중심으로 **완전한 Auth API 명세서**를 작성하겠습니다.

---

## 📋 [BE] Issue-03: Auth API 명세서 (Email 기반)

### 🔐 1. 회원가입 (Sign Up)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `POST /api/v1/auth/signup` |
| **설명** | 이메일/비밀번호 기반 신규 사용자 가입 |
| **인증** | `permitAll()` - 인증 불필요 |
| **난이도** | **하 (Low)** |

#### Request Body
| 필드명 | 타입 | 필수 | 설명 | Validation |
|:---|:---|:---:|:---|:---|
| `email` | `String` | ✅ | 사용자 이메일 (로그인 ID) | `@Email`, `@NotBlank`, max 100자 |
| `password` | `String` | ✅ | 비밀번호 | `@Pattern`: 8~20자, 영문+숫자+특수문자 |
| `name` | `String` | ✅ | 이름/닉네임 | `@NotBlank`, 2~50자 |
| `role` | `Enum` | ❌ | 사용자 역할 | `SENIOR`(기본), `CAREGIVER` |
| `agreements` | `Object` | ✅ | 약관 동의 | - |
| ├ `termsService` | `Boolean` | ✅ | 서비스 이용약관 | `true` 필수 |
| └ `privacyPolicy` | `Boolean` | ✅ | 개인정보 처리방침 | `true` 필수 |

#### Response Body
**✅ 성공 (201 Created)**
```json
{
  "status": "success",
  "data": {
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "email": "user@example.com",
    "name": "홍길동",
    "role": "SENIOR",
    "createdAt": "2025-11-28T10:00:00"
  }
}
```

**❌ 실패 (RFC 7807 ProblemDetails)**
| HTTP | type | title | 원인 |
|:---|:---|:---|:---|
| `400` | `/errors/validation` | 입력값 검증 실패 | 비밀번호 형식 불일치 등 |
| `409` | `/errors/duplicate-email` | 이메일 중복 | 이미 가입된 이메일 |

#### Logic Steps
```
1. [Validation] Request Body 유효성 검사 (jakarta.validation)
2. [Duplicate Check] email로 기존 회원 존재 여부 확인 (existsByEmail)
3. [Encoding] BCryptPasswordEncoder로 비밀번호 해시
4. [Entity Create] User Entity 생성 (role 기본값: SENIOR)
5. [Transaction] UserRepository.save() + UserAgreement 저장
6. [Response] 민감정보 제외한 UserResponse(record) 반환
```

---

### 🔑 2. 로그인 (Login)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `POST /api/v1/auth/login` |
| **설명** | 이메일/비밀번호로 JWT 토큰 발급 |
| **인증** | `permitAll()` |
| **난이도** | **중 (Medium)** |

#### Request Body
| 필드명 | 타입 | 필수 | 설명 | Validation |
|:---|:---|:---:|:---|:---|
| `email` | `String` | ✅ | 가입된 이메일 | `@Email`, `@NotBlank` |
| `password` | `String` | ✅ | 비밀번호 | `@NotBlank` |

#### Response Body
**✅ 성공 (200 OK)**
```json
{
  "status": "success",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "user": {
      "userId": "550e8400-e29b-41d4-a716-446655440000",
      "email": "user@example.com",
      "name": "홍길동",
      "role": "SENIOR"
    }
  }
}
```

**❌ 실패**
| HTTP | type | title | 원인 |
|:---|:---|:---|:---|
| `401` | `/errors/invalid-credentials` | 인증 실패 | 이메일/비밀번호 불일치 |
| `403` | `/errors/account-locked` | 계정 잠김 | 로그인 5회 실패 |

#### Logic Steps
```
1. [Validation] Request Body 유효성 검사
2. [Find User] email로 사용자 조회 (없으면 401)
3. [Password Match] BCrypt.matches()로 비밀번호 검증
4. [Generate Tokens] JwtTokenProvider로 Access/Refresh Token 생성
   - AccessToken: 1시간 만료
   - RefreshToken: 14일 만료 (Redis에 저장)
5. [Audit Log] 로그인 성공 기록 (AuditLog 테이블)
6. [Response] 토큰 및 사용자 정보 반환
```

---

### 🔄 3. 토큰 갱신 (Refresh)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `POST /api/v1/auth/refresh` |
| **설명** | Refresh Token으로 새 Access Token 발급 |
| **인증** | `permitAll()` |
| **난이도** | **중 (Medium)** |

#### Request Body
| 필드명 | 타입 | 필수 | 설명 | Validation |
|:---|:---|:---:|:---|:---|
| `refreshToken` | `String` | ✅ | 유효한 Refresh Token | `@NotBlank` |

#### Response Body
**✅ 성공 (200 OK)**
```json
{
  "status": "success",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
    "tokenType": "Bearer",
    "expiresIn": 3600
  }
}
```

**❌ 실패**
| HTTP | type | title | 원인 |
|:---|:---|:---|:---|
| `401` | `/errors/invalid-token` | 유효하지 않은 토큰 | 만료/변조된 Refresh Token |
| `401` | `/errors/token-revoked` | 토큰 폐기됨 | 로그아웃된 토큰 |

#### Logic Steps
```
1. [Validation] Refresh Token 형식 검증
2. [Verify Token] JWT 서명 및 만료 검증
3. [Check Redis] Redis에서 토큰 존재 여부 확인 (블랙리스트 체크)
4. [Rotate Token] 새 Access Token + Refresh Token 발급 (RTR 방식)
5. [Update Redis] 기존 Refresh Token 삭제 + 새 토큰 저장
6. [Response] 새 토큰 쌍 반환
```

---

### 🚪 4. 로그아웃 (Logout)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `POST /api/v1/auth/logout` |
| **설명** | 현재 토큰 무효화 |
| **인증** | `Bearer Token` 필수 |
| **난이도** | **하 (Low)** |

#### Request Header
| 헤더명 | 값 | 필수 |
|:---|:---|:---:|
| `Authorization` | `Bearer {accessToken}` | ✅ |

#### Request Body
| 필드명 | 타입 | 필수 | 설명 |
|:---|:---|:---:|:---|
| `refreshToken` | `String` | ❌ | 함께 폐기할 Refresh Token |

#### Response Body
**✅ 성공 (200 OK)**
```json
{
  "status": "success",
  "message": "로그아웃되었습니다."
}
```

#### Logic Steps
```
1. [Extract Token] SecurityContext에서 현재 사용자 정보 추출
2. [Blacklist Access] Access Token을 Redis 블랙리스트에 추가 (남은 TTL만큼)
3. [Revoke Refresh] Refresh Token Redis에서 삭제
4. [Audit Log] 로그아웃 기록
5. [Response] 성공 메시지 반환
```

---

### 👤 5. 내 정보 조회 (Get Me)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `GET /api/v1/users/me` |
| **설명** | 현재 로그인한 사용자 정보 조회 |
| **인증** | `Bearer Token` 필수 |
| **난이도** | **하 (Low)** |

#### Request Header
| 헤더명 | 값 | 필수 |
|:---|:---|:---:|
| `Authorization` | `Bearer {accessToken}` | ✅ |

#### Response Body
**✅ 성공 (200 OK)**
```json
{
  "status": "success",
  "data": {
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "email": "user@example.com",
    "name": "홍길동",
    "role": "SENIOR",
    "profile": {
      "phoneNumber": "010-1234-5678",
      "birthDate": "1965-03-15",
      "gender": "MALE",
      "profileImageUrl": null
    },
    "createdAt": "2025-11-28T10:00:00"
  }
}
```

#### Logic Steps
```
1. [Resolve User] @CurrentUserId로 JWT에서 userId 추출
2. [Find User] UserRepository.findById() + Profile 조회
3. [Response] UserDetailResponse(record) 반환
```

---

### 📧 6. 이메일 중복 확인 (Check Email)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `GET /api/v1/auth/check-email` |
| **설명** | 회원가입 전 이메일 사용 가능 여부 확인 |
| **인증** | `permitAll()` |
| **난이도** | **하 (Low)** |

#### Query Parameters
| 파라미터명 | 타입 | 필수 | 설명 |
|:---|:---|:---:|:---|
| `email` | `String` | ✅ | 확인할 이메일 |

#### Response Body
**✅ 성공 (200 OK)**
```json
{
  "status": "success",
  "data": {
    "email": "user@example.com",
    "available": true
  }
}
```

---

### 🔒 7. 비밀번호 재설정 요청 (Request Password Reset)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `POST /api/v1/auth/password/reset-request` |
| **설명** | 비밀번호 재설정 이메일 발송 |
| **인증** | `permitAll()` |
| **난이도** | **중 (Medium)** |

#### Request Body
| 필드명 | 타입 | 필수 | 설명 | Validation |
|:---|:---|:---:|:---|:---|
| `email` | `String` | ✅ | 가입된 이메일 | `@Email`, `@NotBlank` |

#### Response Body
**✅ 성공 (200 OK)** - 보안상 존재 여부 노출 안함
```json
{
  "status": "success",
  "message": "이메일이 존재하면 재설정 링크가 발송됩니다."
}
```

#### Logic Steps
```
1. [Find User] email로 사용자 조회
2. [Generate Token] UUID 기반 Reset Token 생성 (15분 만료)
3. [Store Token] Redis에 resetToken:userId 매핑 저장
4. [Send Email] 비동기로 재설정 링크 이메일 발송
5. [Response] 성공 메시지 (사용자 존재 여부 숨김)
```

---

### 🔓 8. 비밀번호 재설정 (Reset Password)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `POST /api/v1/auth/password/reset` |
| **설명** | 토큰 검증 후 새 비밀번호 설정 |
| **인증** | `permitAll()` |
| **난이도** | **중 (Medium)** |

#### Request Body
| 필드명 | 타입 | 필수 | 설명 | Validation |
|:---|:---|:---:|:---|:---|
| `token` | `String` | ✅ | 이메일로 받은 Reset Token | `@NotBlank` |
| `newPassword` | `String` | ✅ | 새 비밀번호 | `@Pattern`: 8~20자, 영문+숫자+특수문자 |

#### Response Body
**✅ 성공 (200 OK)**
```json
{
  "status": "success",
  "message": "비밀번호가 변경되었습니다."
}
```

**❌ 실패**
| HTTP | type | title | 원인 |
|:---|:---|:---|:---|
| `400` | `/errors/invalid-token` | 유효하지 않은 토큰 | 만료/변조된 Reset Token |

#### Logic Steps
```
1. [Verify Token] Redis에서 Reset Token 유효성 확인
2. [Find User] Token에 매핑된 userId로 사용자 조회
3. [Update Password] BCrypt로 새 비밀번호 해시 후 저장
4. [Invalidate Token] Redis에서 Reset Token 삭제
5. [Revoke Sessions] 해당 사용자의 모든 Refresh Token 삭제 (보안)
6. [Response] 성공 메시지 반환
```

---

## 📊 API 요약 테이블

| # | Method | Endpoint | 설명 | 인증 | 난이도 |
|:---:|:---|:---|:---|:---:|:---:|
| 1 | `POST` | `/api/v1/auth/signup` | 회원가입 | ❌ | 하 |
| 2 | `POST` | `/api/v1/auth/login` | 로그인 | ❌ | 중 |
| 3 | `POST` | `/api/v1/auth/refresh` | 토큰 갱신 | ❌ | 중 |
| 4 | `POST` | `/api/v1/auth/logout` | 로그아웃 | ✅ | 하 |
| 5 | `GET` | `/api/v1/users/me` | 내 정보 조회 | ✅ | 하 |
| 6 | `GET` | `/api/v1/auth/check-email` | 이메일 중복확인 | ❌ | 하 |
| 7 | `POST` | `/api/v1/auth/password/reset-request` | 비밀번호 재설정 요청 | ❌ | 중 |
| 8 | `POST` | `/api/v1/auth/password/reset` | 비밀번호 재설정 | ❌ | 중 |

---

## 🏗️ 구현 참고사항

### Security Config 설정
```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(s -> s.sessionCreationPolicy(STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/v1/auth/**").permitAll()
            .requestMatchers("/api/v1/users/me").authenticated()
            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
}
```

### DTO 예시 (Java Record)
```java
public record SignupRequest(
    @Email @NotBlank String email,
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$")
    String password,
    @NotBlank @Size(min = 2, max = 50) String name,
    Role role,
    @Valid AgreementsRequest agreements
) {}

public record UserResponse(UUID userId, String email, String name, Role role, LocalDateTime createdAt) {}
```

---
