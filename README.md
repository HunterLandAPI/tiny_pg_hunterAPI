# Tiny PG Hunter API

마인크래프트 서버용 플레이어 데이터 관리 플러그인입니다. MySQL과 SQLite를 지원합니다.

## 🚀 주요 기능

- **다중 데이터베이스 지원**: MySQL과 SQLite 선택 가능
- **간단한 설정**: config.yml에서 `database.type`만 변경
- **자동 스키마 관리**: Hibernate를 통한 자동 테이블 생성/업데이트
- **고성능**: HikariCP 커넥션 풀링 적용

## 📋 요구사항

- **Java**: 21 이상
- **Minecraft Server**: Paper 1.20.6 이상 (Bukkit/Spigot 호환)
- **MySQL**: 8.0 이상 (MySQL 사용 시)

## ⚙️ 설정 방법

`plugins/Tiny_pg_hunterAPI/config.yml` 파일에서 설정합니다.

### SQLite 사용 (기본값, 권장)

```yaml
database:
  type: "sqlite"
  sqlite:
    file: "database/player_data.db"
```

**장점:**
- 별도 DB 서버 설치 불필요
- 설정이 간단함  
- 소규모 서버에 적합

### MySQL 사용

```yaml
database:
  type: "mysql"
  mysql:
    host: "localhost"
    port: 3306
    database: "minecraft_db"
    username: "root"
    password: "your_password_here"
```

**장점:**
- 대용량 데이터 처리에 유리
- 복수 서버 간 데이터 공유 가능
- 백업 및 복제 기능 우수

#### MySQL 설정 가이드

1. **데이터베이스 생성**:
```sql
CREATE DATABASE minecraft_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. **사용자 생성 및 권한 부여**:
```sql
CREATE USER 'root'@'localhost' IDENTIFIED BY 'your_password_here';
GRANT ALL PRIVILEGES ON minecraft_db.* TO 'root'@'localhost';
FLUSH PRIVILEGES;
```

3. **config.yml 수정** 후 서버 재시작

## 🔧 사용법

1. `database.type`을 `"sqlite"` 또는 `"mysql"`로 설정
2. 해당 데이터베이스 설정 입력
3. 서버 재시작

## 🎯 실무 표준

이 플러그인은 **LuckPerms**와 **Plan** 플러그인의 데이터베이스 설정 방식을 참고하여 제작되었습니다:

- **간단한 분기**: `database.type`으로 DB 선택
- **필수 설정만**: DB 연결에 필요한 최소한의 정보만 노출  
- **표준 기술**: HikariCP + Hibernate 조합
- **실무 검증**: 수천 개 서버에서 검증된 방식

## 📝 라이센스

MIT License

---

**참고**: 이 플러그인은 실무에서 검증된 데이터베이스 설정 패턴을 따릅니다.
