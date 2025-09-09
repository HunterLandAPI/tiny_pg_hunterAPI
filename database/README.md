# =====================================================
# Tiny PG Hunter API - 데이터베이스 설정 가이드
# =====================================================

## 📋 목차
1. [환경 요구사항](#환경-요구사항)
2. [데이터베이스 설정](#데이터베이스-설정)
3. [플러그인 설정](#플러그인-설정)
4. [환경별 설정](#환경별-설정)
5. [문제 해결](#문제-해결)

---

## 🔧 환경 요구사항

### 필수 소프트웨어
- **MySQL 8.0 이상** (또는 MariaDB 10.5 이상)
- **Java 21** (플러그인 실행용)
- **Paper/Spigot 1.20.6** (마인크래프트 서버)

### 네트워크 요구사항
- MySQL 서버와 마인크래프트 서버 간 네트워크 연결
- 기본 MySQL 포트: 3306

---

## 🗄️ 데이터베이스 설정

### 1단계: MySQL 설치 및 실행
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install mysql-server

# CentOS/RHEL
sudo yum install mysql-server

# macOS (Homebrew)
brew install mysql

# Windows: MySQL Installer 사용
```

### 2단계: MySQL 보안 설정
```bash
# MySQL 보안 스크립트 실행
sudo mysql_secure_installation

# 권장 설정:
# - root 패스워드 설정: Y
# - 익명 사용자 제거: Y  
# - 원격 root 로그인 금지: Y
# - 테스트 데이터베이스 제거: Y
# - 권한 테이블 재로드: Y
```

### 3단계: 데이터베이스 및 사용자 생성
```bash
# MySQL에 root로 접속
mysql -u root -p

# database/setup.sql 스크립트 실행
source /path/to/tiny_pg_hunterAPI/database/setup.sql

# 또는 수동으로 실행:
```

```sql
-- 데이터베이스 생성
CREATE DATABASE minecraft_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 사용자 생성 (패스워드 변경 필수!)
CREATE USER 'minecraft_user'@'localhost' IDENTIFIED BY 'your_strong_password_here';

-- 권한 부여
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, DROP, INDEX, REFERENCES 
    ON minecraft_db.* TO 'minecraft_user'@'localhost';

-- 권한 적용
FLUSH PRIVILEGES;
```

---

## ⚙️ 플러그인 설정

### persistence.xml 수정
`src/main/resources/META-INF/persistence.xml` 파일에서 데이터베이스 정보를 수정하세요:

```xml
<!-- 데이터베이스 연결 URL -->
<property name="jakarta.persistence.jdbc.url" 
          value="jdbc:mysql://localhost:3306/minecraft_db?useSSL=false&serverTimezone=Asia/Seoul&allowPublicKeyRetrieval=true"/>

<!-- 사용자명 -->
<property name="jakarta.persistence.jdbc.user" value="minecraft_user"/>

<!-- 패스워드 (실제 패스워드로 변경) -->
<property name="jakarta.persistence.jdbc.password" value="your_strong_password_here"/>
```

### 중요 설정 옵션 설명

#### 1. 연결 URL 파라미터
- `useSSL=false`: 로컬 개발 환경용 (운영환경에서는 true 권장)
- `serverTimezone=Asia/Seoul`: 시간대 설정
- `allowPublicKeyRetrieval=true`: MySQL 8.0+ 호환성

#### 2. HikariCP 커넥션 풀
- `minimumIdle=2`: 최소 연결 수
- `maximumPoolSize=10`: 최대 연결 수  
- `connectionTimeout=30000`: 연결 타임아웃 (30초)

#### 3. Hibernate 설정
- `hibernate.hbm2ddl.auto=update`: 스키마 자동 업데이트
- `hibernate.show_sql=true`: SQL 쿼리 로그 (개발용)

---

## 🌍 환경별 설정

### 개발 환경 (Development)
```xml
<!-- 개발용 설정: 로컬 MySQL -->
<property name="jakarta.persistence.jdbc.url" 
          value="jdbc:mysql://localhost:3306/minecraft_dev?useSSL=false&serverTimezone=Asia/Seoul"/>
<property name="hibernate.show_sql" value="true"/>
<property name="hibernate.format_sql" value="true"/>
<property name="hibernate.hbm2ddl.auto" value="create-drop"/>
```

### 테스트 환경 (Testing)
```xml
<!-- 테스트용 설정: 별도 테스트 DB -->
<property name="jakarta.persistence.jdbc.url" 
          value="jdbc:mysql://localhost:3306/minecraft_test?useSSL=false&serverTimezone=Asia/Seoul"/>
<property name="hibernate.show_sql" value="false"/>
<property name="hibernate.hbm2ddl.auto" value="create-drop"/>
```

### 운영 환경 (Production)
```xml
<!-- 운영용 설정: 보안 강화 -->
<property name="jakarta.persistence.jdbc.url" 
          value="jdbc:mysql://db.yourserver.com:3306/minecraft_prod?useSSL=true&serverTimezone=Asia/Seoul"/>
<property name="hibernate.show_sql" value="false"/>
<property name="hibernate.hbm2ddl.auto" value="validate"/>
<property name="hibernate.hikari.maximumPoolSize" value="20"/>
```

---

## 🔧 빌드 및 배포

### 1. 프로젝트 빌드
```bash
# 프로젝트 디렉토리로 이동
cd /Users/imsang-u/Desktop/git/tiny_pg_hunterAPI/tiny_pg_hunterAPI

# Gradle 빌드 (테스트 포함)
./gradlew clean build

# 빌드만 (테스트 제외)
./gradlew clean shadowJar
```

### 2. 빌드 결과물 확인
- 생성 위치: `build/libs/tiny_pg_hunterAPI-1.0-SNAPSHOT.jar`
- 이 JAR 파일을 마인크래프트 서버의 `plugins/` 폴더에 복사

### 3. 서버 시작 및 확인
```bash
# 마인크래프트 서버 시작
java -jar paper-1.20.6.jar

# 로그에서 플러그인 로드 확인
[INFO] [TinyPG] === Tiny PG Hunter API 플러그인 시작 ===
[INFO] [TinyPG] 데이터베이스 연결을 초기화합니다...
[INFO] [TinyPG] === 플러그인이 성공적으로 활성화되었습니다! ===
```

---

## 🐛 문제 해결

### 자주 발생하는 오류들

#### 1. 데이터베이스 연결 실패
```
Caused by: java.sql.SQLException: Access denied for user 'minecraft_user'@'localhost'
```
**해결방법:**
- 사용자명/패스워드 확인
- MySQL 사용자 권한 확인
- 네트워크 연결 상태 확인

#### 2. 테이블 생성 권한 부족
```
Caused by: java.sql.SQLException: CREATE command denied to user 'minecraft_user'@'localhost'
```
**해결방법:**
```sql
GRANT CREATE, ALTER, DROP ON minecraft_db.* TO 'minecraft_user'@'localhost';
FLUSH PRIVILEGES;
```

#### 3. 시간대 설정 오류
```
The server time zone value 'KST' is unrecognized
```
**해결방법:**
- URL에 `serverTimezone=Asia/Seoul` 추가
- 또는 MySQL 서버 시간대 설정

#### 4. SSL 연결 오류
```
javax.net.ssl.SSLException: closing inbound before receiving peer's close_notify
```
**해결방법:**
- 개발 환경: URL에 `useSSL=false` 추가
- 운영 환경: SSL 인증서 설정

### 로그 확인 방법
```bash
# 마인크래프트 서버 로그
tail -f logs/latest.log

# MySQL 에러 로그 (Ubuntu)
sudo tail -f /var/log/mysql/error.log

# MySQL 에러 로그 (CentOS)
sudo tail -f /var/log/mysqld.log
```

### 성능 최적화
```xml
<!-- 고성능 설정 (대규모 서버용) -->
<property name="hibernate.hikari.minimumIdle" value="5"/>
<property name="hibernate.hikari.maximumPoolSize" value="25"/>
<property name="hibernate.hikari.connectionTimeout" value="20000"/>
<property name="hibernate.hikari.idleTimeout" value="300000"/>
<property name="hibernate.hikari.maxLifetime" value="1200000"/>
```

---

## 📞 지원 및 문의

### 개발자 정보
- **GitHub**: https://github.com/louis5103/tiny_pg_hunterAPI
- **이슈 리포팅**: GitHub Issues 탭 이용

### 참고 문서
- [Hibernate 공식 문서](https://hibernate.org/orm/documentation/)
- [HikariCP 설정 가이드](https://github.com/brettwooldridge/HikariCP)
- [Paper API 문서](https://docs.papermc.io/)
- [MySQL 공식 문서](https://dev.mysql.com/doc/)
