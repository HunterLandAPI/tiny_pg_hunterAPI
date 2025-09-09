package io.github.louis5103.tiny_pg_hunterAPI.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;

/**
 * 플레이어 데이터를 저장하는 JPA 엔티티 클래스입니다.
 * 
 * @Entity: 이 클래스가 데이터베이스 테이블과 매핑되는 엔티티임을 나타냅니다
 * @Table: 실제 데이터베이스에서 사용할 테이블 이름을 지정합니다
 * @Data: Lombok이 자동으로 getter, setter, toString, equals, hashCode를 생성합니다
 */
@Entity
@Table(name = "player_data")
@Data  // Lombok: getter, setter, toString, equals, hashCode 자동 생성
@NoArgsConstructor  // Lombok: 기본 생성자 자동 생성 (JPA 필수)
@AllArgsConstructor // Lombok: 모든 필드를 받는 생성자 자동 생성
public class PlayerData {
    
    /**
     * 플레이어의 고유 식별자 (UUID)
     * @Id: 이 필드가 기본키임을 나타냅니다
     * 마인크래프트에서 각 플레이어는 고유한 UUID를 가집니다
     */
    @Id
    private String uuid;

    /**
     * 플레이어의 닉네임
     * @Column: 데이터베이스 컬럼과의 매핑을 설정합니다
     * name 속성으로 실제 컬럼명을 지정할 수 있습니다
     */
    @Column(name = "player_name", nullable = false)
    private String playerName;

    /**
     * 플레이어가 보유한 게임 내 화폐
     * Double 타입을 사용해서 소수점 단위의 돈도 관리할 수 있습니다
     */
    @Column(name = "money", nullable = false)
    private Double money = 0.0;

    /**
     * 플레이어의 마지막 로그인 시간
     * @Temporal: Date 타입 필드의 정밀도를 설정합니다
     * TIMESTAMP는 날짜와 시간을 모두 저장합니다
     */
    @Column(name = "last_login")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLogin;

    /**
     * 플레이어의 총 플레이 시간 (분 단위)
     * Long 타입을 사용해서 큰 숫자도 저장할 수 있습니다
     */
    @Column(name = "play_time_minutes", nullable = false)
    private Long playTimeMinutes = 0L;

    /**
     * 플레이어 생성을 위한 편의 생성자
     * 새로운 플레이어가 처음 접속했을 때 사용됩니다
     */
    public PlayerData(String uuid, String playerName) {
        this.uuid = uuid;
        this.playerName = playerName;
        this.money = 0.0;
        this.playTimeMinutes = 0L;
        this.lastLogin = new Date(); // 현재 시간으로 설정
    }

    /**
     * 마지막 로그인 시간을 현재 시간으로 업데이트
     * 플레이어가 접속할 때마다 호출됩니다
     */
    public void updateLastLogin() {
        this.lastLogin = new Date();
    }

    /**
     * 플레이어의 돈을 추가합니다
     * @param amount 추가할 금액 (음수면 차감)
     * @return 변경 후 총 금액
     */
    public Double addMoney(Double amount) {
        this.money += amount;
        return this.money;
    }

    /**
     * 플레이 시간을 추가합니다
     * @param minutes 추가할 시간 (분 단위)
     * @return 변경 후 총 플레이 시간
     */
    public Long addPlayTime(Long minutes) {
        this.playTimeMinutes += minutes;
        return this.playTimeMinutes;
    }
}