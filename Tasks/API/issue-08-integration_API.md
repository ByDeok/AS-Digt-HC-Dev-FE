# [BE] Issue-08: 외부 연동 (Device & Portal) 및 동의 관리 API 명세서

## 📋 개요

**SRS REQ-FUNC-003, 004** 및 **동의(Consent)** 관리를 위한 외부 시스템 연동 API 명세서입니다.

**기술 스택**: Java 17+, Spring Boot 3.4+, MySQL 9.x, Jakarta EE 10

---

## 📱 1. 디바이스 연동 목록 조회 (Get Device Links)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `GET /api/v1/integration/devices` |
| **설명** | 사용자의 연동된 디바이스 목록 조회 |
| **인증** | `Bearer Token` 필수 |
| **난이도** | **하 (Low)** |

### Response Body

**✅ 성공 (200 OK)**

```json
{
  "success": true,
  "message": "조회 성공",
  "data": [
    {
      "deviceId": "550e8400-e29b-41d4-a716-446655440000",
      "vendor": "samsung",
      "deviceType": "watch",
      "status": "ACTIVE",
      "lastSyncAt": "2025-01-15T10:00:00",
      "hasActiveConsent": true
    }
  ]
}
```

---

## 🔗 2. 디바이스 연동 (Connect Device)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `POST /api/v1/integration/devices` |
| **설명** | 디바이스 OAuth 연동 및 동의 기록 |
| **인증** | `Bearer Token` 필수 |
| **난이도** | **상 (High)** |

### Request Body

```json
{
  "vendor": "samsung",
  "deviceType": "watch",
  "authCode": "oauth_auth_code",
  "consentScope": {
    "dataTypes": ["steps", "heartRate", "sleep"],
    "frequency": "realtime",
    "retentionPeriod": "2years"
  }
}
```

### Response Body

**✅ 성공 (201 Created)**

```json
{
  "success": true,
  "message": "디바이스가 연동되었습니다.",
  "data": {
    "deviceId": "550e8400-e29b-41d4-a716-446655440000",
    "vendor": "samsung",
    "deviceType": "watch",
    "status": "ACTIVE",
    "lastSyncAt": "2025-01-15T10:00:00"
  }
}
```

**❌ 실패**

| HTTP | type | title | 원인 |
|:---|:---|:---|:---|
| `409` | `/errors/device-already-linked` | 이미 연동됨 | 동일 벤더 디바이스 이미 연동 |
| `400` | `/errors/invalid-auth-code` | 잘못된 인증 코드 | OAuth 인증 코드 유효하지 않음 |

### Logic Steps

```
1. [Check Existing] 기존 연동 확인
   - DeviceLinkRepository.findByUserIdAndVendor()

2. [Authorize] OAuth 토큰 교환
   - DeviceDataProvider.authorize(authCode)

3. [Encrypt Tokens] 토큰 암호화 저장
   - TokenEncryptionService.encrypt()

4. [Create DeviceLink] DeviceLink 생성
   - DeviceLink.create(user, vendor, type)
   - setTokens(accessToken, refreshToken, expiresAt)

5. [Grant Consent] 동의 기록 생성
   - ConsentService.grantConsent(DEVICE, deviceId, scope)

6. [Initial Sync] 초기 데이터 동기화
   - DeviceDataProvider.getHealthData()
   - HealthDataDaily 저장

7. [Save] DeviceLink 저장

8. [Response] 연동 정보 반환
```

---

## 🔄 3. 디바이스 수동 동기화 (Sync Device)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `POST /api/v1/integration/devices/{deviceId}/sync` |
| **설명** | 디바이스 데이터 수동 동기화 트리거 |
| **인증** | `Bearer Token` 필수 |
| **난이도** | **중 (Medium)** |

### Path Parameters

| 파라미터명 | 타입 | 필수 | 설명 |
|:---|:---|:---:|:---|
| `deviceId` | `UUID` | ✅ | 디바이스 ID |

### Response Body

**✅ 성공 (200 OK)**

```json
{
  "success": true,
  "message": "동기화가 완료되었습니다.",
  "data": {
    "recordsSynced": 7,
    "syncedAt": "2025-01-15T10:30:00",
    "status": "SUCCESS",
    "errors": []
  }
}
```

---

## 🗑️ 4. 디바이스 연동 해제 (Disconnect Device)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `DELETE /api/v1/integration/devices/{deviceId}` |
| **설명** | 디바이스 연동 해제 및 동의 철회 |
| **인증** | `Bearer Token` 필수 |
| **난이도** | **하 (Low)** |

### Response Body

**✅ 성공 (204 No Content)**

### Logic Steps

```
1. [Find Device] 디바이스 조회
2. [Revoke Consent] 관련 동의 철회
3. [Revoke Access] 벤더 측 연동 해제
4. [Mark Revoked] DeviceLink 상태 변경
5. [Save] 저장
```

---

## 🏥 5. 포털 연동 목록 조회 (Get Portal Connections)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `GET /api/v1/integration/portals` |
| **설명** | 사용자의 연동된 병원 포털 목록 조회 |
| **인증** | `Bearer Token` 필수 |
| **난이도** | **하 (Low)** |

### Response Body

**✅ 성공 (200 OK)**

```json
{
  "success": true,
  "message": "조회 성공",
  "data": [
    {
      "portalId": "550e8400-e29b-41d4-a716-446655440000",
      "portalType": "NHIS",
      "portalName": "건강보험심사평가원",
      "status": "ACTIVE",
      "lastSyncAt": "2025-01-15T09:00:00"
    }
  ]
}
```

---

## 🔗 6. 포털 연동 (Connect Portal)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `POST /api/v1/integration/portals` |
| **설명** | 병원 포털 연동 및 검진 결과 조회 |
| **인증** | `Bearer Token` 필수 |
| **난이도** | **상 (High)** |

### Request Body

```json
{
  "portalType": "NHIS",
  "portalId": "user_portal_id",
  "credentials": {
    "certificate": "encrypted_cert_data"
  }
}
```

### Response Body

**✅ 성공 (201 Created)**

```json
{
  "success": true,
  "message": "포털이 연동되었습니다.",
  "data": {
    "portalId": "550e8400-e29b-41d4-a716-446655440000",
    "portalType": "NHIS",
    "portalName": "건강보험심사평가원",
    "status": "ACTIVE",
    "lastSyncAt": "2025-01-15T10:00:00"
  }
}
```

**❌ 실패**

| HTTP | type | title | 원인 |
|:---|:---|:---|:---|
| `400` | `/errors/unsupported-region` | 미지원 지역 | 해당 지역 포털 미지원 |
| `400` | `/errors/invalid-credentials` | 잘못된 인증 정보 | 포털 인증 실패 |

### Logic Steps

```
1. [Check Support] 포털 지원 여부 확인
   - 미지원 지역인 경우 UNSUPPORTED 상태로 저장
   - 파일 업로드 대체 경로 안내

2. [Authenticate] 포털 인증
   - PortalDataProvider.authenticate(credentials)

3. [Create Connection] PortalConnection 생성
   - PortalConnection.create(user, type, id)

4. [Grant Consent] 동의 기록 생성
   - ConsentService.grantConsent(PORTAL, portalId, scope)

5. [Fetch Data] 최근 6개월 검진 결과 조회
   - PortalDataProvider.getCheckupRecords()

6. [Save] PortalConnection 저장

7. [Response] 연동 정보 반환
```

---

## 📤 7. 포털 데이터 파일 업로드 (Upload Portal Data)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `POST /api/v1/integration/portals/upload` |
| **설명** | 미지원 지역 대체 경로 - 검진 결과 파일 업로드 |
| **인증** | `Bearer Token` 필수 |
| **난이도** | **중 (Medium)** |

### Request Body

- Content-Type: `multipart/form-data`
- `file`: PDF/이미지/CSV 파일

### Response Body

**✅ 성공 (201 Created)**

```json
{
  "success": true,
  "message": "파일이 업로드되었습니다.",
  "data": {
    "uploadId": 1,
    "uploadType": "PDF",
    "processingStatus": "PENDING",
    "uploadedAt": "2025-01-15T10:00:00"
  }
}
```

### Logic Steps

```
1. [Validate File] 파일 형식 검증
   - PDF, 이미지, CSV 허용

2. [Save File] 파일 저장
   - FileStorageService.save()

3. [Create Upload] PortalDataUpload 생성
   - status = PENDING

4. [Queue Processing] 데이터 추출 작업 큐에 추가
   - DataExtractionService.extract() (비동기)

5. [Response] 업로드 정보 반환
```

---

## ✅ 8. 동의 목록 조회 (Get Consents)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `GET /api/v1/integration/consents` |
| **설명** | 사용자의 모든 동의 기록 조회 |
| **인증** | `Bearer Token` 필수 |
| **난이도** | **하 (Low)** |

### Response Body

**✅ 성공 (200 OK)**

```json
{
  "success": true,
  "message": "조회 성공",
  "data": [
    {
      "consentId": "550e8400-e29b-41d4-a716-446655440000",
      "subjectType": "DEVICE",
      "subjectName": "Samsung Galaxy Watch",
      "scope": {
        "dataTypes": ["steps", "heartRate", "sleep"],
        "frequency": "realtime"
      },
      "status": "ACTIVE",
      "consentedAt": "2025-01-10T09:00:00"
    }
  ]
}
```

---

## 🗑️ 9. 동의 철회 (Revoke Consent)

| 항목 | 내용 |
|:---|:---|
| **Endpoint** | `DELETE /api/v1/integration/consents/{consentId}` |
| **설명** | 동의 철회 및 관련 연동 해제 |
| **인증** | `Bearer Token` 필수 |
| **난이도** | **중 (Medium)** |

### Path Parameters

| 파라미터명 | 타입 | 필수 | 설명 |
|:---|:---|:---:|:---|
| `consentId` | `UUID` | ✅ | 동의 ID |

### Request Body (Optional)

```json
{
  "revokeReason": "사용자 요청"
}
```

### Response Body

**✅ 성공 (204 No Content)**

### Logic Steps

```
1. [Find Consent] 동의 기록 조회
2. [Revoke Consent] 동의 철회 처리
3. [Disconnect Integration] 관련 연동 해제
   - DEVICE인 경우: DeviceLink.revoke()
   - PORTAL인 경우: PortalConnection.revoke()
4. [Log Audit] 감사 로그 기록
5. [Save] 저장
```

---

## 📊 API 요약 테이블

| # | Method | Endpoint | 설명 | 인증 | 난이도 |
|:---:|:---|:---|:---|:---:|:---:|
| 1 | `GET` | `/api/v1/integration/devices` | 디바이스 목록 조회 | ✅ | 하 |
| 2 | `POST` | `/api/v1/integration/devices` | 디바이스 연동 | ✅ | 상 |
| 3 | `POST` | `/api/v1/integration/devices/{id}/sync` | 수동 동기화 | ✅ | 중 |
| 4 | `DELETE` | `/api/v1/integration/devices/{id}` | 연동 해제 | ✅ | 하 |
| 5 | `GET` | `/api/v1/integration/portals` | 포털 목록 조회 | ✅ | 하 |
| 6 | `POST` | `/api/v1/integration/portals` | 포털 연동 | ✅ | 상 |
| 7 | `POST` | `/api/v1/integration/portals/upload` | 파일 업로드 | ✅ | 중 |
| 8 | `GET` | `/api/v1/integration/consents` | 동의 목록 조회 | ✅ | 하 |
| 9 | `DELETE` | `/api/v1/integration/consents/{id}` | 동의 철회 | ✅ | 중 |

---

## 🏗️ 구현 참고사항

### 1. 디바이스 연동 서비스 예시

```java
@Service
@RequiredArgsConstructor
public class DeviceLinkServiceImpl implements DeviceLinkService {
    
    private final DeviceLinkRepository deviceRepository;
    private final DeviceProviderFactory providerFactory;
    private final ConsentService consentService;
    private final TokenEncryptionService encryptionService;
    
    @Override
    @Transactional
    public DeviceLinkRes connectDevice(UUID userId, DeviceConnectReq req) {
        // 기존 연동 확인
        deviceRepository.findByUserIdAndVendor(userId, req.vendor())
            .ifPresent(d -> {
                throw new DeviceAlreadyLinkedException("이미 연동된 디바이스입니다.");
            });
        
        // OAuth 토큰 교환
        DeviceDataProvider provider = providerFactory.getProvider(req.vendor());
        TokenResponse tokenResponse = provider.authorize(req.authCode(), redirectUri);
        
        // 토큰 암호화
        String encryptedAccessToken = encryptionService.encrypt(tokenResponse.accessToken());
        String encryptedRefreshToken = encryptionService.encrypt(tokenResponse.refreshToken());
        
        // DeviceLink 생성
        DeviceLink deviceLink = DeviceLink.create(user, req.vendor(), req.deviceType());
        deviceLink.setTokens(encryptedAccessToken, encryptedRefreshToken, 
                            LocalDateTime.now().plusSeconds(tokenResponse.expiresIn()));
        
        DeviceLink savedDevice = deviceRepository.save(deviceLink);
        
        // 동의 기록
        consentService.grantConsent(userId, ConsentGrantReq.builder()
            .subjectType(ConsentSubjectType.DEVICE)
            .subjectId(savedDevice.getId())
            .consentScope(req.consentScope())
            .build());
        
        // 초기 데이터 동기화
        syncDevice(userId, savedDevice.getId());
        
        return DeviceLinkRes.from(savedDevice);
    }
}
```

### 2. DTO 예시

```java
public record DeviceConnectReq(
    @NotBlank String vendor,
    @NotBlank String deviceType,
    @NotBlank String authCode,
    @NotNull ConsentScopeDto consentScope
) {}

public record ConsentScopeDto(
    @NotEmpty List<String> dataTypes,
    @NotBlank String frequency,
    String retentionPeriod,
    Map<String, Boolean> sharingAllowed
) {}

public record DeviceLinkRes(
    UUID deviceId,
    String vendor,
    String deviceType,
    DeviceStatus status,
    LocalDateTime lastSyncAt,
    boolean hasActiveConsent
) {}
```

---

## 📝 참고 자료

- [BE] Issue-08: 외부 연동 (Device & Portal) 및 동의 관리 (`studio/Tasks/BE_issue/issue-08-integration.md`)
- SRS REQ-FUNC-003, 004, 019
- SRS 6.2.6 ~ 6.2.9 (Consent, DeviceLink, PortalConnection)
- Java Spring Boot 3.x Cursor Rules (`.cursor/rules/300-java-spring-cursor-rules.mdc`)

