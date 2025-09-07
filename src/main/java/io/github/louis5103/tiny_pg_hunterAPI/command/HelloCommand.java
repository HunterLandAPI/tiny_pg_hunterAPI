package io.github.louis5103.tiny_pg_hunterAPI.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.springframework.stereotype.Component;

// ✨ 명령어 실행 클래스도 Spring의 컴포넌트로 등록합니다.
@Component("helloCommand") // Bean의 이름을 'helloCommand'로 명시
public class HelloCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("§a안녕하세요! Spring이 응답하는 명령어입니다!");
        return true;
    }
}