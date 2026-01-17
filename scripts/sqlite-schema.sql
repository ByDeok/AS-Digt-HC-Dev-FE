PRAGMA foreign_keys = ON;

-- Users
CREATE TABLE IF NOT EXISTS users (
  user_id TEXT PRIMARY KEY,
  email TEXT NOT NULL UNIQUE,
  password TEXT,
  role TEXT NOT NULL,
  status TEXT NOT NULL,
  auth_provider TEXT,
  provider_id TEXT,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL
);

-- User Profiles
CREATE TABLE IF NOT EXISTS user_profiles (
  profile_id TEXT PRIMARY KEY,
  user_id TEXT NOT NULL UNIQUE,
  name TEXT NOT NULL,
  phone_number TEXT,
  profile_image_url TEXT,
  bio TEXT,
  birth_date DATE,
  age INTEGER,
  gender TEXT,
  primary_conditions TEXT,
  accessibility_prefs TEXT,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE INDEX IF NOT EXISTS idx_profiles_age_gender ON user_profiles(age, gender);

-- Health metrics daily (manual input)
CREATE TABLE IF NOT EXISTS health_metrics_daily (
  metric_id TEXT PRIMARY KEY,
  user_id TEXT NOT NULL,
  record_date DATE NOT NULL,
  steps INTEGER,
  heart_rate INTEGER,
  weight REAL,
  bp_systolic INTEGER,
  bp_diastolic INTEGER,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users(user_id),
  UNIQUE (user_id, record_date)
);

CREATE INDEX IF NOT EXISTS idx_metrics_user_date ON health_metrics_daily(user_id, record_date);

-- Health reports
CREATE TABLE IF NOT EXISTS health_reports (
  report_id TEXT PRIMARY KEY,
  user_id TEXT NOT NULL,
  metrics TEXT,
  context TEXT,
  start_date DATE,
  end_date DATE,
  period_type TEXT,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE INDEX IF NOT EXISTS idx_reports_user_created ON health_reports(user_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_reports_user_period_type ON health_reports(user_id, period_type);

-- Action cards (daily missions)
CREATE TABLE IF NOT EXISTS action_cards (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  user_id TEXT NOT NULL,
  action_date DATE NOT NULL,
  title TEXT NOT NULL,
  description TEXT,
  status TEXT NOT NULL,
  rule_id TEXT,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE INDEX IF NOT EXISTS idx_action_card_date_user ON action_cards(action_date, user_id);
CREATE INDEX IF NOT EXISTS idx_action_card_status ON action_cards(status);

-- Family board
CREATE TABLE IF NOT EXISTS family_boards (
  id TEXT PRIMARY KEY,
  senior_id TEXT NOT NULL UNIQUE,
  name TEXT NOT NULL,
  description TEXT,
  settings TEXT,
  last_activity_at DATETIME,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  FOREIGN KEY (senior_id) REFERENCES users(user_id)
);

CREATE INDEX IF NOT EXISTS idx_boards_senior ON family_boards(senior_id);

-- Family board members
CREATE TABLE IF NOT EXISTS family_board_members (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  board_id TEXT NOT NULL,
  member_id TEXT NOT NULL,
  board_role TEXT NOT NULL,
  member_status TEXT NOT NULL,
  invited_at DATETIME NOT NULL,
  accepted_at DATETIME,
  removed_at DATETIME,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  FOREIGN KEY (board_id) REFERENCES family_boards(id),
  FOREIGN KEY (member_id) REFERENCES users(user_id),
  UNIQUE (board_id, member_id)
);

CREATE INDEX IF NOT EXISTS idx_members_board ON family_board_members(board_id);
CREATE INDEX IF NOT EXISTS idx_members_member ON family_board_members(member_id);

-- Board invitations
CREATE TABLE IF NOT EXISTS board_invitations (
  id TEXT PRIMARY KEY,
  board_id TEXT NOT NULL,
  inviter_id TEXT NOT NULL,
  invite_code TEXT NOT NULL UNIQUE,
  invitee_email TEXT,
  intended_role TEXT NOT NULL,
  status TEXT NOT NULL,
  expires_at DATETIME NOT NULL,
  accepted_at DATETIME,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  FOREIGN KEY (board_id) REFERENCES family_boards(id),
  FOREIGN KEY (inviter_id) REFERENCES users(user_id)
);

CREATE INDEX IF NOT EXISTS idx_invitations_board ON board_invitations(board_id);
CREATE INDEX IF NOT EXISTS idx_invitations_status_expires ON board_invitations(status, expires_at);
