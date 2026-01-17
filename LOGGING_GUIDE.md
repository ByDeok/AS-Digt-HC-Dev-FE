# API 로깅 가이드

## 개요

프론트엔드와 백엔드 간의 모든 API 요청/응답을 로깅하는 시스템입니다.
4가지 로거 유형을 제공하며, 각각을 독립적으로 온/오프할 수 있습니다.

## 로거 유형

### 1. 프론트엔드 요청 로거 (Frontend Request Logger)
- **위치**: 프론트엔드 (`studio/src/lib/logger.ts`)
- **용도**: 프론트엔드에서 백엔드로 보내는 요청을 로깅
- **자동 적용**: `studio/src/lib/api.ts`의 axios 인터셉터에 통합됨

### 2. 프론트엔드 응답 로거 (Frontend Response Logger)
- **위치**: 프론트엔드 (`studio/src/lib/logger.ts`)
- **용도**: 백엔드로부터 받는 응답을 로깅
- **자동 적용**: `studio/src/lib/api.ts`의 axios 인터셉터에 통합됨

### 3. 백엔드 요청 로거 (Backend Request Logger)
- **위치**: 백엔드 (`src/main/java/.../logging/ApiLogger.java`)
- **용도**: 백엔드에서 받는 요청을 로깅
- **자동 적용**: `RequestLoggingFilter`를 통해 모든 요청에 자동 적용

### 4. 백엔드 응답 로거 (Backend Response Logger)
- **위치**: 백엔드 (`src/main/java/.../logging/ApiLogger.java`)
- **용도**: 백엔드에서 보내는 응답을 로깅
- **자동 적용**: `RequestLoggingFilter`를 통해 모든 응답에 자동 적용

## 프론트엔드 설정

### 환경변수 설정

`.env` 또는 `.env.local` 파일에 다음 설정을 추가:

```env
# API 로깅 활성화 (기본값: 개발 환경에서는 true, 프로덕션에서는 false)
VITE_API_LOGGING_ENABLED=true

# 추가 설정은 코드에서 직접 변경 가능
```

### 프로그래밍 방식 설정

```typescript
import { setLogConfig, setLoggingEnabled } from '@/lib/logger';

// 로깅 완전히 비활성화
setLoggingEnabled(false);

// 로깅 활성화 및 설정 변경
setLogConfig({
  enabled: true,
  includeHeaders: true,
  includeBody: true,
  maxBodyLength: 2000, // 본문 최대 길이 조정
});
```

### 로거 함수 직접 사용

```typescript
import { 
  logFrontendRequest, 
  logFrontendResponse,
  logBackendRequest,
  logBackendResponse 
} from '@/lib/logger';

// 프론트엔드 요청 로깅
logFrontendRequest('POST', '/api/users', headers, requestBody, queryParams);

// 프론트엔드 응답 로깅
logFrontendResponse('POST', '/api/users', 200, 'OK', headers, responseData, 150);
```

## 백엔드 설정

### application.yml 설정

```yaml
app:
  logging:
    api:
      enabled: ${API_LOGGING_ENABLED:true}  # API 로깅 활성화 여부
      include-headers: ${API_LOGGING_INCLUDE_HEADERS:true}  # 헤더 포함 여부
      include-body: ${API_LOGGING_INCLUDE_BODY:true}  # 본문 포함 여부
      max-body-length: ${API_LOGGING_MAX_BODY_LENGTH:1000}  # 본문 최대 길이
```

### 환경변수 설정

```bash
# API 로깅 비활성화
export API_LOGGING_ENABLED=false

# 헤더 로깅 비활성화
export API_LOGGING_INCLUDE_HEADERS=false

# 본문 로깅 비활성화
export API_LOGGING_INCLUDE_BODY=false

# 본문 최대 길이 조정
export API_LOGGING_MAX_BODY_LENGTH=2000
```

### 프로그래밍 방식 사용

```java
@Autowired
private ApiLogger apiLogger;

// 백엔드 요청 로깅
apiLogger.logBackendRequest(request, requestBody);

// 백엔드 응답 로깅
apiLogger.logBackendResponse(request, response, responseBody, duration);
```

## 보안 기능

### 자동 마스킹

다음 필드들은 자동으로 마스킹됩니다:

**헤더:**
- `Authorization`
- `Cookie`
- `X-API-Key`
- `X-Auth-Token`

**본문 필드:**
- `password`
- `accessToken`
- `refreshToken`
- `token`
- `authorization`

마스킹된 값은 `***MASKED***`로 표시됩니다.

## 로그 출력 예시

### 프론트엔드 콘솔 출력

```
🚀 [Frontend Request] POST /api/v1/auth/login
  Timestamp: 2024-12-XX...
  Headers: {...}
  Request Body: {
    "email": "user@example.com",
    "password": "***MASKED***"
  }

✅ [Frontend Response] POST /api/v1/auth/login
  Timestamp: 2024-12-XX...
  Status: 200 OK
  Duration: 150ms
  Response Body: {
    "success": true,
    "data": {
      "accessToken": "***MASKED***",
      ...
    }
  }
```

### 백엔드 로그 출력

```
INFO  📥 [Backend Request] POST /api/v1/auth/login - {...}
INFO  ✅ [Backend Response] POST /api/v1/auth/login - Status: 200 - {...}
```

## 제외 경로

다음 경로는 자동으로 로깅에서 제외됩니다:

- `/actuator/*` - Spring Boot Actuator
- `/health` - Health check
- `/favicon.ico` - Favicon

## 성능 고려사항

1. **본문 길이 제한**: 기본값 1000자로 제한하여 대용량 응답의 로깅 오버헤드 방지
2. **비동기 로깅**: 로깅이 메인 요청 처리 흐름을 차단하지 않도록 설계
3. **프로덕션 환경**: 프로덕션에서는 `enabled: false`로 설정 권장

## 문제 해결

### 로깅이 작동하지 않는 경우

1. **프론트엔드**:
   - `VITE_API_LOGGING_ENABLED=true` 확인
   - 브라우저 콘솔 확인
   - `setLoggingEnabled(true)` 호출 확인

2. **백엔드**:
   - `application.yml`의 `app.logging.api.enabled` 확인
   - 환경변수 `API_LOGGING_ENABLED` 확인
   - 로그 레벨이 INFO 이상인지 확인

### 로그가 너무 많은 경우

- `include-body: false`로 설정하여 본문 로깅 비활성화
- `max-body-length` 값을 줄여서 본문 길이 제한
- 특정 경로를 제외 목록에 추가

## 업데이트 이력

- 2024-12-XX: 초기 구현
