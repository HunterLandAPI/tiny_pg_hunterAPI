package io.github.louis5103.tiny_pg_hunterAPI.command;

import io.github.louis5103.tiny_pg_hunterAPI.service.MessageService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * /hello 명령어를 처리하는 커맨드 실행 클래스입니다.
 * 
 * CommandExecutor 인터페이스의 역할:
 * - Bukkit(마인크래프트 서버 API)이 정의한 명령어 처리 인터페이스입니다
 * - onCommand() 메서드를 구현해서 명령어 실행 로직을 정의합니다
 * - plugin.yml에서 정의한 명령어와 연결됩니다
 * 
 * 의존성 주입 패턴 적용:
 * 이전에는 Spring의 @Component를 사용했지만, 이제는 생성자를 통해
 * 필요한 서비스들을 직접 받아옵니다. 이를 "생성자 기반 의존성 주입"이라고 합니다.
 * 
 * 생성자 기반 의존성 주입의 장점:
 * 1. 의존성이 명확하게 드러납니다 (생성자 파라미터로 확인 가능)
 * 2. 불변성(immutability)을 보장할 수 있습니다 (final 필드 사용)
 * 3. 테스트하기 쉽습니다 (Mock 객체를 주입하기 쉬움)
 * 4. 컴파일 타임에 의존성 오류를 발견할 수 있습니다
 */
public class HelloCommand implements CommandExecutor {
    
    /**
     * MessageService 의존성을 저장하는 필드입니다.
     * 
     * final 키워드의 의미:
     * - 이 필드는 생성자에서 한 번 설정된 후 변경될 수 없습니다
     * - 이를 통해 불변성(immutability)을 보장하고, 예상치 못한 변경을 방지합니다
     * - 또한 이 필드가 null이 될 수 없음을 컴파일러가 보장합니다
     */
    private final MessageService messageService;
    
    /**
     * HelloCommand의 생성자입니다.
     * 
     * 생성자 기반 의존성 주입의 핵심 부분입니다:
     * - 이 클래스가 동작하기 위해 필요한 모든 의존성을 생성자 파라미터로 받습니다
     * - 의존성이 null이면 생성 자체가 불가능하므로, 안전성이 보장됩니다
     * - 메인 클래스에서 이 생성자를 호출할 때 MessageService 인스턴스를 전달합니다
     * 
     * @param messageService 메시지 생성을 담당하는 서비스 객체
     */
    public HelloCommand(MessageService messageService) {
        // null 체크를 통한 방어적 프로그래밍
        // 만약 null이 전달되면 즉시 예외를 발생시켜서 문제를 조기에 발견할 수 있습니다
        if (messageService == null) {
            throw new IllegalArgumentException("MessageService는 null일 수 없습니다!");
        }
        
        this.messageService = messageService;
    }

    /**
     * /hello 명령어가 실행될 때 호출되는 메서드입니다.
     * 
     * Bukkit의 명령어 처리 시스템:
     * - 플레이어가 /hello를 입력하면 Bukkit이 이 메서드를 자동으로 호출합니다
     * - plugin.yml에서 정의한 명령어와 이 클래스를 연결해야 작동합니다
     * - 메인 클래스의 registerCommands()에서 연결 작업을 수행합니다
     * 
     * @param sender 명령어를 실행한 주체 (플레이어, 콘솔, 커맨드 블록 등)
     * @param command 실행된 명령어 정보
     * @param label 실제로 입력된 명령어 문자열 (별칭 포함)
     * @param args 명령어 뒤에 입력된 추가 인수들
     * @return true면 명령어가 성공적으로 처리됨, false면 사용법을 표시
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // 여기서 비즈니스 로직을 직접 구현하지 않고, MessageService에 위임합니다
        // 이는 "단일 책임 원칙(Single Responsibility Principle)"을 따르는 것입니다:
        // - HelloCommand는 명령어 처리만 담당
        // - MessageService는 메시지 생성만 담당
        
        String helloMessage = messageService.getCommandMessage("안녕하세요! 플러그인이 정상적으로 작동하고 있습니다!");
        sender.sendMessage(helloMessage);
        
        // 추가 기능들을 여기서 구현할 수 있습니다:
        // if (args.length > 0) {
        //     // /hello 뒤에 추가 인수가 있는 경우의 처리
        //     String customMessage = messageService.getCommandMessage("안녕하세요, " + args[0] + "님!");
        //     sender.sendMessage(customMessage);
        // }
        
        // true를 반환하면 명령어가 성공적으로 처리되었음을 의미합니다
        // false를 반환하면 plugin.yml에서 정의한 usage 메시지가 표시됩니다
        return true;
    }
}
