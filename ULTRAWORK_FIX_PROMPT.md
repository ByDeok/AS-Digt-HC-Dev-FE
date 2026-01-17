# AS-Digt-HC 프로젝트 문제점 수정 프롬프트 (UltraWork용)

## 프로젝트 개요
- **프로젝트명**: AS-Digt-HC (AI 기반 시니어 웰니스 및 가족 건강 관리 플랫폼)
- **기술 스택**: 
  - Backend: Spring Boot 4.0.1-SNAPSHOT, Java 21, JPA, Spring Security, JWT
  - Frontend: React + TypeScript, Vite, React Query
- **프로젝트 구조**: Monorepo (Backend + Frontend)

---

## 발견된 문제점 목록

### 1. 프론트엔드-백엔드 연동 문제

#### 1.1 토큰 자동 갱신 미구현 (심각)
**문제점**:
- 백엔드에는 `POST /v1/auth/refresh` API가 존재하지만, 프론트엔드에서 호출하지 않음
- 토큰 만료 시 사용자가 강제로 재로그인해야 함
- `studio/src/lib/api.ts`의 인터셉터에서 401 발생 시 자동 갱신 로직이 없음

**해결 방법**:
1. `studio/src/lib/api.ts`의 `response` 인터셉터에서 401 에러 발생 시:
   - `getRefreshToken()`으로 refresh token 조회
   - `POST /v1/auth/refresh` API 호출 (axios 직접 사용, 무한 루프 방지)
   - 새 access token으로 재저장 후 원래 요청 재시도
   - refresh token도 만료된 경우 `clearAuthSession()` 호출 및 로그인 페이지로 리다이렉트

2. 갱신 중 동시 요청 방지를 위한 플래그 추가 (중복 호출 방지)

#### 1.2 포털 연동 API 미구현 (중요)
**문제점**:
- 백엔드에 포털 연동 API가 존재하지만 프론트엔드 서비스가 없음
  - `GET /v1/integration/portals`
  - `POST /v1/integration/portals`
  - `POST /v1/integration/portals/{portalId}/sync`
  - `DELETE /v1/integration/portals/{portalId}`

**해결 방법**:
1. `studio/src/services/integrationService.ts`에 포털 연동 함수 추가:
   ```typescript
   listPortals(): Promise<PortalConnection[]>
   connectPortal(req: PortalConnectRequest): Promise<PortalConnection>
   syncPortal(portalId: string): Promise<SyncResult>
   disconnectPortal(portalId: string): Promise<void>
   ```
2. 타입 정의 추가 (`PortalConnection`, `PortalConnectRequest` 등)
3. 백엔드 DTO와 타입 구조 일치 확인

#### 1.3 가족 보드 일부 기능 미구현 (중요)
**문제점**:
- 백엔드 API는 존재하지만 프론트엔드에서 호출하지 않음:
  - `PUT /v1/family-board/members/{memberId}/role` - 역할 변경
  - `DELETE /v1/family-board/members/{memberId}` - 멤버 제거
  - `PUT /v1/family-board/settings` - 보드 설정 변경

**해결 방법**:
1. `studio/src/services/familyBoardService.ts`에 함수 추가:
   ```typescript
   updateMemberRole(memberId: string, newRole: BoardRole): Promise<FamilyMember>
   removeMember(memberId: string): Promise<void>
   updateSettings(settings: Record<string, unknown>): Promise<FamilyBoard>
   ```

---

### 2. 백엔드 설정 문제

#### 2.1 CORS 설정 중복 (중요)
**문제점**:
- `WebConfig.java`와 `SecurityConfig.java`에서 CORS 설정이 중복됨
- `WebConfig`는 모든 Origin 허용 (`allowedOrigins("*")`)
- `SecurityConfig`는 localhost 패턴만 허용
- 두 설정이 충돌하여 예상치 못한 동작 가능

**해결 방법**:
1. `WebConfig.java`의 `addCorsMappings` 메서드 제거 (SecurityConfig가 우선순위가 높음)
2. 또는 `WebConfig`의 CORS 설정을 삭제하고 `SecurityConfig`만 사용
3. 프로덕션 환경에서는 `SecurityConfig`의 CORS 설정을 환경 변수로 제어 가능하도록 개선

#### 2.2 에러 응답 형식 불일치 (중요)
**문제점**:
- `GlobalExceptionHandler`는 `ErrorResponse` 사용
- 정상 응답은 `ApiResponse` 사용
- 일관성 부족으로 프론트엔드에서 에러 처리 복잡

**해결 방법**:
1. `GlobalExceptionHandler`를 수정하여 `ApiResponse` 형태로 에러 응답:
   ```java
   return ResponseEntity.status(HttpStatus.BAD_REQUEST)
       .body(ApiResponse.<ErrorResponse>failure("Validation failed"));
   ```
2. 또는 프론트엔드에서 `ErrorResponse`도 처리하도록 `api.ts` 수정
3. **권장**: `ErrorResponse`를 `ApiResponse`의 `data` 필드에 포함시키는 방식으로 통일

---

### 3. 코드 품질 문제

#### 3.1 테스트 코드 의존성 누락 (경미)
**문제점**:
- `AuthControllerTest.java`에서 `WebMvcTest`, `MockBean` import 오류
- `build.gradle`에 `spring-boot-starter-test`는 있지만 실제 테스트가 컴파일되지 않음

**해결 방법**:
1. `build.gradle`의 테스트 의존성 확인 (이미 존재하므로 버전 호환성 문제 가능)
2. Spring Boot 4.0.1-SNAPSHOT 버전과 테스트 라이브러리 호환성 확인
3. 필요 시 테스트 코드 주석 처리 또는 제거

#### 3.2 사용하지 않는 Import 경고 (경미)
**문제점**:
- `ProfileUpdateRequest.java`에서 `@Email` import가 사용되지 않음

**해결 방법**:
1. 사용하지 않는 import 제거

---

### 4. 보안 설정 문제

#### 4.1 CORS 허용 범위 과다 (중요)
**문제점**:
- `WebConfig`에서 `allowedOrigins("*")` 사용 (모든 Origin 허용)
- 프로덕션 환경에서 보안 취약점

**해결 방법**:
1. `WebConfig` 제거 또는 프로파일별 CORS 설정 분리
2. `SecurityConfig`의 CORS 설정을 환경 변수로 제어
3. 프로덕션 환경에서는 특정 도메인만 허용

---

### 5. API 경로 일관성 문제

#### 5.1 `/metrics` 경로에 `/v1` prefix 없음 (경미)
**문제점**:
- 대부분의 API는 `/v1/` prefix 사용
- `/metrics`, `/reports`, `/actions`, `/onboarding`는 prefix 없음
- API 버전 관리 일관성 부족

**해결 방법**:
1. 모든 API에 `/v1/` prefix 추가하거나
2. 문서화하여 의도적 차이임을 명시
3. **권장**: 하위 호환성을 위해 현재 상태 유지, 새 API부터 `/v1/` 사용

---

## 수정 우선순위

### 🔴 높음 (즉시 수정 필요)
1. 토큰 자동 갱신 구현
2. CORS 설정 중복 제거
3. 에러 응답 형식 통일

### 🟡 중간 (곧 수정 필요)
4. 포털 연동 API 구현
5. 가족 보드 일부 기능 구현
6. CORS 보안 설정 개선

### 🟢 낮음 (점진적 개선)
7. 테스트 코드 의존성 수정
8. 사용하지 않는 Import 제거
9. API 경로 일관성 검토

---

## 수정 가이드라인

### 일반 원칙
1. **기존 코드 스타일 유지**: 프로젝트의 기존 코딩 컨벤션 준수
2. **하위 호환성**: 기존 API 변경 시 프론트엔드와의 호환성 확인
3. **에러 처리**: 모든 에러는 적절한 HTTP 상태 코드와 메시지 반환
4. **타입 안정성**: TypeScript 타입 정의와 Java DTO 일치 확인
5. **보안**: 인증이 필요한 API는 `@CurrentUserId` 사용 확인

### 백엔드 수정 시 주의사항
- `@RestController` 또는 `ResponseEntity` 사용 일관성
- `@Valid` 검증이 필요한 DTO에 검증 어노테이션 확인
- 예외는 `GlobalExceptionHandler`에서 처리
- 로깅은 `@Slf4j` 사용

### 프론트엔드 수정 시 주의사항
- API 호출은 `studio/src/lib/api.ts`의 axios 인스턴스 사용
- 응답 언래핑은 `unwrapApiResponse` 유틸 사용
- 타입 정의는 `studio/src/services/*.ts`에 위치
- 에러 처리는 try-catch 또는 React Query의 `onError` 사용

---

## 수정 완료 후 확인 사항

1. ✅ 프론트엔드에서 토큰 만료 시 자동 갱신 확인
2. ✅ 모든 API 엔드포인트가 프론트엔드에서 호출 가능한지 확인
3. ✅ CORS 설정이 개발/프로덕션 환경에서 정상 동작하는지 확인
4. ✅ 에러 응답이 프론트엔드에서 적절히 처리되는지 확인
5. ✅ 컴파일 오류 및 린트 경고 제거
6. ✅ API 응답 형식 일관성 확인 (`ApiResponse` 또는 `ErrorResponse`)

---

## 참고 파일 위치

### 백엔드 주요 파일
- `src/main/java/vibe/digthc/as_digt_hc_dev_fe/interfaces/common/ApiResponse.java`
- `src/main/java/vibe/digthc/as_digt_hc_dev_fe/interfaces/common/GlobalExceptionHandler.java`
- `src/main/java/vibe/digthc/as_digt_hc_dev_fe/infrastructure/config/WebConfig.java`
- `src/main/java/vibe/digthc/as_digt_hc_dev_fe/infrastructure/security/SecurityConfig.java`
- `src/main/java/vibe/digthc/as_digt_hc_dev_fe/interfaces/controller/**/*.java`

### 프론트엔드 주요 파일
- `studio/src/lib/api.ts` - Axios 설정 및 인터셉터
- `studio/src/lib/auth.ts` - 인증 토큰 관리
- `studio/src/services/*.ts` - API 서비스 레이어

---

## 실행 지시사항

위의 문제점들을 순서대로 수정하고, 각 수정 사항마다 다음을 확인하세요:
1. 코드 컴파일 및 빌드 성공
2. 기존 기능이 정상 동작하는지 확인 (회귀 테스트)
3. 새로운 기능이 의도대로 동작하는지 확인
4. 린트 오류 및 경고 제거

모든 수정이 완료되면 최종 통합 테스트를 수행하여 프론트엔드와 백엔드가 정상적으로 연동되는지 확인하세요.
