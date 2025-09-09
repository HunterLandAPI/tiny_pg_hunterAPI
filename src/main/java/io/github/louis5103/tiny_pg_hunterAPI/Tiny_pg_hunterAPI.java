package io.github.louis5103.tiny_pg_hunterAPI;

import io.github.louis5103.tiny_pg_hunterAPI.command.HelloCommand;
import io.github.louis5103.tiny_pg_hunterAPI.config.DatabaseManager;
import io.github.louis5103.tiny_pg_hunterAPI.listener.PlayerJoinListener;
import io.github.louis5103.tiny_pg_hunterAPI.model.repository.PlayerDataRepository;
import io.github.louis5103.tiny_pg_hunterAPI.service.MessageService;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 메인 플러그인 클래스입니다.
 * 
 * JavaPlugin을 상속받아서 마인크래프트 플러그인의 기본 기능들을 제공받습니다.
 * onEnable()과 onDisable() 메서드를 통해 플러그인의 생명주기를 관리합니다.
 * 
 * 이 클래스에서는 다음과 같은 작업들을 수행합니다:
 * 1. 데이터베이스 연결 초기화
 * 2. 명령어 등록
 * 3. 이벤트 리스너 등록
 * 4. 서비스 클래스들 초기화
 */
public class Tiny_pg_hunterAPI extends JavaPlugin {
    
    // 싱글톤 패턴으로 플러그인 인스턴스 관리
    // 다른 클래스에서 이 플러그인 인스턴스에 접근할 수 있도록 합니다
    private static Tiny_pg_hunterAPI instance;
    
    // 핵심 컴포넌트들
    private DatabaseManager databaseManager;
    private PlayerDataRepository playerRepository;
    private MessageService messageService;

    /**
     * 플러그인이 활성화될 때 호출되는 메서드입니다.
     * 모든 초기화 작업이 여기서 이루어집니다.
     * 
     * 초기화 순서가 중요합니다:
     * 1. 인스턴스 설정 (다른 곳에서 참조 가능하도록)
     * 2. 데이터베이스 초기화 (가장 기본이 되는 부분)
     * 3. 레포지토리와 서비스 초기화 (데이터베이스가 필요함)
     * 4. 명령어와 리스너 등록 (서비스들이 준비된 후)
     */
    @Override
    public void onEnable() {
        try {
            // 1. 플러그인 인스턴스 저장 (다른 클래스에서 접근 가능하도록)
            instance = this;
            getLogger().info("=== Tiny PG Hunter API 플러그인 시작 ===");
            
            // 2. 설정 파일 로드
            getLogger().info("설정 파일을 로드합니다...");
            saveDefaultConfig();  // config.yml이 없으면 기본 설정 파일 생성
            reloadConfig();        // 설정 파일 다시 로드
            
            // 3. 데이터베이스 초기화
            getLogger().info("데이터베이스 연결을 초기화합니다...");
            this.databaseManager = new DatabaseManager(this);
            
            // 4. 레포지토리 초기화 (데이터베이스 매니저가 필요)
            getLogger().info("데이터 접근 계층을 초기화합니다...");
            this.playerRepository = new PlayerDataRepository(databaseManager);
            
            // 5. 서비스 계층 초기화
            getLogger().info("서비스 계층을 초기화합니다...");
            this.messageService = new MessageService();
            
            // 6. 명령어 등록
            getLogger().info("명령어를 등록합니다...");
            registerCommands();
            
            // 7. 이벤트 리스너 등록
            getLogger().info("이벤트 리스너를 등록합니다...");
            registerListeners();
            
            getLogger().info("=== 플러그인이 성공적으로 활성화되었습니다! ===");
            
        } catch (Exception e) {
            // 초기화 중 오류가 발생하면 플러그인을 안전하게 비활성화
            getLogger().severe("플러그인 초기화 중 오류가 발생했습니다: " + e.getMessage());
            e.printStackTrace();
            
            // 부분적으로 초기화된 자원들을 정리
            cleanup();
            
            // 플러그인 비활성화
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    /**
     * 플러그인이 비활성화될 때 호출되는 메서드입니다.
     * 모든 자원을 안전하게 정리합니다.
     * 
     * 정리 순서는 초기화의 역순으로 하는 것이 안전합니다:
     * 1. 리스너와 명령어 해제는 자동으로 처리됨
     * 2. 데이터베이스 연결 종료
     * 3. 인스턴스 정리
     */
    @Override
    public void onDisable() {
        getLogger().info("=== Tiny PG Hunter API 플러그인 종료 시작 ===");
        
        cleanup();
        
        getLogger().info("=== 플러그인이 안전하게 종료되었습니다 ===");
    }
    
    /**
     * 자원 정리를 담당하는 메서드입니다.
     * onDisable()과 오류 발생 시 모두에서 사용됩니다.
     */
    private void cleanup() {
        try {
            // 데이터베이스 연결 종료
            if (databaseManager != null) {
                getLogger().info("데이터베이스 연결을 종료합니다...");
                databaseManager.shutdown();
                databaseManager = null;
            }
            
            // 레퍼런스 정리
            playerRepository = null;
            messageService = null;
            instance = null;
            
        } catch (Exception e) {
            getLogger().warning("자원 정리 중 오류 발생: " + e.getMessage());
        }
    }
    
    /**
     * 명령어들을 등록하는 메서드입니다.
     * 각 명령어는 plugin.yml에도 정의되어 있어야 합니다.
     */
    private void registerCommands() {
        // HelloCommand 등록
        HelloCommand helloCommand = new HelloCommand(messageService);
        this.getCommand("hello").setExecutor(helloCommand);
        
        getLogger().info("명령어 등록 완료: /hello");
    }
    
    /**
     * 이벤트 리스너들을 등록하는 메서드입니다.
     */
    private void registerListeners() {
        // 플레이어 접속 리스너 등록
        PlayerJoinListener joinListener = new PlayerJoinListener(playerRepository, messageService);
        getServer().getPluginManager().registerEvents(joinListener, this);
        
        getLogger().info("이벤트 리스너 등록 완료: PlayerJoinListener");
    }
    
    // === Getter 메서드들 ===
    // 다른 클래스에서 이 플러그인의 컴포넌트들에 접근할 수 있도록 제공합니다
    
    /**
     * 플러그인 인스턴스를 반환합니다.
     * 정적 메서드이므로 어디서든 Tiny_pg_hunterAPI.getInstance()로 접근 가능합니다.
     */
    public static Tiny_pg_hunterAPI getInstance() {
        return instance;
    }
    
    /**
     * 데이터베이스 매니저를 반환합니다.
     */
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
    
    /**
     * 플레이어 데이터 레포지토리를 반환합니다.
     */
    public PlayerDataRepository getPlayerRepository() {
        return playerRepository;
    }
    
    /**
     * 메시지 서비스를 반환합니다.
     */
    public MessageService getMessageService() {
        return messageService;
    }
}