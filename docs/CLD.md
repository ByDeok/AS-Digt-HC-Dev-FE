# Component Level Diagram (CLD)

## 개요

본 문서는 **AS-Digt-HC-Dev-FE** 프로젝트의 컴포넌트 레벨 다이어그램(CLD)을 정의합니다. 
백엔드(Spring Boot)와 프론트엔드(React + Vite)의 컴포넌트 구조, 계층 관계, 의존성을 시각화합니다.

---

## 전체 시스템 아키텍처

```mermaid
graph TB
    subgraph "Client Layer"
        WEB[Web Browser]
        MOBILE[Mobile App/PWA]
    end
    
    subgraph "Frontend (React + Vite)"
        ROUTER[React Router]
        PAGES[Page Components]
        COMPONENTS[UI Components]
        HOOKS[Custom Hooks]
        STORE[State Management]
        AI_CLIENT[AI Client/Genkit]
    end
    
    subgraph "API Gateway / Load Balancer"
        LB[Load Balancer]
    end
    
    subgraph "Backend (Spring Boot)"
        CONTROLLERS[Controller Layer]
        SERVICES[Service Layer]
        REPOS[Repository Layer]
        ENTITIES[Domain Entities]
        INFRA[Infrastructure]
    end
    
    subgraph "External Services"
        DB[(MySQL Database)]
        REDIS[(Redis Cache)]
        KAFKA[Kafka Message Queue]
        OAUTH[OAuth Providers]
        DEVICE[Device SDKs]
        PORTAL[Hospital Portals]
    end
    
    WEB --> ROUTER
    MOBILE --> ROUTER
    ROUTER --> PAGES
    PAGES --> COMPONENTS
    PAGES --> HOOKS
    HOOKS --> STORE
    HOOKS --> AI_CLIENT
    COMPONENTS --> HOOKS
    
    PAGES --> LB
    LB --> CONTROLLERS
    CONTROLLERS --> SERVICES
    SERVICES --> REPOS
    REPOS --> ENTITIES
    SERVICES --> INFRA
    REPOS --> DB
    INFRA --> REDIS
    INFRA --> KAFKA
    INFRA --> OAUTH
    INFRA --> DEVICE
    INFRA --> PORTAL
```

---

## 백엔드 컴포넌트 구조

### 1. 레이어별 컴포넌트 다이어그램

```mermaid
graph TB
    subgraph "Interface Layer (interfaces/)"
        AUTH_CTRL[AuthController]
        ONBOARD_CTRL[OnboardingController]
        REPORT_CTRL[ReportController]
        ACTION_CTRL[ActionController]
        FAMILY_CTRL[FamilyBoardController]
        INTEG_CTRL[IntegrationController]
        HEALTH_CTRL[HealthController]
        EXCEPTION[GlobalExceptionHandler]
        RESPONSE[ApiResponse/ErrorResponse]
    end
    
    subgraph "Domain Layer (domain/)"
        subgraph "User Domain"
            USER_SVC[AuthService]
            USER_REPO[UserRepository]
            USER_ENTITY[User/UserProfile]
        end
        
        subgraph "Onboarding Domain"
            ONBOARD_SVC[OnboardingService]
            ONBOARD_REPO[OnboardingSessionRepository]
            ONBOARD_ENTITY[OnboardingSession]
        end
        
        subgraph "Report Domain"
            REPORT_SVC[HealthReportService]
            REPORT_REPO[HealthReportRepository]
            REPORT_ENTITY[HealthReport]
            REPORT_CONV[ReportMetricsConverter]
        end
        
        subgraph "Action Domain"
            ACTION_SVC[ActionCardService]
            ACTION_SCHED[ActionScheduler]
            ACTION_REPO[ActionCardRepository]
            ACTION_ENTITY[ActionCard]
        end
        
        subgraph "Family Domain"
            FAMILY_SVC[FamilyBoardService]
            INVITE_SVC[InvitationService]
            PERM_SVC[PermissionService]
            FAMILY_REPO[FamilyBoardRepository]
            MEMBER_REPO[FamilyBoardMemberRepository]
            INVITE_REPO[BoardInvitationRepository]
            FAMILY_ENTITY[FamilyBoard/Member/Invitation]
        end
        
        subgraph "Integration Domain"
            DEVICE_SVC[DeviceLinkService]
            PORTAL_SVC[PortalConnectionService]
            CONSENT_SVC[ConsentService]
            DEVICE_REPO[DeviceLinkRepository]
            PORTAL_REPO[PortalConnectionRepository]
            CONSENT_REPO[ConsentRecordRepository]
            INTEG_ENTITY[DeviceLink/PortalConnection/ConsentRecord]
        end
    end
    
    subgraph "Infrastructure Layer (infrastructure/)"
        SECURITY[SecurityConfig/JwtTokenProvider]
        SCHEDULER[SyncScheduler]
        DEVICE_PROVIDER[DeviceProviderFactory]
        PORTAL_PROVIDER[PortalProviderFactory]
        MOCK_PROVIDER[MockDeviceProvider/MockPortalProvider]
    end
    
    AUTH_CTRL --> USER_SVC
    ONBOARD_CTRL --> ONBOARD_SVC
    REPORT_CTRL --> REPORT_SVC
    ACTION_CTRL --> ACTION_SVC
    FAMILY_CTRL --> FAMILY_SVC
    INTEG_CTRL --> DEVICE_SVC
    INTEG_CTRL --> PORTAL_SVC
    INTEG_CTRL --> CONSENT_SVC
    
    USER_SVC --> USER_REPO
    ONBOARD_SVC --> ONBOARD_REPO
    REPORT_SVC --> REPORT_REPO
    ACTION_SVC --> ACTION_REPO
    FAMILY_SVC --> FAMILY_REPO
    FAMILY_SVC --> MEMBER_REPO
    INVITE_SVC --> INVITE_REPO
    DEVICE_SVC --> DEVICE_REPO
    PORTAL_SVC --> PORTAL_REPO
    CONSENT_SVC --> CONSENT_REPO
    
    USER_REPO --> USER_ENTITY
    ONBOARD_REPO --> ONBOARD_ENTITY
    REPORT_REPO --> REPORT_ENTITY
    ACTION_REPO --> ACTION_ENTITY
    FAMILY_REPO --> FAMILY_ENTITY
    DEVICE_REPO --> INTEG_ENTITY
    
    DEVICE_SVC --> DEVICE_PROVIDER
    PORTAL_SVC --> PORTAL_PROVIDER
    SCHEDULER --> DEVICE_SVC
    SCHEDULER --> PORTAL_SVC
    
    SECURITY -.-> AUTH_CTRL
    SECURITY -.-> USER_SVC
```

---

## 프론트엔드 컴포넌트 구조

### 1. 페이지 및 라우팅 구조

```mermaid
graph TB
    subgraph "App Entry"
        MAIN[main.tsx]
        APP[App.tsx]
    end
    
    subgraph "Routing Layer"
        ROUTER[BrowserRouter]
        ROUTES[Routes]
    end
    
    subgraph "Page Components (app/)"
        LANDING[LandingPage /]
        ONBOARD_LAYOUT[OnboardingLayout]
        ONBOARD_PROFILE[ProfilePage /onboarding]
        ONBOARD_DEVICE[DevicePage /onboarding/device]
        ONBOARD_COMPLETE[CompletePage /onboarding/complete]
        MAIN_LAYOUT[MainLayout]
        DASHBOARD[DashboardPage /dashboard]
        REPORT[ReportPage /report]
        FAMILY[FamilyPage /family]
    end
    
    subgraph "UI Components (components/)"
        UI_PRIMITIVES[shadcn/ui Primitives]
        UI_COMMON[Common Components]
        UI_LAYOUT[Layout Components]
    end
    
    subgraph "Business Logic"
        HOOKS[Custom Hooks]
        STORE[State Store]
        API_CLIENT[API Client]
        AI_GENKIT[AI Genkit Client]
    end
    
    MAIN --> APP
    APP --> ROUTER
    ROUTER --> ROUTES
    ROUTES --> LANDING
    ROUTES --> ONBOARD_LAYOUT
    ROUTES --> MAIN_LAYOUT
    
    ONBOARD_LAYOUT --> ONBOARD_PROFILE
    ONBOARD_LAYOUT --> ONBOARD_DEVICE
    ONBOARD_LAYOUT --> ONBOARD_COMPLETE
    
    MAIN_LAYOUT --> DASHBOARD
    MAIN_LAYOUT --> REPORT
    MAIN_LAYOUT --> FAMILY
    
    DASHBOARD --> UI_PRIMITIVES
    DASHBOARD --> UI_COMMON
    REPORT --> UI_PRIMITIVES
    REPORT --> UI_COMMON
    FAMILY --> UI_PRIMITIVES
    FAMILY --> UI_COMMON
    
    DASHBOARD --> HOOKS
    REPORT --> HOOKS
    FAMILY --> HOOKS
    
    HOOKS --> STORE
    HOOKS --> API_CLIENT
    HOOKS --> AI_GENKIT
```

---

## 도메인별 컴포넌트 상세

### 1. 사용자 인증 도메인

```mermaid
graph LR
    AUTH_CTRL[AuthController] --> AUTH_SVC[AuthService]
    AUTH_SVC --> USER_REPO[UserRepository]
    AUTH_SVC --> JWT_PROVIDER[JwtTokenProvider]
    AUTH_SVC --> USER_DETAILS[CustomUserDetailsService]
    USER_REPO --> USER_ENTITY[User Entity]
    JWT_PROVIDER --> SECURITY_CONFIG[SecurityConfig]
    SECURITY_CONFIG --> JWT_FILTER[JwtAuthenticationFilter]
```

**주요 컴포넌트:**
- `AuthController`: 로그인, 회원가입, 토큰 갱신 API
- `AuthService`: 인증 비즈니스 로직
- `JwtTokenProvider`: JWT 토큰 생성/검증
- `CustomUserDetailsService`: 사용자 정보 로드
- `SecurityConfig`: Spring Security 설정

---

### 2. 온보딩 도메인

```mermaid
graph LR
    ONBOARD_CTRL[OnboardingController] --> ONBOARD_SVC[OnboardingService]
    ONBOARD_SVC --> ONBOARD_REPO[OnboardingSessionRepository]
    ONBOARD_SVC --> USER_SVC[AuthService]
    ONBOARD_SVC --> DEVICE_SVC[DeviceLinkService]
    ONBOARD_SVC --> PORTAL_SVC[PortalConnectionService]
    ONBOARD_REPO --> ONBOARD_ENTITY[OnboardingSession]
```

**주요 컴포넌트:**
- `OnboardingController`: 온보딩 단계별 API
- `OnboardingService`: 온보딩 플로우 관리
- `OnboardingSession`: 진행 상태 추적

---

### 3. 건강 리포트 도메인

```mermaid
graph LR
    REPORT_CTRL[ReportController] --> REPORT_SVC[HealthReportService]
    REPORT_SVC --> REPORT_REPO[HealthReportRepository]
    REPORT_SVC --> DEVICE_SVC[DeviceLinkService]
    REPORT_SVC --> PORTAL_SVC[PortalConnectionService]
    REPORT_SVC --> METRICS_CONV[ReportMetricsConverter]
    REPORT_SVC --> CONTEXT_CONV[ReportContextConverter]
    REPORT_REPO --> REPORT_ENTITY[HealthReport]
```

**주요 컴포넌트:**
- `ReportController`: 리포트 생성/조회 API
- `HealthReportService`: 리포트 생성 로직
- `ReportMetricsConverter`: 지표 데이터 변환
- `ReportContextConverter`: 맥락 정보 변환

---

### 4. 행동 코칭 도메인

```mermaid
graph LR
    ACTION_CTRL[ActionController] --> ACTION_SVC[ActionCardService]
    ACTION_SVC --> ACTION_REPO[ActionCardRepository]
    ACTION_SVC --> ACTION_SCHED[ActionScheduler]
    ACTION_SCHED --> ACTION_SVC
    ACTION_REPO --> ACTION_ENTITY[ActionCard]
```

**주요 컴포넌트:**
- `ActionController`: 행동 카드 조회/완료 API
- `ActionCardService`: 행동 카드 생성/관리
- `ActionScheduler`: 일일 배치 스케줄러

---

### 5. 가족 보드 도메인

```mermaid
graph TB
    FAMILY_CTRL[FamilyBoardController] --> FAMILY_SVC[FamilyBoardService]
    FAMILY_CTRL --> INVITE_SVC[InvitationService]
    FAMILY_SVC --> FAMILY_REPO[FamilyBoardRepository]
    FAMILY_SVC --> MEMBER_REPO[FamilyBoardMemberRepository]
    FAMILY_SVC --> PERM_SVC[PermissionService]
    INVITE_SVC --> INVITE_REPO[BoardInvitationRepository]
    INVITE_SVC --> PERM_SVC
    FAMILY_REPO --> FAMILY_ENTITY[FamilyBoard]
    MEMBER_REPO --> MEMBER_ENTITY[FamilyBoardMember]
    INVITE_REPO --> INVITE_ENTITY[BoardInvitation]
```

**주요 컴포넌트:**
- `FamilyBoardController`: 보드 조회/설정 API
- `FamilyBoardService`: 보드 관리 로직
- `InvitationService`: 초대 관리
- `PermissionService`: 권한 검증

---

### 6. 외부 연동 도메인

```mermaid
graph TB
    INTEG_CTRL[IntegrationController] --> DEVICE_SVC[DeviceLinkService]
    INTEG_CTRL --> PORTAL_SVC[PortalConnectionService]
    INTEG_CTRL --> CONSENT_SVC[ConsentService]
    
    DEVICE_SVC --> DEVICE_REPO[DeviceLinkRepository]
    DEVICE_SVC --> DEVICE_FACTORY[DeviceProviderFactory]
    DEVICE_FACTORY --> MOCK_DEVICE[MockDeviceProvider]
    DEVICE_FACTORY --> REAL_DEVICE[RealDeviceProvider]
    
    PORTAL_SVC --> PORTAL_REPO[PortalConnectionRepository]
    PORTAL_SVC --> PORTAL_FACTORY[PortalProviderFactory]
    PORTAL_FACTORY --> MOCK_PORTAL[MockPortalProvider]
    PORTAL_FACTORY --> REAL_PORTAL[RealPortalProvider]
    
    CONSENT_SVC --> CONSENT_REPO[ConsentRecordRepository]
    
    DEVICE_REPO --> DEVICE_ENTITY[DeviceLink]
    PORTAL_REPO --> PORTAL_ENTITY[PortalConnection]
    CONSENT_REPO --> CONSENT_ENTITY[ConsentRecord]
    
    SCHEDULER[SyncScheduler] --> DEVICE_SVC
    SCHEDULER --> PORTAL_SVC
```

**주요 컴포넌트:**
- `IntegrationController`: 연동 관리 API
- `DeviceLinkService`: 디바이스 연동 관리
- `PortalConnectionService`: 포털 연동 관리
- `ConsentService`: 동의 기록 관리
- `DeviceProviderFactory`: 디바이스 제공자 팩토리
- `PortalProviderFactory`: 포털 제공자 팩토리
- `SyncScheduler`: 주기적 동기화 스케줄러

---

## 인프라스트럭처 컴포넌트

### 1. 보안 인프라

```mermaid
graph TB
    SECURITY_CONFIG[SecurityConfig] --> JWT_FILTER[JwtAuthenticationFilter]
    SECURITY_CONFIG --> USER_DETAILS[CustomUserDetailsService]
    JWT_FILTER --> JWT_PROVIDER[JwtTokenProvider]
    USER_DETAILS --> USER_REPO[UserRepository]
```

### 2. 스케줄러 인프라

```mermaid
graph LR
    SYNC_SCHEDULER[SyncScheduler] --> DEVICE_SYNC[Device Sync]
    SYNC_SCHEDULER --> PORTAL_SYNC[Portal Sync]
    SYNC_SCHEDULER --> ACTION_GEN[Action Card Generation]
```

### 3. 외부 통신 인프라

```mermaid
graph TB
    DEVICE_FACTORY[DeviceProviderFactory] --> SAMSUNG[Samsung Provider]
    DEVICE_FACTORY --> APPLE[Apple Provider]
    DEVICE_FACTORY --> MOCK[Mock Provider]
    
    PORTAL_FACTORY[PortalProviderFactory] --> HOSPITAL_A[Hospital A Provider]
    PORTAL_FACTORY --> HOSPITAL_B[Hospital B Provider]
    PORTAL_FACTORY --> MOCK_PORTAL[Mock Portal Provider]
```

---

## 데이터 흐름 다이어그램

### 1. 온보딩 플로우

```mermaid
sequenceDiagram
    participant Client as Frontend
    participant Controller as OnboardingController
    participant Service as OnboardingService
    participant Repo as OnboardingRepository
    participant Device as DeviceLinkService
    participant Portal as PortalConnectionService
    participant DB as Database
    
    Client->>Controller: POST /onboarding/start
    Controller->>Service: startOnboarding(userId)
    Service->>Repo: createSession(userId)
    Repo->>DB: INSERT onboarding_sessions
    Service-->>Controller: OnboardingSession
    Controller-->>Client: Session Response
    
    Client->>Controller: POST /onboarding/device
    Controller->>Service: linkDevice(userId, deviceInfo)
    Service->>Device: connectDevice(userId, deviceInfo)
    Device->>DB: INSERT device_links
    Service->>Repo: updateStep(sessionId, DEVICE)
    Service-->>Controller: Success
    Controller-->>Client: Device Linked
```

### 2. 리포트 생성 플로우

```mermaid
sequenceDiagram
    participant Client as Frontend
    participant Controller as ReportController
    participant Service as HealthReportService
    participant Device as DeviceLinkService
    participant Portal as PortalConnectionService
    participant Repo as HealthReportRepository
    participant DB as Database
    
    Client->>Controller: POST /reports/generate
    Controller->>Service: generateReport(userId, period)
    Service->>Device: fetchDeviceData(userId, period)
    Device-->>Service: HealthData
    Service->>Portal: fetchPortalData(userId, period)
    Portal-->>Service: MedicalData
    Service->>Service: aggregateMetrics(data)
    Service->>Service: buildContext(devices, period)
    Service->>Repo: save(report)
    Repo->>DB: INSERT health_reports
    Service-->>Controller: HealthReport
    Controller-->>Client: Report Response
```

### 3. 행동 카드 생성 플로우

```mermaid
sequenceDiagram
    participant Scheduler as ActionScheduler
    participant Service as ActionCardService
    participant Repo as ActionCardRepository
    participant Report as HealthReportService
    participant DB as Database
    participant Notification as NotificationService
    
    Scheduler->>Service: generateDailyActions()
    Service->>Report: getLatestReport(userId)
    Report-->>Service: HealthReport
    Service->>Service: evaluateRules(report, userProfile)
    Service->>Service: createActionCards(1-3 cards)
    Service->>Repo: saveAll(cards)
    Repo->>DB: INSERT action_cards
    Service->>Notification: sendNotifications(cards)
    Notification-->>Service: Sent
```

---

## 컴포넌트 의존성 규칙

### 1. 레이어 의존성 방향

```
Controller → Service → Repository → Entity
     ↓           ↓
  DTO/Response  Domain Logic
```

### 2. 허용되는 의존성

- ✅ Controller는 Service에만 의존
- ✅ Service는 Repository와 다른 Service에 의존 가능
- ✅ Repository는 Entity에만 의존
- ✅ Infrastructure는 모든 레이어에서 사용 가능

### 3. 금지되는 의존성

- ❌ Controller가 Repository에 직접 의존
- ❌ Service가 Controller에 의존
- ❌ Entity가 Service나 Repository에 의존
- ❌ Domain이 Infrastructure에 직접 의존 (인터페이스 통해서만)

---

## 컴포넌트 통신 방식

### 1. 동기 통신
- **REST API**: Frontend ↔ Backend
- **JPA Repository**: Service ↔ Database
- **Service Method Call**: Service ↔ Service

### 2. 비동기 통신
- **Kafka**: 서비스 간 이벤트 전달 (Post-MVP)
- **Scheduler**: 주기적 배치 작업
- **WebSocket**: 실시간 동기화 (Post-MVP)

### 3. 외부 통신
- **OAuth2/OIDC**: 소셜 로그인
- **Device SDK API**: 디바이스 데이터 수집
- **Hospital Portal API**: 병원 포털 연동

---

## 컴포넌트 확장 전략

### 1. 새로운 디바이스 추가
```
DeviceProviderFactory
    ├── SamsungProvider (기존)
    ├── AppleProvider (기존)
    └── NewDeviceProvider (추가) ← 구현만 추가
```

### 2. 새로운 포털 추가
```
PortalProviderFactory
    ├── HospitalAProvider (기존)
    ├── HospitalBProvider (기존)
    └── NewPortalProvider (추가) ← 구현만 추가
```

### 3. 새로운 도메인 추가
```
domain/
    ├── user/ (기존)
    ├── report/ (기존)
    └── new_domain/ (추가)
        ├── entity/
        ├── repository/
        ├── service/
        └── dto/
```

---

## 버전 정보

- **작성일**: 2025-01-27
- **버전**: 1.0
- **작성자**: AI Assistant
- **참고 문서**: 
  - `docs/01-component-structure-analysis.md`
  - `docs/04-function-call-hierarchy.md`
  - `docs/SRS/SRS_V0.3.md`
