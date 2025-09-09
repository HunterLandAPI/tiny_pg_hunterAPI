package io.github.louis5103.tiny_pg_hunterAPI.listener;

import io.github.louis5103.tiny_pg_hunterAPI.model.entity.PlayerData;
import io.github.louis5103.tiny_pg_hunterAPI.model.repository.PlayerDataRepository;
import io.github.louis5103.tiny_pg_hunterAPI.service.MessageService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * 플레이어 접속 이벤트를 처리하는 리스너 클래스입니다.
 * 
 * Bukkit 이벤트 시스템의 작동 원리:
 * 1. 마인크래프트에서 특정 행동이 발생합니다 (예: 플레이어 접속)
 * 2. Bukkit이 해당 이벤트 객체를 생성합니다 (예: PlayerJoinEvent)
 * 3. 등록된 모든 리스너들에게 이벤트를 전달합니다
 * 4. 각 리스너의 @EventHandler 메서드가 호출됩니다
 * 5. 리스너에서 원하는 로직을 수행합니다 (데이터베이스 저장, 메시지 전송 등)
 * 
 * 이벤트 리스너의 특징:
 * - Listener 인터페이스를 구현해야 합니다
 * - @EventHandler 어노테이션이 붙은 메서드가 실제 이벤트 처리 메서드입니다
 * - 메인 클래스에서 registerEvents()로 등록해야 작동합니다
 * - 하나의 리스너 클래스에서 여러 종류의 이벤트를 처리할 수 있습니다
 * 
 * 의존성 주입 적용:
 * 이전에는 Spring의 @Component와 @RequiredArgsConstructor를 사용했지만,
 * 이제는 직접 생성자를 만들어서 필요한 의존성들을 주입받습니다.
 */
public class PlayerJoinListener implements Listener {
    
    /**
     * 플레이어 데이터 관리를 위한 레포지토리입니다.
     * 
     * 레포지토리 패턴의 역할:
     * - 데이터 접근 로직을 캡슐화합니다
     * - 비즈니스 로직과 데이터 접근 로직을 분리합니다
     * - 데이터베이스 종류가 바뀌어도 비즈니스 로직은 영향받지 않습니다
     * - 테스트할 때 Mock 레포지토리로 쉽게 대체할 수 있습니다
     */
    private final PlayerDataRepository playerRepository;
    
    /**
     * 메시지 생성을 담당하는 서비스입니다.
     * 
     * 서비스 계층의 역할:
     * - 메시지 포맷팅과 같은 비즈니스 로직을 담당합니다
     * - 여러 곳에서 재사용할 수 있는 공통 기능을 제공합니다
     * - 복잡한 메시지 생성 로직을 캡슐화합니다
     */
    private final MessageService messageService;
    
    /**
     * PlayerJoinListener의 생성자입니다.
     * 
     * 생성자 기반 의존성 주입의 활용:
     * - 이 리스너가 제대로 작동하기 위해 필요한 모든 의존성을 명시적으로 받습니다
     * - playerRepository: 플레이어 데이터를 저장하고 조회하기 위해 필요
     * - messageService: 접속 메시지를 생성하기 위해 필요
     * - 생성 시점에 의존성이 모두 준비되므로 안전성이 보장됩니다
     * 
     * @param playerRepository 플레이어 데이터 접근을 담당하는 레포지토리
     * @param messageService 메시지 생성을 담당하는 서비스
     */
    public PlayerJoinListener(PlayerDataRepository playerRepository, MessageService messageService) {
        // 방어적 프로그래밍: null 체크를 통해 잘못된 상태의 객체 생성을 방지
        if (playerRepository == null) {
            throw new IllegalArgumentException("PlayerDataRepository는 null일 수 없습니다!");
        }
        if (messageService == null) {
            throw new IllegalArgumentException("MessageService는 null일 수 없습니다!");
        }
        
        this.playerRepository = playerRepository;
        this.messageService = messageService;
    }

    /**
     * 플레이어가 서버에 접속했을 때 호출되는 이벤트 핸들러입니다.
     * 
     * @EventHandler 어노테이션의 역할:
     * - Bukkit에게 "이 메서드가 이벤트를 처리한다"고 알려줍니다
     * - 메서드 파라미터의 타입을 보고 어떤 이벤트를 처리할지 자동으로 판단합니다
     * - PlayerJoinEvent 타입이므로 플레이어 접속 이벤트를 처리합니다
     * 
     * 이벤트 처리 시 수행하는 작업들:
     * 1. 플레이어 데이터 조회 또는 생성
     * 2. 마지막 로그인 시간 업데이트
     * 3. 접속 메시지 설정
     * 4. 필요시 추가 초기화 작업
     * 
     * @param event 플레이어 접속 이벤트 객체 (플레이어 정보, 접속 메시지 등 포함)
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // 이벤트에서 접속한 플레이어 객체를 가져옵니다
        Player player = event.getPlayer();
        String playerUUID = player.getUniqueId().toString();
        String playerName = player.getName();
        
        try {
            // === 1단계: 플레이어 데이터 처리 ===
            // 데이터베이스에서 플레이어 정보를 조회합니다
            PlayerData playerData = playerRepository.findByUuid(playerUUID);
            
            if (playerData == null) {
                // 첫 접속 플레이어: 새로운 데이터 생성
                playerData = new PlayerData(playerUUID, playerName);
                playerRepository.save(playerData);
                
                // 로그에 첫 접속 기록
                player.getServer().getLogger().info(
                    String.format("새로운 플레이어 등록: %s (UUID: %s)", playerName, playerUUID)
                );
            } else {
                // 기존 플레이어: 마지막 로그인 시간 업데이트
                playerData.setPlayerName(playerName); // 닉네임이 변경되었을 수도 있으므로 업데이트
                playerData.updateLastLogin();
                playerRepository.save(playerData);
                
                // 로그에 재접속 기록
                player.getServer().getLogger().info(
                    String.format("플레이어 재접속: %s (총 플레이 시간: %d분)", 
                        playerName, playerData.getPlayTimeMinutes())
                );
            }
            
            // === 2단계: 접속 메시지 설정 ===
            // MessageService를 사용해서 일관된 형태의 접속 메시지를 생성합니다
            String joinMessage = messageService.getJoinMessage(player);
            
            // 기본 접속 메시지를 우리가 만든 메시지로 교체합니다
            // setJoinMessage(null)로 설정하면 접속 메시지가 표시되지 않습니다
            event.setJoinMessage(joinMessage);
            
            // === 3단계: 추가 환영 작업 (선택사항) ===
            // 여기서 추가적인 환영 작업들을 수행할 수 있습니다:
            
            // 첫 접속 플레이어에게 환영 메시지 전송
            if (playerData.getPlayTimeMinutes() == 0) {
                player.sendMessage(messageService.getSuccessMessage("처음 오신 것을 환영합니다!"));
                player.sendMessage(messageService.getCommandMessage("/hello 명령어로 플러그인을 테스트해보세요!"));
            }
            
            // 특정 조건에 따른 추가 메시지 (예: VIP 플레이어, 관리자 등)
            if (player.hasPermission("tinypg.vip")) {
                player.sendMessage(messageService.getSuccessMessage("VIP 플레이어로 접속하셨습니다!"));
            }
            
        } catch (Exception e) {
            // 데이터베이스 오류 등이 발생해도 플레이어 접속은 막지 않습니다
            // 대신 로그에 오류를 기록하고 기본 메시지를 표시합니다
            player.getServer().getLogger().warning(
                String.format("플레이어 접속 처리 중 오류 발생 (%s): %s", playerName, e.getMessage())
            );
            
            // 오류가 발생해도 기본적인 환영 메시지는 표시합니다
            String fallbackMessage = messageService.getJoinMessage(player);
            event.setJoinMessage(fallbackMessage);
            
            // 선택사항: 관리자에게 오류 알림
            if (player.hasPermission("tinypg.admin")) {
                player.sendMessage(messageService.getErrorMessage("데이터베이스 연결에 문제가 발생했습니다."));
            }
        }
    }
    
    /**
     * 향후 확장을 위한 예시: 플레이어 퇴장 이벤트 처리
     * 
     * 추가 이벤트 핸들러 구현 예시입니다.
     * 실제로 사용하려면 주석을 해제하고 PlayerQuitEvent를 import해야 합니다.
     */
    /*
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String quitMessage = messageService.getQuitMessage(player);
        event.setQuitMessage(quitMessage);
        
        // 플레이어의 세션 플레이 시간을 계산해서 데이터베이스에 저장
        // (이를 위해서는 접속 시간을 어딘가에 저장해두어야 합니다)
    }
    */
}
