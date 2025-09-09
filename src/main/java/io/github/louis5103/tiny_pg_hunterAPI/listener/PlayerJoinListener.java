package io.github.louis5103.tiny_pg_hunterAPI.listener;


import io.github.louis5103.tiny_pg_hunterAPI.service.MessageService;
import lombok.RequiredArgsConstructor; // Lombok 어노테이션 추가
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor // ✨ final 필드에 대한 생성자를 자동으로 만들어주는 Lombok 어노테이션
public class PlayerJoinListener implements Listener {

    // ✨ 'final'로 선언된 이 필드에 Spring이 MessageService 객체를 자동으로 주입(injection)해줍니다.
    private final MessageService messageService;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // 직접 메시지를 만들지 않고, 주입받은 MessageService의 기능을 호출합니다.
        String message = messageService.getJoinMessage(event.getPlayer());
        event.setJoinMessage(message);
    }
}