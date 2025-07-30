package org.Asthenia.login.Listener;

import org.Asthenia.login.Handler.Commands;
import org.Asthenia.login.Login;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerListener implements Listener {
    private final Login plugin;
    private final Commands commands;

    public PlayerListener(Login plugin) {
        this.plugin = plugin;
        this.commands = new Commands(plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getConfig().contains("players." + player.getName())) {
            player.sendMessage("欢迎新玩家！请使用 /register <密码> <确认密码> 注册");
        } else {
            player.sendMessage("请使用 /login <密码> 登录");
        }

        // 设置30秒超时
        commands.setupTimeoutTask(player);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!plugin.isPlayerLoggedIn(player.getUniqueId())) {
            // 取消移动
            event.setCancelled(true);
        }
    }
}