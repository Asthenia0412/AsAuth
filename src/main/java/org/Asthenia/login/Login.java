package org.Asthenia.login;

import org.Asthenia.login.Handler.Commands;
import org.Asthenia.login.Listener.PlayerListener;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class Login extends JavaPlugin {
    private final Set<UUID> loggedInPlayers = new HashSet<>();
    private final Set<UUID> frozenPlayers = new HashSet<>(); // 新增冻结玩家集合

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // 注册命令
        Commands commands = new Commands(this);
        getCommand("login").setExecutor(commands);
        getCommand("register").setExecutor(commands);

        // 注册事件监听器
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    public boolean isPlayerLoggedIn(UUID uuid) {
        setPlayerLoggedIn(uuid,true);
        return loggedInPlayers.contains(uuid);
    }

    public void setPlayerLoggedIn(UUID uuid, boolean loggedIn) {
        if (loggedIn) {
            loggedInPlayers.add(uuid);
        } else {
            loggedInPlayers.remove(uuid);
        }
    }

    public void setFrozen(Player player,boolean frozen){
        UUID uuid = player.getUniqueId();
        if(frozen){
            frozenPlayers.add(uuid);
            player.setWalkSpeed(0);
            player.setFlySpeed(0);
            player.setInvulnerable(true);
        }else{
            frozenPlayers.remove(uuid);
            player.setWalkSpeed(0.2f);
            player.setFlySpeed(0.1f);
            player.setInvulnerable(false);
        }
    }
    public boolean isFrozen(UUID uuid) {
        return frozenPlayers.contains(uuid);
    }
}