# [BE] Issue-01: Spring Boot 프로젝트 초기 설정 API 명세서

## 📋 개요

**SRS 1.5 Assumptions & Constraints** (C-TEC-002, C-TEC-003)에 따라 백엔드 개발 환경 구축 시 필요한 기본 API 명세서입니다.

**기술 스택**: Java 17+, Spring Boot 3.4+, MySQL 9.x, Jakarta EE 10

---

## 🔐 1. 회원가입 (Sign Up)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `POST /api/v1/auth/signup` |
| **설명** | 이메일/비밀번호 기반 신규 사용자 가입 |
| **인증** | `permitAll()` - 인증 불필요 |
| **난이도** | **하 (Low)** |

### Request Body

| 필드명 | 타입 | 필수 | 설명 | Validation |
|:---|:---|:---:|:---|:---|
| `email` | `String` | ✅ | 사용자 이메일 (로그인 ID) | `@Email`, `@NotBlank`, max 100자 |
| `password` | `String` | ✅ | 비밀번호 | `@Pattern`: 8~20자, 영문+숫자+특수문자 포함 |
| `name` | `String` | ✅ | 이름/닉네임 | `@NotBlank`, `@Size(min = 2, max = 50)` |
| `role` | `Enum` | ❌ | 사용자 역할 | `SENIOR`(기본값), `CAREGIVER`, `ADMIN` |
| `agreements` | `Object` | ✅ | 약관 동의 정보 | - |
| ├ `termsService` | `Boolean` | ✅ | 서비스 이용약관 | `true` 필수 |
| ├ `privacyPolicy` | `Boolean` | ✅ | 개인정보 처리방침 | `true` 필수 |
| └ `marketingConsent` | `Boolean` | ❌ | 마케팅 수신 동의 | `false` 기본값 |

#### Request Body 예시

```json
{
  "email": "user@example.com",
  "password": "SecurePass123!",
  "name": "홍길동",
  "role": "SENIOR",
  "agreements": {
    "termsService": true,
    "privacyPolicy": true,
    "marketingConsent": false
  }
}
```

### Response Body

**✅ 성공 (201 Created)**

```json
{
  "success": true,
  "message": "회원가입이 완료되었습니다.",
  "data": {
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "email": "user@example.com",
    "name": "홍길동",
    "role": "SENIOR",
    "authProvider": "EMAIL",
    "createdAt": "2025-01-15T10:00:00"
  }
}
```

**❌ 실패 (RFC 7807 ProblemDetails)**

| HTTP | type | title | 원인 |
|:---|:---|:---|:---|
| `400` | `/errors/validation` | 입력값 검증 실패 | 비밀번호 형식 불일치, 필수 필드 누락 등 |
| `409` | `/errors/duplicate-email` | 이메일 중복 | 이미 가입된 이메일 |

#### 실패 응답 예시 (400 Bad Request)

```json
{
  "type": "/errors/validation",
  "title": "입력값 검증 실패",
  "status": 400,
  "detail": "비밀번호는 8~20자이며 영문, 숫자, 특수문자를 포함해야 합니다.",
  "instance": "/api/v1/auth/signup",
  "errors": [
    {
      "field": "password",
      "message": "비밀번호 형식이 올바르지 않습니다."
    }
  ]
}
```

### Logic Steps

```
1. [Validation] Request Body 유효성 검사 (jakarta.validation.constraints)
   - @Valid 어노테이션으로 자동 검증
   - 필수 약관 동의 여부 확인 (termsService, privacyPolicy는 true 필수)

2. [Duplicate Check] email로 기존 회원 존재 여부 확인
   - UserRepository.existsByEmail(email) 호출
   - 중복 시 DuplicateEmailException 발생 (409 Conflict)

3. [Password Encoding] BCryptPasswordEncoder로 비밀번호 해시
   - passwordEncoder.encode(request.getPassword())

4. [Entity Create] User Entity 생성
   - User.createEmailUser() 팩토리 메서드 사용
   - role 기본값: SENIOR (요청값이 없을 경우)

5. [Transaction] 데이터 저장 (트랜잭션 내에서 수행)
   - UserRepository.save(user) → users 테이블 저장
   - UserProfile 생성 및 저장 (빈 프로필)
   - UserAgreement 생성 및 저장 (약관 동의 기록)

6. [Response] 민감정보 제외한 UserResponse(record) 반환
   - password, providerId 등 제외
   - ApiResponse.success(UserRes.from(user)) 래핑
```

---

## 🔑 2. 로그인 (Login)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `POST /api/v1/auth/login` |
| **설명** | 이메일/비밀번호로 JWT 토큰 발급 |
| **인증** | `permitAll()` - 인증 불필요 |
| **난이도** | **중 (Medium)** |

### Request Body

| 필드명 | 타입 | 필수 | 설명 | Validation |
|:---|:---|:---:|:---|:---|
| `email` | `String` | ✅ | 가입된 이메일 | `@Email`, `@NotBlank` |
| `password` | `String` | ✅ | 비밀번호 | `@NotBlank` |

#### Request Body 예시

```json
{
  "email": "user@example.com",
  "password": "SecurePass123!"
}
```

### Response Body

**✅ 성공 (200 OK)**

```json
{
  "success": true,
  "message": "로그인 성공",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
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
| `404` | `/errors/user-not-found` | 사용자 없음 | 가입되지 않은 이메일 |

#### 실패 응답 예시 (401 Unauthorized)

```json
{
  "type": "/errors/invalid-credentials",
  "title": "인증 실패",
  "status": 401,
  "detail": "이메일 또는 비밀번호가 올바르지 않습니다.",
  "instance": "/api/v1/auth/login"
}
```

### Logic Steps

```
1. [Validation] Request Body 유효성 검사
   - @Valid로 email, password 형식 검증

2. [Find User] email로 사용자 조회
   - UserRepository.findByEmail(email)
   - 사용자 없으면 UserNotFoundException 발생 (404)

3. [Password Match] BCrypt.matches()로 비밀번호 검증
   - passwordEncoder.matches(rawPassword, encodedPassword)
   - 불일치 시 InvalidCredentialsException 발생 (401)

4. [Generate Tokens] JwtTokenProvider로 Access/Refresh Token 생성
   - AccessToken: 1시간 만료 (3600초)
   - RefreshToken: 14일 만료 (1209600초)
   - Redis에 RefreshToken 저장 (key: refreshToken:{userId}, TTL: 14일)

5. [Audit Log] 로그인 성공 기록 (선택사항)
   - AuditLog 테이블에 LOGIN 액션 기록
   - IP 주소, 디바이스 정보 등 메타데이터 포함

6. [Response] 토큰 및 사용자 정보 반환
   - TokenResponse(record) 생성
   - ApiResponse.success(TokenRes) 래핑
```

---

## 🚪 3. 로그아웃 (Logout)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `POST /api/v1/auth/logout` |
| **설명** | 현재 토큰 무효화 및 세션 종료 |
| **인증** | `Bearer Token` 필수 |
| **난이도** | **하 (Low)** |

### Request Header

| 헤더명 | 값 | 필수 |
|:---|:---|:---:|
| `Authorization` | `Bearer {accessToken}` | ✅ |

### Request Body

| 필드명 | 타입 | 필수 | 설명 |
|:---|:---|:---:|:---|
| `refreshToken` | `String` | ❌ | 함께 폐기할 Refresh Token (선택사항) |

#### Request Body 예시 (선택사항)

```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Response Body

**✅ 성공 (200 OK)**

```json
{
  "success": true,
  "message": "로그아웃되었습니다."
}
```

**❌ 실패**

| HTTP | type | title | 원인 |
|:---|:---|:---|:---|
| `401` | `/errors/unauthorized` | 인증 실패 | 유효하지 않은 토큰 |

### Logic Steps

```
1. [Extract Token] SecurityContext에서 현재 사용자 정보 추출
   - JWT에서 userId 추출 (@CurrentUserId 또는 SecurityContext)

2. [Blacklist Access] Access Token을 Redis 블랙리스트에 추가
   - key: blacklist:accessToken:{token}
   - TTL: AccessToken의 남은 만료 시간만큼 설정

3. [Revoke Refresh] Refresh Token Redis에서 삭제 (요청 시)
   - refreshToken이 제공된 경우: Redis에서 삭제
   - key: refreshToken:{userId}

4. [Audit Log] 로그아웃 기록 (선택사항)
   - AuditLog 테이블에 LOGOUT 액션 기록

5. [Response] 성공 메시지 반환
   - ApiResponse.success("로그아웃되었습니다.")
```

---

## 👤 4. 내 정보 조회 (Get My Profile)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `GET /api/v1/users/me` |
| **설명** | 현재 로그인한 사용자 정보 및 프로필 조회 |
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
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "email": "user@example.com",
    "name": "홍길동",
    "role": "SENIOR",
    "authProvider": "EMAIL",
    "profile": {
      "phoneNumber": "010-1234-5678",
      "profileImageUrl": null,
      "bio": null,
      "birthDate": "1965-03-15",
      "gender": "MALE"
    },
    "createdAt": "2025-01-15T10:00:00",
    "updatedAt": "2025-01-15T10:00:00"
  }
}
```

**❌ 실패**

| HTTP | type | title | 원인 |
|:---|:---|:---|:---|
| `401` | `/errors/unauthorized` | 인증 실패 | 유효하지 않은 토큰 |
| `404` | `/errors/user-not-found` | 사용자 없음 | 사용자 정보를 찾을 수 없음 |

### Logic Steps

```
1. [Resolve User] @CurrentUserId로 JWT에서 userId 추출
   - JwtAuthenticationFilter 또는 @AuthenticationPrincipal 사용

2. [Find User] UserRepository.findById() + Profile 조회
   - UserRepository.findByIdWithProfile(userId) 사용 (Fetch Join)
   - N+1 문제 방지를 위해 LEFT JOIN FETCH 사용
   - 사용자 없으면 UserNotFoundException 발생 (404)

3. [Response] UserDetailResponse(record) 반환
   - UserProfile 정보 포함
   - 민감정보 제외 (password, providerId 등)
   - ApiResponse.success(UserDetailRes) 래핑
```

---

## ✏️ 5. 프로필 수정 (Update Profile)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `PUT /api/v1/users/me` |
| **설명** | 현재 로그인한 사용자 프로필 정보 수정 |
| **인증** | `Bearer Token` 필수 |
| **난이도** | **중 (Medium)** |

### Request Header

| 헤더명 | 값 | 필수 |
|:---|:---|:---:|
| `Authorization` | `Bearer {accessToken}` | ✅ |

### Request Body

| 필드명 | 타입 | 필수 | 설명 | Validation |
|:---|:---|:---:|:---|:---|
| `name` | `String` | ❌ | 이름/닉네임 | `@Size(min = 2, max = 50)` |
| `phoneNumber` | `String` | ❌ | 전화번호 | `@Pattern`: 010-XXXX-XXXX 형식 |
| `profileImageUrl` | `String` | ❌ | 프로필 이미지 URL | `@URL`, max 255자 |
| `bio` | `String` | ❌ | 자기소개 | max 500자 |
| `birthDate` | `LocalDate` | ❌ | 생년월일 | `@Past` (과거 날짜만) |
| `gender` | `Enum` | ❌ | 성별 | `MALE`, `FEMALE`, `OTHER` |

#### Request Body 예시

```json
{
  "name": "홍길동",
  "phoneNumber": "010-1234-5678",
  "profileImageUrl": "https://example.com/profile.jpg",
  "bio": "안녕하세요. 건강한 삶을 추구합니다.",
  "birthDate": "1965-03-15",
  "gender": "MALE"
}
```

### Response Body

**✅ 성공 (200 OK)**

```json
{
  "success": true,
  "message": "프로필이 수정되었습니다.",
  "data": {
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "email": "user@example.com",
    "name": "홍길동",
    "role": "SENIOR",
    "profile": {
      "phoneNumber": "010-1234-5678",
      "profileImageUrl": "https://example.com/profile.jpg",
      "bio": "안녕하세요. 건강한 삶을 추구합니다.",
      "birthDate": "1965-03-15",
      "gender": "MALE"
    },
    "updatedAt": "2025-01-15T11:30:00"
  }
}
```

**❌ 실패**

| HTTP | type | title | 원인 |
|:---|:---|:---|:---|
| `400` | `/errors/validation` | 입력값 검증 실패 | 잘못된 형식의 데이터 |
| `401` | `/errors/unauthorized` | 인증 실패 | 유효하지 않은 토큰 |
| `404` | `/errors/user-not-found` | 사용자 없음 | 사용자 정보를 찾을 수 없음 |

### Logic Steps

```
1. [Resolve User] JWT에서 userId 추출
   - @CurrentUserId 또는 SecurityContext 사용

2. [Validation] Request Body 유효성 검사
   - @Valid로 각 필드 형식 검증
   - phoneNumber 형식 검증 (010-XXXX-XXXX)
   - birthDate는 과거 날짜만 허용

3. [Find User] UserRepository.findById() + Profile 조회
   - UserRepository.findByIdWithProfile(userId)
   - 사용자 없으면 UserNotFoundException 발생 (404)

4. [Update User] User Entity 업데이트 (필요 시)
   - name이 제공된 경우 User.name 업데이트

5. [Update Profile] UserProfile Entity 업데이트
   - UserProfile.update() 메서드 호출
   - null이 아닌 필드만 업데이트 (부분 업데이트)
   - UserProfileRepository.save(profile)

6. [Response] 업데이트된 UserDetailResponse 반환
   - ApiResponse.success(UserDetailRes) 래핑
```

---

## 📊 API 요약 테이블

| # | Method | Endpoint | 설명 | 인증 | 난이도 |
|:---:|:---|:---|:---|:---:|:---:|
| 1 | `POST` | `/api/v1/auth/signup` | 회원가입 | ❌ | 하 |
| 2 | `POST` | `/api/v1/auth/login` | 로그인 | ❌ | 중 |
| 3 | `POST` | `/api/v1/auth/logout` | 로그아웃 | ✅ | 하 |
| 4 | `GET` | `/api/v1/users/me` | 내 정보 조회 | ✅ | 하 |
| 5 | `PUT` | `/api/v1/users/me` | 프로필 수정 | ✅ | 중 |

---

## 🏗️ 구현 참고사항

### 1. 표준 응답 포맷 (ApiResponse)

```java
public record ApiResponse<T>(
    boolean success,
    String message,
    T data,
    List<ErrorDetail> errors
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "성공", data, null);
    }
    
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, null);
    }
    
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, null);
    }
}
```

### 2. DTO 예시 (Java Record)

```java
// Request DTO
public record SignupRequest(
    @Email @NotBlank @Size(max = 100) String email,
    @NotBlank 
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$")
    String password,
    @NotBlank @Size(min = 2, max = 50) String name,
    Role role,
    @Valid AgreementsRequest agreements
) {}

public record AgreementsRequest(
    @NotNull Boolean termsService,
    @NotNull Boolean privacyPolicy,
    Boolean marketingConsent
) {}

// Response DTO
public record UserResponse(
    UUID userId,
    String email,
    String name,
    Role role,
    AuthProvider authProvider,
    LocalDateTime createdAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
            user.getId(),
            user.getEmail(),
            user.getName(),
            user.getRole(),
            user.getAuthProvider(),
            user.getCreatedAt()
        );
    }
}
```

### 3. Security Config 설정

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

### 4. Global Exception Handler (RFC 7807)

```java
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleBusinessException(BusinessException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            e.getStatus(), 
            e.getMessage()
        );
        problem.setProperty("code", e.getErrorCode());
        return problem;
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "입력값 검증 실패"
        );
        // Validation 에러 상세 정보 추가
        return problem;
    }
}
```

---

## 📝 참고 자료

- [BE] Issue-01: Spring Boot 프로젝트 초기 설정 및 환경 구축 (`studio/Tasks/BE_issue/issue-01-be-setup.md`)
- [BE] Issue-03: Auth API 명세서 (`studio/Tasks/API/issue-03-auth-user_API.md`)
- Java Spring Boot 3.x Cursor Rules (`.cursor/rules/300-java-spring-cursor-rules.mdc`)
- SRS 1.5 Assumptions & Constraints

