/**
 * Google Analytics 4 통합 모듈
 *
 * 사용자 행동 추적 및 이벤트 전송을 담당합니다.
 * - ULID 기반 세션 ID로 프론트엔드-백엔드 로그 연결
 * - 환경변수로 추적 활성화/비활성화
 *
 * @see docs/GOOGLE_ANALYTICS_GUIDE.md
 */

// GA4 측정 ID (환경변수에서 로드)
const GA_MEASUREMENT_ID = import.meta.env.VITE_GA_MEASUREMENT_ID as string | undefined;

// 개발 환경에서 추적 비활성화 옵션
const IS_TRACKING_ENABLED =
  import.meta.env.PROD || import.meta.env.VITE_ENABLE_ANALYTICS === 'true';

/**
 * gtag 함수 타입 정의
 */
declare global {
  interface Window {
    gtag: (...args: unknown[]) => void;
    dataLayer: unknown[];
  }
}

// GA4 초기화 상태
let isInitialized = false;

/**
 * GA4 초기화
 * App.tsx에서 앱 마운트 시 1회 호출
 */
export function initGA(): void {
  // 이미 초기화된 경우 스킵
  if (isInitialized) {
    return;
  }

  // 추적 비활성화 또는 측정 ID 미설정 시 스킵
  if (!IS_TRACKING_ENABLED || !GA_MEASUREMENT_ID) {
    console.log('[Analytics] Tracking disabled or Measurement ID not set');
    return;
  }

  // gtag.js 스크립트 로드
  const script = document.createElement('script');
  script.async = true;
  script.src = `https://www.googletagmanager.com/gtag/js?id=${GA_MEASUREMENT_ID}`;
  document.head.appendChild(script);

  // dataLayer 초기화
  window.dataLayer = window.dataLayer || [];
  window.gtag = function gtag(...args: unknown[]) {
    window.dataLayer.push(args);
  };

  window.gtag('js', new Date());
  window.gtag('config', GA_MEASUREMENT_ID, {
    send_page_view: false, // 수동으로 페이지뷰 전송 (SPA 라우팅 대응)
    debug_mode: import.meta.env.DEV,
  });

  isInitialized = true;
  console.log('[Analytics] GA4 initialized with ID:', GA_MEASUREMENT_ID);
}

/**
 * 페이지뷰 이벤트 전송
 * @param pagePath - 페이지 경로 (예: "/dashboard")
 * @param pageTitle - 페이지 제목 (선택)
 */
export function trackPageView(pagePath: string, pageTitle?: string): void {
  if (!IS_TRACKING_ENABLED || !isInitialized) return;

  window.gtag?.('event', 'page_view', {
    page_path: pagePath,
    page_title: pageTitle || document.title,
    page_location: window.location.href,
  });

  // 개발 환경에서 콘솔 로그
  if (import.meta.env.DEV) {
    console.log('[Analytics] page_view:', pagePath);
  }
}

/**
 * 커스텀 이벤트 전송
 * @param eventName - 이벤트 이름 (예: "signup_complete")
 * @param params - 이벤트 매개변수
 */
export function trackEvent(eventName: string, params?: Record<string, unknown>): void {
  if (!IS_TRACKING_ENABLED || !isInitialized) return;

  window.gtag?.('event', eventName, {
    ...params,
    timestamp: Date.now(),
  });

  // 개발 환경에서 콘솔 로그
  if (import.meta.env.DEV) {
    console.log('[Analytics Event]', eventName, params);
  }
}

/**
 * 사용자 속성 설정
 * @param properties - 사용자 속성 객체
 */
export function setUserProperties(properties: Record<string, unknown>): void {
  if (!IS_TRACKING_ENABLED || !isInitialized) return;

  window.gtag?.('set', 'user_properties', properties);

  if (import.meta.env.DEV) {
    console.log('[Analytics] User properties set:', properties);
  }
}

/**
 * 사용자 ID 설정 (로그인 시 호출)
 * @param userId - 사용자 ID 또는 null (로그아웃 시)
 */
export function setUserId(userId: string | null): void {
  if (!IS_TRACKING_ENABLED || !isInitialized || !GA_MEASUREMENT_ID) return;

  window.gtag?.('config', GA_MEASUREMENT_ID, {
    user_id: userId,
  });

  if (import.meta.env.DEV) {
    console.log('[Analytics] User ID set:', userId);
  }
}

// ============================================
// 사전 정의된 이벤트 헬퍼 함수
// 가이드 문서의 이벤트 설계에 맞춰 구현
// ============================================

// --- 인증 관련 이벤트 ---

/** 회원가입 페이지 진입 */
export const trackSignupStart = (sourcePage: string) =>
  trackEvent('signup_start', { source_page: sourcePage });

/** 회원가입 폼 첫 상호작용 */
export const trackSignupFormInteract = (fieldName: string) =>
  trackEvent('signup_form_interact', { field_name: fieldName });

/** 회원가입 완료 */
export const trackSignupComplete = (timeToCompleteSec: number) =>
  trackEvent('signup_complete', { time_to_complete_sec: timeToCompleteSec });

/** 회원가입 실패 */
export const trackSignupError = (errorType: string, errorField?: string) =>
  trackEvent('signup_error', { error_type: errorType, error_field: errorField });

/** 로그인 성공 */
export const trackLoginSuccess = (method: string) =>
  trackEvent('login_success', { login_method: method });

/** 로그인 실패 */
export const trackLoginFail = (errorType: string) =>
  trackEvent('login_fail', { error_type: errorType });

// --- 온보딩 관련 이벤트 ---

/** 온보딩 시작 */
export const trackOnboardingStart = () => trackEvent('onboarding_start', {});

/** 온보딩 단계 시작 */
export const trackOnboardingStepView = (stepName: string, stepNumber: number) =>
  trackEvent('onboarding_step_view', {
    step_name: stepName,
    step_number: stepNumber,
  });

/** 온보딩 단계 완료 */
export const trackOnboardingStepComplete = (
  stepName: string,
  stepNumber: number,
  durationSec: number,
  fieldsCompleted?: number,
  totalFields?: number
) =>
  trackEvent('onboarding_step_complete', {
    step_name: stepName,
    step_number: stepNumber,
    step_duration_sec: durationSec,
    fields_completed: fieldsCompleted,
    total_fields: totalFields,
  });

/** 온보딩 완료 */
export const trackOnboardingComplete = (totalDurationSec: number, devicesConnected: number = 0) =>
  trackEvent('onboarding_complete', {
    total_duration_sec: totalDurationSec,
    devices_connected: devicesConnected,
  });

// --- 미션 관련 이벤트 ---

/** 미션 목록 조회 */
export const trackMissionView = (missionsCount: number, completedCount: number) =>
  trackEvent('mission_view', {
    missions_count: missionsCount,
    completed_count: completedCount,
  });

/** 미션 완료 */
export const trackMissionComplete = (
  missionId: string,
  missionTitle: string,
  category?: string,
  isFirstMissionToday?: boolean
) =>
  trackEvent('mission_complete', {
    mission_id: missionId,
    mission_title: missionTitle,
    mission_category: category,
    is_first_mission_today: isFirstMissionToday,
  });

/** 미션 스킵 */
export const trackMissionSkip = (missionId: string, missionTitle: string, reason?: string) =>
  trackEvent('mission_skip', {
    mission_id: missionId,
    mission_title: missionTitle,
    skip_reason: reason,
  });

// --- 건강 기록 관련 이벤트 ---

/** 건강 기록 모달 열기 */
export const trackHealthRecordOpen = () => trackEvent('health_record_open', {});

/** 건강 기록 제출 */
export const trackHealthRecordSubmit = (
  recordDate: string,
  metricsTypes: string[],
  metricsCount: number
) =>
  trackEvent('health_record_submit', {
    record_date: recordDate,
    metrics_count: metricsCount,
    metrics_types: metricsTypes.join(','),
    has_blood_pressure: metricsTypes.includes('bloodPressure'),
    has_weight: metricsTypes.includes('weight'),
    has_steps: metricsTypes.includes('steps'),
    has_heart_rate: metricsTypes.includes('heartRate'),
  });

// --- 리포트 관련 이벤트 ---

/** 리포트 페이지 조회 */
export const trackReportPageView = (reportsCount: number) =>
  trackEvent('report_page_view', { reports_count: reportsCount });

/** 리포트 유형 토글 */
export const trackReportTypeToggle = (reportType: 'weekly' | 'monthly') =>
  trackEvent('report_type_toggle', { report_type: reportType });

/** 리포트 생성 시도 */
export const trackReportGenerateClick = (reportType: 'weekly' | 'monthly') =>
  trackEvent('report_generate_click', { report_type: reportType });

/** 리포트 생성 성공 */
export const trackReportGenerateSuccess = (
  reportType: 'weekly' | 'monthly',
  reportId: string,
  dataCoverage?: number
) =>
  trackEvent('report_generate_success', {
    report_type: reportType,
    report_id: reportId,
    data_coverage: dataCoverage,
  });

/** 리포트 상세 조회 */
export const trackReportView = (reportId: string, reportType: string) =>
  trackEvent('report_view', { report_id: reportId, report_type: reportType });

/** 리포트 공유 */
export const trackReportShare = (reportId: string, shareMethod: string) =>
  trackEvent('report_share', { report_id: reportId, share_method: shareMethod });

/** 리포트 삭제 */
export const trackReportDelete = (reportId: string) =>
  trackEvent('report_delete', { report_id: reportId });

// --- 가족 보드 관련 이벤트 ---

/** 가족 보드 페이지 조회 */
export const trackFamilyBoardView = (memberCount: number, userRole: string) =>
  trackEvent('family_board_view', {
    member_count: memberCount,
    user_role: userRole,
  });

/** 가족 초대 모달 열기 */
export const trackFamilyInviteOpen = () => trackEvent('family_invite_open', {});

/** 가족 초대 발송 */
export const trackFamilyInviteSend = (intendedRole: string, inviterRole?: string) =>
  trackEvent('family_invite_send', {
    intended_role: intendedRole,
    inviter_role: inviterRole,
  });

/** 초대 코드 복사 */
export const trackFamilyInviteCopyCode = () => trackEvent('family_invite_copy_code', {});

/** 가족 참여 시도 */
export const trackFamilyJoinAttempt = () => trackEvent('family_join_attempt', {});

/** 가족 참여 성공 */
export const trackFamilyJoinSuccess = (inviteSource?: string) =>
  trackEvent('family_join_success', { invite_source: inviteSource });

/** 가족 멤버 역할 변경 */
export const trackFamilyMemberRoleChange = (oldRole: string, newRole: string) =>
  trackEvent('family_member_role_change', {
    old_role: oldRole,
    new_role: newRole,
  });

// --- 랜딩 페이지 관련 이벤트 ---

/** CTA 버튼 클릭 */
export const trackCtaClick = (ctaName: string, ctaLocation: string) =>
  trackEvent('click_cta', {
    cta_name: ctaName,
    cta_location: ctaLocation,
  });

/** 스크롤 깊이 */
export const trackScrollDepth = (depth: number) =>
  trackEvent('scroll_depth', { depth_percent: depth });

/** 섹션 노출 */
export const trackSectionView = (sectionName: string) =>
  trackEvent('view_section', { section_name: sectionName });

/** 이탈 의도 감지 */
export const trackExitIntent = (scrollDepth: number, timeOnPageSec: number) =>
  trackEvent('exit_intent', {
    scroll_depth: scrollDepth,
    time_on_page_sec: timeOnPageSec,
  });

// --- 기기 연동 관련 이벤트 ---

/** 기기 연동 시도 */
export const trackDeviceConnectAttempt = (deviceType: string, vendor: string) =>
  trackEvent('device_connect_attempt', {
    device_type: deviceType,
    vendor: vendor,
  });

/** 기기 연동 성공 */
export const trackDeviceConnectSuccess = (deviceType: string, vendor: string) =>
  trackEvent('device_connect_success', {
    device_type: deviceType,
    vendor: vendor,
  });

/** 기기 연동 실패 */
export const trackDeviceConnectFail = (deviceType: string, vendor: string, errorType: string) =>
  trackEvent('device_connect_fail', {
    device_type: deviceType,
    vendor: vendor,
    error_type: errorType,
  });

/** 기기 연동 건너뛰기 */
export const trackDeviceStepSkip = () => trackEvent('device_step_skip', {});

// --- 에러 추적 이벤트 ---

/** 일반 에러 발생 */
export const trackError = (errorType: string, errorMessage: string, errorLocation: string) =>
  trackEvent('error_occurred', {
    error_type: errorType,
    error_message: errorMessage,
    error_location: errorLocation,
  });

/** API 에러 발생 */
export const trackApiError = (endpoint: string, statusCode: number, errorMessage: string) =>
  trackEvent('api_error', {
    endpoint: endpoint,
    status_code: statusCode,
    error_message: errorMessage,
  });

// --- 유틸리티 함수 ---

/**
 * 추적 활성화 여부 확인
 */
export function isTrackingEnabled(): boolean {
  return IS_TRACKING_ENABLED && isInitialized;
}

/**
 * 개발 환경에서 현재 추적 상태 출력
 */
export function printAnalyticsStatus(): void {
  console.log('[Analytics Status]', {
    isInitialized,
    isTrackingEnabled: IS_TRACKING_ENABLED,
    measurementId: GA_MEASUREMENT_ID ? `${GA_MEASUREMENT_ID.slice(0, 5)}...` : 'not set',
    environment: import.meta.env.DEV ? 'development' : 'production',
  });
}
