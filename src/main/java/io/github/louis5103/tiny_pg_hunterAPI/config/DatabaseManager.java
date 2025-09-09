package io.github.louis5103.tiny_pg_hunterAPI.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Function;


public class DatabaseManager {
    private EntityManagerFactory entityManagerFactory;
    private final JavaPlugin plugin;

    public DatabaseManager(JavaPlugin plugin) {
        this.plugin = plugin;
        initializeDatabase();
    }

    private void initializeDatabase() {
        try {
            // JPA EntityManagerFactory 생성
            // 이 과정에서 persistence.xml의 설정이 읽어집니다
            entityManagerFactory = Persistence.createEntityManagerFactory("minecraft-plugin-db");
            plugin.getLogger().info("데이터베이스 연결이 성공적으로 초기화되었습니다.");
        } catch (Exception e) {
            plugin.getLogger().severe("데이터베이스 초기화 실패: " + e.getMessage());
        }
    }

    // 트랜잭션을 안전하게 처리하는 헬퍼 메서드
    public <T> T executeInTransaction(Function<EntityManager, T> operation) {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();
            T result = operation.apply(em);
            transaction.commit();
            return result;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            plugin.getLogger().warning("데이터베이스 작업 중 오류 발생: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }

    public void shutdown() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }
}