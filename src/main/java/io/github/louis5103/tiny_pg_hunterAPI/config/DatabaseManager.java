package io.github.louis5103.tiny_pg_hunterAPI.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import io.github.louis5103.tiny_pg_hunterAPI.model.entity.PlayerData;

import java.io.File;
import java.util.Properties;
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
            FileConfiguration config = plugin.getConfig();
            String dbType = config.getString("database.type", "sqlite");
            
            Properties properties = new Properties();
            
            if ("mysql".equals(dbType)) {
                setupMySQLProperties(config, properties);
            } else {
                setupSQLiteProperties(config, properties);
            }
            
            // config.yml의 hibernate 섹션 전체 로드 (개발 편의성)
            loadHibernateProperties(config, properties);
            
            StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySettings(properties)
                .build();
                
            SessionFactory sessionFactory = new MetadataSources(registry)
                .addAnnotatedClass(PlayerData.class)
                .getMetadataBuilder()
                .build()
                .getSessionFactoryBuilder()
                .build();
                
            entityManagerFactory = sessionFactory.unwrap(EntityManagerFactory.class);
            
            plugin.getLogger().info("데이터베이스 연결 성공: " + dbType.toUpperCase());
            
        } catch (Exception e) {
            plugin.getLogger().severe("데이터베이스 초기화 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void setupMySQLProperties(FileConfiguration config, Properties properties) {
        String host = config.getString("database.mysql.host", "localhost");
        int port = config.getInt("database.mysql.port", 3306);
        String database = config.getString("database.mysql.database", "minecraft_db");
        String username = config.getString("database.mysql.username", "root");
        String password = config.getString("database.mysql.password", "password");
        
        String url = String.format("jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=Asia/Seoul&allowPublicKeyRetrieval=true", 
                                  host, port, database);
        
        properties.setProperty("hibernate.connection.url", url);
        properties.setProperty("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
        properties.setProperty("hibernate.connection.username", username);
        properties.setProperty("hibernate.connection.password", password);
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
    }
    
    private void setupSQLiteProperties(FileConfiguration config, Properties properties) {
        File dbDir = new File(plugin.getDataFolder(), "database");
        if (!dbDir.exists()) {
            dbDir.mkdirs();
        }
        
        String file = config.getString("database.sqlite.file", "database/player_data.db");
        String fullPath = plugin.getDataFolder().getAbsolutePath() + "/" + file;
        
        properties.setProperty("hibernate.connection.url", "jdbc:sqlite:" + fullPath);
        properties.setProperty("hibernate.connection.driver_class", "org.sqlite.JDBC");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.SQLiteDialect");
    }
    
    private void loadHibernateProperties(FileConfiguration config, Properties properties) {
        // hibernate.* 하위의 모든 설정을 그대로 properties에 추가
        if (config.isConfigurationSection("hibernate")) {
            for (String key : config.getConfigurationSection("hibernate").getKeys(true)) {
                Object value = config.get("hibernate." + key);
                if (value != null) {
                    properties.setProperty("hibernate." + key, value.toString());
                }
            }
        }
    }

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
            plugin.getLogger().warning("데이터베이스 작업 중 오류: " + e.getMessage());
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
