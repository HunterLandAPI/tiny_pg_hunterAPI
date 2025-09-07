package io.github.louis5103.tiny_pg_hunterAPI.service;

import org.bukkit.entity.Player;
import org.springframework.stereotype.Service;

// @Service는 @Component와 기능적으로 동일하지만,
// "이 클래스는 비즈니스 로직을 처리하는 서비스 계층이다"라는 의미를 명확히 해줍니다.
@Service
public class MessageService {

    public String getJoinMessage(Player player) {
        String playerName = player.getName();
        // 나중에 이곳에서 플레이어의 랭크나 길드 정보를 가져와 메시지를 꾸밀 수 있습니다.
        return "§b[§aSpring§b] §e" + playerName + "§f님이 서버에 접속했습니다! (서비스에서 보냄)";
    }
}