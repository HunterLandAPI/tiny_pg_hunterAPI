package io.github.louis5103.tiny_pg_hunterAPI.model.repository;

import io.github.louis5103.tiny_pg_hunterAPI.config.DatabaseManager;
import io.github.louis5103.tiny_pg_hunterAPI.model.entity.PlayerData;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class PlayerDataRepository {
    private final DatabaseManager databaseManager;

    public PlayerData findByUuid(String uuid) {
        return databaseManager.executeInTransaction(em -> {
            return em.find(PlayerData.class, uuid);
        });
    }

    public PlayerData save(PlayerData playerData) {
        return databaseManager.executeInTransaction(em -> {
            return em.merge(playerData); // 새로 생성하거나 기존 데이터 업데이트
        });
    }

    public List<PlayerData> findTopPlayersByMoney(int limit) {
        return databaseManager.executeInTransaction(em -> {
            TypedQuery<PlayerData> query = em.createQuery(
                    "SELECT p FROM PlayerData p ORDER BY p.money DESC",
                    PlayerData.class
            );
            query.setMaxResults(limit);
            return query.getResultList();
        });
    }

    public void updatePlayTime(String uuid, long additionalMinutes) {
        databaseManager.executeInTransaction(em -> {
            PlayerData player = em.find(PlayerData.class, uuid);
            if (player != null) {
                player.setPlayTimeMinutes(player.getPlayTimeMinutes() + additionalMinutes);
                em.merge(player);
            }
            return null;
        });
    }
}