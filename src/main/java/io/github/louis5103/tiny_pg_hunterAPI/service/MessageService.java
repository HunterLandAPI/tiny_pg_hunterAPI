package io.github.louis5103.tiny_pg_hunterAPI.service;

import org.bukkit.entity.Player;

/**
 * 메시지 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * 
 * 서비스 클래스의 역할:
 * - 비즈니스 로직을 캡슐화하여 재사용성을 높입니다
 * - 메시지 포맷팅, 다국어 지원, 권한별 메시지 등을 처리합니다
 * - 여러 다른 클래스에서 공통으로 사용할 수 있는 기능들을 제공합니다
 * 
 * 이전에는 Spring의 @Service 어노테이션을 사용했지만,
 * 이제는 단순한 POJO(Plain Old Java Object)로 구현합니다.
 * 이렇게 하면 Spring 없이도 동일한 기능을 수행할 수 있습니다.
 */
public class MessageService {
    
    // 메시지 색상 코드들을 상수로 정의해서 일관성 있게 사용
    private static final String PREFIX_COLOR = "§b";
    private static final String ACCENT_COLOR = "§a";
    private static final String PLAYER_NAME_COLOR = "§e";
    private static final String TEXT_COLOR = "§f";
    
    /**
     * 플레이어 접속 시 표시될 메시지를 생성합니다.
     * 
     * @param player 접속한 플레이어 객체
     * @return 포맷팅된 접속 메시지
     * 
     * 메시지 포맷 설명:
     * - §b[§aTiny PG§b] : 플러그인 프리픽스 (파란색 괄호, 초록색 이름)
     * - §e플레이어명§f : 노란색 플레이어 이름, 흰색 텍스트
     */
    public String getJoinMessage(Player player) {
        String playerName = player.getName();
        
        // 나중에 여기서 추가 기능들을 구현할 수 있습니다:
        // - 플레이어의 권한 레벨에 따른 다른 메시지
        // - 첫 접속 여부에 따른 환영 메시지
        // - 길드나 파티 정보 포함
        // - 다국어 지원
        
        return String.format("%s[%sTiny PG%s] %s%s%s님이 서버에 접속했습니다!",
                PREFIX_COLOR, ACCENT_COLOR, PREFIX_COLOR,
                PLAYER_NAME_COLOR, playerName, TEXT_COLOR);
    }
    
    /**
     * 플레이어 퇴장 시 표시될 메시지를 생성합니다.
     * 
     * @param player 퇴장한 플레이어 객체
     * @return 포맷팅된 퇴장 메시지
     */
    public String getQuitMessage(Player player) {
        String playerName = player.getName();
        return String.format("%s[%sTiny PG%s] %s%s%s님이 서버에서 나갔습니다.",
                PREFIX_COLOR, ACCENT_COLOR, PREFIX_COLOR,
                PLAYER_NAME_COLOR, playerName, TEXT_COLOR);
    }
    
    /**
     * 명령어 응답 메시지를 생성합니다.
     * 
     * @param message 기본 메시지 내용
     * @return 플러그인 프리픽스가 포함된 메시지
     */
    public String getCommandMessage(String message) {
        return String.format("%s[%sTiny PG%s] %s%s",
                PREFIX_COLOR, ACCENT_COLOR, PREFIX_COLOR,
                TEXT_COLOR, message);
    }
    
    /**
     * 오류 메시지를 생성합니다.
     * 
     * @param errorMessage 오류 내용
     * @return 빨간색으로 포맷팅된 오류 메시지
     */
    public String getErrorMessage(String errorMessage) {
        return String.format("%s[%sTiny PG%s] §c%s",
                PREFIX_COLOR, ACCENT_COLOR, PREFIX_COLOR, errorMessage);
    }
    
    /**
     * 성공 메시지를 생성합니다.
     * 
     * @param successMessage 성공 내용
     * @return 초록색으로 포맷팅된 성공 메시지
     */
    public String getSuccessMessage(String successMessage) {
        return String.format("%s[%sTiny PG%s] %s%s",
                PREFIX_COLOR, ACCENT_COLOR, PREFIX_COLOR,
                ACCENT_COLOR, successMessage);
    }
}
