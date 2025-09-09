package io.github.louis5103.tiny_pg_hunterAPI;


import be.lennertsoffers.spigotbootstrapper.library.SpringSpigot;
import org.bukkit.plugin.java.JavaPlugin;

public class Tiny_pg_hunterAPI extends JavaPlugin {

    @Override
    public void onEnable() {
        // 플러그인이 켜질 때 Spring 컨텍스트를 초기화 (가장 중요한 부분)
        SpringSpigot.initialize(this);
        getLogger().info("Plugin enabled with Spring!");
    }

    @Override
    public void onDisable() {
        // 플러그인이 꺼질 때 Spring 컨텍스트를 안전하게 종료
        SpringSpigot.unload();
        getLogger().info("Plugin disabled.");
    }
}