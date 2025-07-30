package org.Asthenia.login.Listener;

import org.Asthenia.login.Login;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;

import java.util.UUID;

public class PlayerListener implements Listener {
    private final Login plugin;

    public PlayerListener(Login plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        // 初始化玩家状态
        plugin.setFrozen(player, true);
        // 发送提示消息
        String message = plugin.getConfig().contains("players." + player.getName())
                ? "请使用 /login <密码> 登录"
                : "欢迎新玩家！请使用 /register <密码> <确认密码> 注册";
        player.sendMessage(message);
    }



}