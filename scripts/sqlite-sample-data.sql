PRAGMA foreign_keys = ON;

-- Users
INSERT INTO users (user_id, email, password, role, status, auth_provider, provider_id, created_at, updated_at)
VALUES
  ('u_admin_001', 'admin@example.com', 'hashed_password', 'ADMIN', 'ACTIVE', 'LOCAL', NULL, datetime('now'), datetime('now')),
  ('u_senior_001', 'senior@example.com', 'hashed_password', 'SENIOR', 'ACTIVE', 'LOCAL', NULL, datetime('now'), datetime('now')),
  ('u_guardian_001', 'guardian@example.com', 'hashed_password', 'GUARDIAN', 'ACTIVE', 'LOCAL', NULL, datetime('now'), datetime('now'));

-- User Profiles
INSERT INTO user_profiles (profile_id, user_id, name, phone_number, profile_image_url, bio, birth_date, age, gender, primary_conditions, accessibility_prefs, created_at, updated_at)
VALUES
  ('p_admin_001', 'u_admin_001', '관리자', '010-9999-0000', NULL, '시스템 관리자 계정입니다.', '1990-01-01', 35, 'MALE', '[]', '{"admin":true}', datetime('now'), datetime('now')),
  ('p_senior_001', 'u_senior_001', '김시니어', '010-0000-0000', NULL, '건강을 챙기고 있습니다.', '1948-05-12', 77, 'FEMALE', '[]', '{"fontSize":"large"}', datetime('now'), datetime('now')),
  ('p_guardian_001', 'u_guardian_001', '박보호자', '010-1111-1111', NULL, '보호자입니다.', '1988-08-20', 37, 'MALE', '[]', '{"notifications":true}', datetime('now'), datetime('now'));

-- Family Board
INSERT INTO family_boards (id, senior_id, name, description, settings, last_activity_at, created_at, updated_at)
VALUES
  ('fb_001', 'u_senior_001', '김시니어님의 가족 보드', '가족과 함께 건강 정보를 공유하세요', '{"notifications":{"emergencyAlerts":true}}', datetime('now'), datetime('now'), datetime('now'));

-- Family Board Members
INSERT INTO family_board_members (board_id, member_id, board_role, member_status, invited_at, accepted_at, removed_at, created_at, updated_at)
VALUES
  ('fb_001', 'u_senior_001', 'ADMIN', 'ACTIVE', datetime('now'), datetime('now'), NULL, datetime('now'), datetime('now')),
  ('fb_001', 'u_guardian_001', 'VIEWER', 'ACTIVE', datetime('now'), datetime('now'), NULL, datetime('now'), datetime('now'));

-- Board Invitations (example pending)
INSERT INTO board_invitations (id, board_id, inviter_id, invite_code, invitee_email, intended_role, status, expires_at, accepted_at, created_at, updated_at)
VALUES
  ('inv_001', 'fb_001', 'u_senior_001', 'INVITE-TEST-001', 'newuser@example.com', 'VIEWER', 'PENDING', datetime('now', '+7 days'), NULL, datetime('now'), datetime('now'));

-- Action Cards (today)
INSERT INTO action_cards (user_id, action_date, title, description, status, rule_id, created_at, updated_at)
VALUES
  ('u_senior_001', date('now'), '가벼운 산책 10분', '근처 공원을 천천히 걸어보세요.', 'PENDING', 'rule_walk_10m', datetime('now'), datetime('now')),
  ('u_senior_001', date('now'), '물 한 잔 마시기', '수분 섭취를 늘려주세요.', 'PENDING', 'rule_water', datetime('now'), datetime('now'));

-- Daily Health Metrics (recent week)
INSERT INTO health_metrics_daily (metric_id, user_id, record_date, steps, heart_rate, weight, bp_systolic, bp_diastolic, created_at, updated_at)
VALUES
  ('m_2025_01_01', 'u_senior_001', date('now','-6 day'), 5200, 72, 63.5, 118, 76, datetime('now'), datetime('now')),
  ('m_2025_01_02', 'u_senior_001', date('now','-5 day'), 6100, 74, 63.4, 120, 78, datetime('now'), datetime('now')),
  ('m_2025_01_03', 'u_senior_001', date('now','-4 day'), 7000, 70, 63.3, 117, 75, datetime('now'), datetime('now')),
  ('m_2025_01_04', 'u_senior_001', date('now','-3 day'), 4800, 76, 63.6, 121, 80, datetime('now'), datetime('now')),
  ('m_2025_01_05', 'u_senior_001', date('now','-2 day'), 8300, 73, 63.2, 119, 77, datetime('now'), datetime('now')),
  ('m_2025_01_06', 'u_senior_001', date('now','-1 day'), 5400, 75, 63.4, 122, 79, datetime('now'), datetime('now')),
  ('m_2025_01_07', 'u_senior_001', date('now'), 9200, 71, 63.1, 116, 74, datetime('now'), datetime('now'));

-- Health Reports (weekly & monthly)
INSERT INTO health_reports (report_id, user_id, metrics, context, start_date, end_date, period_type, created_at, updated_at)
VALUES
  ('r_weekly_001', 'u_senior_001',
   '{"activity":{"steps":6571},"heartRate":{"avgBpm":73,"minBpm":70,"maxBpm":76},"bloodPressure":{"systolic":119,"diastolic":77},"weight":{"value":63.4,"unit":"kg"}}',
   '{"deviceId":"MANUAL","deviceType":"MANUAL","isMissingData":false,"missingDataFields":[],"metadata":"manual-entry"}',
   date('now','-6 day'), date('now'), 'WEEKLY', datetime('now'), datetime('now')),
  ('r_monthly_001', 'u_senior_001',
   '{"activity":{"steps":6200},"heartRate":{"avgBpm":74,"minBpm":70,"maxBpm":78},"bloodPressure":{"systolic":120,"diastolic":78},"weight":{"value":63.5,"unit":"kg"}}',
   '{"deviceId":"MANUAL","deviceType":"MANUAL","isMissingData":true,"missingDataFields":["steps"],"metadata":"manual-entry"}',
   date('now','start of month'), date('now'), 'MONTHLY', datetime('now'), datetime('now'));
