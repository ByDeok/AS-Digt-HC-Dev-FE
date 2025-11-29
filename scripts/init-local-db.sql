-- =================================================================
-- AS-Digt-HC 로컬 개발용 MySQL 데이터베이스 초기화 스크립트
-- =================================================================
-- 실행 방법:
--   mysql -u root -p < scripts/init-local-db.sql
-- =================================================================

-- 1. 데이터베이스 생성
CREATE DATABASE IF NOT EXISTS as_digt_hc_dev
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- 2. 데이터베이스 사용
USE as_digt_hc_dev;

-- 3. (선택) 전용 사용자 생성 (root 대신 별도 계정 사용 시)
-- CREATE USER IF NOT EXISTS 'hc_dev_user'@'localhost' IDENTIFIED BY 'hc_dev_password';
-- GRANT ALL PRIVILEGES ON as_digt_hc_dev.* TO 'hc_dev_user'@'localhost';
-- FLUSH PRIVILEGES;

-- 4. 확인 메시지
SELECT 'AS-Digt-HC 로컬 개발 데이터베이스가 성공적으로 생성되었습니다!' AS message;
SELECT DATABASE() AS current_database;

