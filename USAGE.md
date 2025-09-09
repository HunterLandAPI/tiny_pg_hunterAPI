# 🎮 Tiny PG Hunter API - 사용법 및 확장 가이드

## 📋 목차
1. [프로젝트 개요](#프로젝트-개요)
2. [설치 및 설정](#설치-및-설정)
3. [기본 사용법](#기본-사용법)
4. [API 활용 방법](#api-활용-방법)
5. [확장 개발 가이드](#확장-개발-가이드)
6. [문제 해결](#문제-해결)

---

## 🎯 프로젝트 개요

### 무엇을 배웠나요?
이 프로젝트를 통해 다음과 같은 핵심 기술들을 학습했습니다:

#### 🏗️ 아키텍처 패턴
- **레이어드 아키텍처**: Presentation(Command/Listener) → Service → Repository → Database
- **의존성 주입**: 생성자 기반 DI를 통한 느슨한 결합
- **싱글톤 패턴**: 플러그인 인스턴스 관리

#### 🗄️ 데이터베이스 기술
- **JPA/Hibernate**: 객체-관계 매핑을 통한 데이터베이스 추상화
- **HikariCP**: 고성능 커넥션 풀링
- **MySQL**: 관계형 데이터베이스 연동

#### 🎮 마인크래프트 플러그인 개발
- **Bukkit/Paper API**: 이벤트 시스템, 명령어 처리
- **플러그인 라이프사이클**: onEnable/onDisable
- **권한 시스템**: plugin.yml을 통한 권한 관리

#### 🛠️ 개발 도구 및 빌드
- **Gradle**: 빌드 자동화, 의존성 관리
- **Shadow Plugin**: Fat JAR 생성
- **Lombok**: 보일러플레이트 코드 제거

---

## 🚀 설치 및 설정

### 1단계: 빌드 및 배포
```bash
# 프로젝트 빌드
cd tiny_pg_hunterAPI
./gradlew clean shadowJar

# JAR 파일 확인
ls -la build/libs/

# 마인크래프트 서버에 배포
cp build/libs/tiny_pg_hunterAPI-1.0-SNAPSHOT.jar /path/to/minecraft/server/plugins/
```

### 2단계: 데이터베이스 설정
자세한 설정은 `database/README.md`를 참조하세요.

```sql
-- MySQL에서 실행
CREATE DATABASE minecraft_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'minecraft_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON minecraft_db.* TO 'minecraft_user'@'localhost';
FLUSH PRIVILEGES;
```

### 3단계: persistence.xml 설정
```xml
<!-- src/main/resources/META-INF/persistence.xml에서 수정 -->
<property name="jakarta.persistence.jdbc.url" 
          value="jdbc:mysql://localhost:3306/minecraft_db?useSSL=false&serverTimezone=Asia/Seoul"/>
<property name="jakarta.persistence.jdbc.user" value="minecraft_user"/>
<property name="jakarta.persistence.jdbc.password" value="your_password"/>
```

---

## 🎮 기본 사용법

### 서버 시작 후 확인
```
[INFO] [TinyPG] === Tiny PG Hunter API 플러그인 시작 ===
[INFO] [TinyPG] 데이터베이스 연결을 초기화합니다...
[INFO] [TinyPG] === 플러그인이 성공적으로 활성화되었습니다! ===
```

### 명령어 사용
```
/hello          # 플러그인 테스트
/hi             # hello 명령어의 별칭
/안녕            # 한글 별칭
```

### 자동 기능
- **플레이어 접속 시**: 자동으로 데이터베이스에 플레이어 정보 저장/업데이트
- **마지막 로그인 시간**: 접속할 때마다 자동 갱신
- **플레이 시간 추적**: 세션별 플레이 시간 누적 (향후 구현)

---

## 🔌 API 활용 방법

### 다른 플러그인에서 사용하기

#### 1. 플러그인 인스턴스 가져오기
```java
// 다른 플러그인의 onEnable()에서
Tiny_pg_hunterAPI api = Tiny_pg_hunterAPI.getInstance();
if (api != null) {
    PlayerDataRepository repository = api.getPlayerRepository();
    MessageService messageService = api.getMessageService();
}
```

#### 2. 플레이어 데이터 조회
```java
// 특정 플레이어의 데이터 가져오기
String playerUUID = player.getUniqueId().toString();
PlayerData data = repository.findByUuid(playerUUID);

if (data != null) {
    double money = data.getMoney();
    long playTime = data.getPlayTimeMinutes();
    // 데이터 활용...
}
```

#### 3. 플레이어 데이터 수정
```java
// 플레이어 돈 추가
PlayerData data = repository.findByUuid(playerUUID);
if (data != null) {
    data.addMoney(100.0); // 100원 추가
    repository.save(data);
}

// 플레이 시간 추가
data.addPlayTime(60L); // 60분 추가
repository.save(data);
```

#### 4. 상위 플레이어 조회
```java
// 돈이 많은 상위 10명 조회
List<PlayerData> topPlayers = repository.findTopPlayersByMoney(10);
for (PlayerData player : topPlayers) {
    System.out.println(player.getPlayerName() + ": " + player.getMoney());
}
```

### plugin.yml에 의존성 추가
```yaml
# 당신의 플러그인의 plugin.yml에 추가
depend: [Tiny_PG_HunterAPI]
```

---

## 🛠️ 확장 개발 가이드

### 새로운 엔티티 추가하기

#### 1. 엔티티 클래스 생성
```java
@Entity
@Table(name = "guild_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuildData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "guild_name", unique = true, nullable = false)
    private String guildName;
    
    @Column(name = "leader_uuid", nullable = false)
    private String leaderUuid;
    
    @Column(name = "member_count")
    private Integer memberCount = 0;
    
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
}
```

#### 2. 레포지토리 생성
```java
public class GuildDataRepository {
    private final DatabaseManager databaseManager;
    
    public GuildDataRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }
    
    public GuildData findByName(String guildName) {
        return databaseManager.executeInTransaction(em -> {
            TypedQuery<GuildData> query = em.createQuery(
                "SELECT g FROM GuildData g WHERE g.guildName = :name", 
                GuildData.class
            );
            query.setParameter("name", guildName);
            return query.getResultStream().findFirst().orElse(null);
        });
    }
    
    public GuildData save(GuildData guild) {
        return databaseManager.executeInTransaction(em -> {
            return em.merge(guild);
        });
    }
}
```

#### 3. persistence.xml에 엔티티 등록
```xml
<persistence-unit name="minecraft-plugin-db">
    <class>io.github.louis5103.tiny_pg_hunterAPI.model.entity.PlayerData</class>
    <class>io.github.louis5103.tiny_pg_hunterAPI.model.entity.GuildData</class>
    <!-- 새로운 엔티티 추가 -->
</persistence-unit>
```

### 새로운 명령어 추가하기

#### 1. 명령어 클래스 생성
```java
public class MoneyCommand implements CommandExecutor {
    private final PlayerDataRepository playerRepository;
    private final MessageService messageService;
    
    public MoneyCommand(PlayerDataRepository playerRepository, MessageService messageService) {
        this.playerRepository = playerRepository;
        this.messageService = messageService;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(messageService.getErrorMessage("플레이어만 사용할 수 있는 명령어입니다."));
            return true;
        }
        
        Player player = (Player) sender;
        String uuid = player.getUniqueId().toString();
        
        PlayerData data = playerRepository.findByUuid(uuid);
        if (data != null) {
            String message = String.format("보유 금액: %.2f원", data.getMoney());
            player.sendMessage(messageService.getCommandMessage(message));
        } else {
            player.sendMessage(messageService.getErrorMessage("플레이어 데이터를 찾을 수 없습니다."));
        }
        
        return true;
    }
}
```

#### 2. plugin.yml에 명령어 등록
```yaml
commands:
  hello:
    description: "플러그인 테스트 명령어"
    usage: "/<command>"
    permission: tinypg.command.hello
  money:
    description: "보유 금액 확인"
    usage: "/<command>"
    permission: tinypg.command.money
    aliases: ["돈", "재산"]
```

#### 3. 메인 클래스에서 등록
```java
private void registerCommands() {
    HelloCommand helloCommand = new HelloCommand(messageService);
    this.getCommand("hello").setExecutor(helloCommand);
    
    // 새 명령어 등록
    MoneyCommand moneyCommand = new MoneyCommand(playerRepository, messageService);
    this.getCommand("money").setExecutor(moneyCommand);
}
```

### 새로운 이벤트 리스너 추가하기

#### 1. 리스너 클래스 생성
```java
public class PlayerDeathListener implements Listener {
    private final PlayerDataRepository playerRepository;
    private final MessageService messageService;
    
    public PlayerDeathListener(PlayerDataRepository playerRepository, MessageService messageService) {
        this.playerRepository = playerRepository;
        this.messageService = messageService;
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        String uuid = player.getUniqueId().toString();
        
        // 사망 시 돈 일부 차감
        PlayerData data = playerRepository.findByUuid(uuid);
        if (data != null && data.getMoney() > 0) {
            double penalty = data.getMoney() * 0.1; // 10% 차감
            data.addMoney(-penalty);
            playerRepository.save(data);
            
            String message = String.format("사망으로 인해 %.2f원이 차감되었습니다.", penalty);
            player.sendMessage(messageService.getErrorMessage(message));
        }
    }
}
```

#### 2. 메인 클래스에서 등록
```java
private void registerListeners() {
    PlayerJoinListener joinListener = new PlayerJoinListener(playerRepository, messageService);
    getServer().getPluginManager().registerEvents(joinListener, this);
    
    // 새 리스너 등록
    PlayerDeathListener deathListener = new PlayerDeathListener(playerRepository, messageService);
    getServer().getPluginManager().registerEvents(deathListener, this);
}
```

### 스케줄러 작업 추가하기

#### 정기적인 데이터 저장
```java
// 메인 클래스의 onEnable()에서
public void onEnable() {
    // ... 기존 초기화 코드 ...
    
    // 5분마다 플레이 시간 업데이트
    getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
        updatePlayTimes();
    }, 20L * 300L, 20L * 300L); // 20틱 = 1초, 300초 = 5분
}

private void updatePlayTimes() {
    // 온라인 플레이어들의 플레이 시간 업데이트
    for (Player player : getServer().getOnlinePlayers()) {
        String uuid = player.getUniqueId().toString();
        playerRepository.updatePlayTime(uuid, 5L); // 5분 추가
    }
}
```

---

## 🎨 UI 및 GUI 확장

### 인벤토리 GUI 만들기
```java
public class PlayerStatsGUI {
    public static void openStatsMenu(Player player, PlayerData data) {
        Inventory gui = Bukkit.createInventory(null, 27, "플레이어 정보");
        
        // 돈 정보 아이템
        ItemStack moneyItem = new ItemStack(Material.GOLD_INGOT);
        ItemMeta moneyMeta = moneyItem.getItemMeta();
        moneyMeta.setDisplayName("§6보유 금액");
        moneyMeta.setLore(Arrays.asList("§f" + data.getMoney() + "원"));
        moneyItem.setItemMeta(moneyMeta);
        gui.setItem(4, moneyItem);
        
        // 플레이 시간 아이템
        ItemStack timeItem = new ItemStack(Material.CLOCK);
        ItemMeta timeMeta = timeItem.getItemMeta();
        timeMeta.setDisplayName("§a플레이 시간");
        timeMeta.setLore(Arrays.asList("§f" + data.getPlayTimeMinutes() + "분"));
        timeItem.setItemMeta(timeMeta);
        gui.setItem(13, timeItem);
        
        player.openInventory(gui);
    }
}
```

---

## 🔧 고급 기능 구현

### 캐싱 시스템 추가
```java
public class CachedPlayerDataRepository {
    private final PlayerDataRepository repository;
    private final Map<String, PlayerData> cache = new ConcurrentHashMap<>();
    private final long cacheExpireTime = 5 * 60 * 1000; // 5분
    
    public PlayerData findByUuid(String uuid) {
        PlayerData cached = cache.get(uuid);
        if (cached != null && !isExpired(cached)) {
            return cached;
        }
        
        PlayerData fresh = repository.findByUuid(uuid);
        if (fresh != null) {
            cache.put(uuid, fresh);
        }
        return fresh;
    }
    
    private boolean isExpired(PlayerData data) {
        // 캐시 만료 로직 구현
        return false;
    }
}
```

### 비동기 처리
```java
public CompletableFuture<PlayerData> findByUuidAsync(String uuid) {
    return CompletableFuture.supplyAsync(() -> {
        return playerRepository.findByUuid(uuid);
    });
}

// 사용 예시
findByUuidAsync(playerUUID).thenAccept(data -> {
    if (data != null) {
        // UI 업데이트는 메인 스레드에서
        Bukkit.getScheduler().runTask(plugin, () -> {
            player.sendMessage("데이터 로드 완료!");
        });
    }
});
```

---

## 🚨 문제 해결

### 자주 발생하는 오류들

#### 1. ClassNotFoundException
```
java.lang.ClassNotFoundException: org.hibernate.jpa.HibernatePersistenceProvider
```
**해결책**: Shadow JAR이 제대로 빌드되지 않았습니다. `./gradlew clean shadowJar`로 다시 빌드하세요.

#### 2. 데이터베이스 연결 실패
```
jakarta.persistence.PersistenceException: Unable to build EntityManagerFactory
```
**해결책**: 
- persistence.xml의 데이터베이스 설정 확인
- MySQL 서버 실행 상태 확인
- 네트워크 연결 확인

#### 3. 테이블 생성 권한 부족
```
Access denied for user 'minecraft_user'@'localhost' to database 'minecraft_db'
```
**해결책**:
```sql
GRANT CREATE, ALTER, DROP ON minecraft_db.* TO 'minecraft_user'@'localhost';
FLUSH PRIVILEGES;
```

### 성능 최적화 팁

#### 1. 커넥션 풀 설정
```xml
<!-- 대규모 서버용 설정 -->
<property name="hibernate.hikari.maximumPoolSize" value="20"/>
<property name="hibernate.hikari.minimumIdle" value="5"/>
```

#### 2. 배치 처리
```java
// 여러 플레이어 데이터를 한 번에 업데이트
public void batchUpdatePlayTime(Map<String, Long> playTimes) {
    databaseManager.executeInTransaction(em -> {
        playTimes.forEach((uuid, time) -> {
            PlayerData player = em.find(PlayerData.class, uuid);
            if (player != null) {
                player.addPlayTime(time);
            }
        });
        return null;
    });
}
```

#### 3. 인덱스 추가
```sql
-- 자주 조회되는 컬럼에 인덱스 추가
CREATE INDEX idx_player_money ON player_data(money);
CREATE INDEX idx_last_login ON player_data(last_login);
```

---

## 📞 지원 및 커뮤니티

### 개발자 리소스
- **GitHub**: https://github.com/louis5103/tiny_pg_hunterAPI
- **이슈 트래킹**: GitHub Issues 사용
- **문서**: 이 README와 코드 주석 참조

### 유용한 참고 자료
- [Paper API 문서](https://docs.papermc.io/)
- [Hibernate ORM 가이드](https://hibernate.org/orm/documentation/)
- [HikariCP 설정 가이드](https://github.com/brettwooldridge/HikariCP)
- [Maven Repository](https://mvnrepository.com/) - 의존성 검색

### 기여 방법
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Write tests
5. Submit a pull request

---

## 🎉 축하합니다!

이 프로젝트를 완료하셨다면, 다음과 같은 실무 기술들을 익히신 것입니다:

✅ **백엔드 개발**: JPA/Hibernate를 활용한 데이터베이스 연동  
✅ **아키텍처 설계**: 레이어드 아키텍처와 의존성 주입  
✅ **게임 서버 개발**: 마인크래프트 플러그인 개발  
✅ **빌드 시스템**: Gradle을 활용한 빌드 자동화  
✅ **데이터베이스 설계**: MySQL 스키마 설계 및 최적화  

이제 이 지식을 바탕으로 더 복잡하고 흥미로운 플러그인들을 개발해보세요! 🚀
