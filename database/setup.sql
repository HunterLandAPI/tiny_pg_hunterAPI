-- =====================================================
-- Tiny PG Hunter API 데이터베이스 설정 스크립트
-- =====================================================
-- 이 스크립트는 MySQL에서 플러그인용 데이터베이스와 사용자를 생성합니다.
-- MySQL 관리자 권한으로 실행해야 합니다.

-- === 1단계: 데이터베이스 생성 ===
-- 플러그인 전용 데이터베이스를 생성합니다
-- IF NOT EXISTS를 사용해서 이미 존재하는 경우 오류가 발생하지 않도록 합니다
CREATE DATABASE IF NOT EXISTS minecraft_db
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

-- 데이터베이스 생성 확인
SHOW DATABASES LIKE 'minecraft_db';

-- === 2단계: 플러그인 전용 사용자 생성 ===
-- 보안을 위해 플러그인만을 위한 별도 사용자를 생성합니다
-- 'minecraft_user'는 사용자명, 'your_password_here'는 실제 패스워드로 변경해야 합니다

-- 기존 사용자가 있다면 삭제 (선택사항)
-- DROP USER IF EXISTS 'minecraft_user'@'localhost';

-- 새 사용자 생성
-- localhost에서만 접속 가능한 사용자를 생성합니다
-- 운영 환경에서는 더 강력한 패스워드를 사용하세요!
CREATE USER IF NOT EXISTS 'minecraft_user'@'localhost' 
    IDENTIFIED BY 'your_password_here';

-- 원격 접속이 필요한 경우 (선택사항)
-- CREATE USER IF NOT EXISTS 'minecraft_user'@'%' 
--     IDENTIFIED BY 'your_password_here';

-- === 3단계: 권한 부여 ===
-- 생성한 사용자에게 minecraft_db 데이터베이스에 대한 모든 권한을 부여합니다
-- SELECT, INSERT, UPDATE, DELETE: 데이터 조작 권한
-- CREATE, ALTER, DROP: 테이블 구조 변경 권한 (Hibernate가 스키마를 자동 생성/수정하기 위해 필요)
-- INDEX: 인덱스 관리 권한
-- REFERENCES: 외래키 생성 권한

GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, DROP, INDEX, REFERENCES 
    ON minecraft_db.* 
    TO 'minecraft_user'@'localhost';

-- 원격 접속 사용자에게도 권한 부여 (위에서 원격 사용자를 생성한 경우)
-- GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, DROP, INDEX, REFERENCES 
--     ON minecraft_db.* 
--     TO 'minecraft_user'@'%';

-- === 4단계: 권한 변경사항 적용 ===
-- MySQL에게 권한 테이블을 다시 로드하도록 지시합니다
FLUSH PRIVILEGES;

-- === 5단계: 설정 확인 ===
-- 생성된 사용자 확인
SELECT User, Host FROM mysql.user WHERE User = 'minecraft_user';

-- 부여된 권한 확인
SHOW GRANTS FOR 'minecraft_user'@'localhost';

-- 데이터베이스 접속 테스트
-- 이 명령어들은 mysql 클라이언트에서 실행해서 테스트할 수 있습니다:
-- mysql -u minecraft_user -p minecraft_db
-- (패스워드 입력 후)
-- SHOW TABLES;
-- SELECT 1; (연결 테스트)

-- === 선택사항: 테스트용 더미 데이터 ===
-- 개발/테스트 환경에서 사용할 수 있는 샘플 데이터입니다
-- 실제 운영 환경에서는 실행하지 마세요!

/*
USE minecraft_db;

-- 테스트용 플레이어 데이터 (Hibernate가 테이블을 생성한 후에 실행)
INSERT INTO player_data (uuid, player_name, money, last_login, play_time_minutes) VALUES
('550e8400-e29b-41d4-a716-446655440000', 'TestPlayer1', 1000.0, NOW(), 120),
('550e8400-e29b-41d4-a716-446655440001', 'TestPlayer2', 500.0, NOW(), 60),
('550e8400-e29b-41d4-a716-446655440002', 'AdminPlayer', 10000.0, NOW(), 300);

-- 테스트 데이터 확인
SELECT * FROM player_data;
*/

-- =====================================================
-- 스크립트 실행 방법:
-- =====================================================
-- 1. MySQL에 root 사용자로 접속:
--    mysql -u root -p
--
-- 2. 이 스크립트 실행:
--    source /path/to/this/file.sql
--    또는
--    \. /path/to/this/file.sql
--
-- 3. 또는 파일 내용을 복사해서 직접 실행
--
-- =====================================================
-- 보안 주의사항:
-- =====================================================
-- 1. 'your_password_here'를 강력한 패스워드로 변경하세요
-- 2. 운영 환경에서는 원격 접속(%)보다는 특정 IP 제한을 권장합니다
-- 3. 정기적으로 패스워드를 변경하세요
-- 4. 불필요한 권한은 부여하지 마세요
-- 5. 이 스크립트 파일을 안전한 곳에 보관하세요
