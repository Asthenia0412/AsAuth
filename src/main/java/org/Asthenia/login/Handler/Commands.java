package org.Asthenia.login.Handler;

import org.Asthenia.login.Login;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Commands {
    private final Login plugin;
    private final Map<UUID, BukkitTask> timeoutTasks = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> loginAttempts = new ConcurrentHashMap<>();
    private final int MAX_ATTEMPTS = 3;
    private final int TIMEOUT_SECONDS = 30;

    public Commands(Login plugin) {
        this.plugin = plugin;
    }

    // 处理注册命令
    public boolean handleRegister(Player player, String[] args) {
        UUID uuid = player.getUniqueId();

        if (args.length != 2) {
            player.sendMessage("用法: /register <密码> <确认密码>");
            return true;
        }

        if (!args[0].equals(args[1])) {
            player.sendMessage("两次输入的密码不一致");
            return true;
        }

        plugin.getConfig().set("players." + player.getName() + ".password", args[0]);
        plugin.saveConfig();
        player.sendMessage("注册成功！");
        cancelTimeoutTask(uuid);
        plugin.setFrozen(player, false);
        plugin.setPlayerLoggedIn(uuid, true);
        return true;
    }

    // 处理登录命令
    public boolean handleLogin(Player player, String[] args) {
        UUID uuid = player.getUniqueId();

        if (args.length != 1) {
            player.sendMessage("用法: /login <密码>");
            return true;
        }

        String storedPassword = plugin.getConfig().getString("players." + player.getName() + ".password");
        if (storedPassword == null) {
            player.sendMessage("您尚未注册账号，请先注册");
            return true;
        }

        if (storedPassword.equals(args[0])) {
            // 登录成功
            player.sendMessage("登录成功！");
            cancelTimeoutTask(uuid);
            loginAttempts.remove(uuid);
            plugin.setFrozen(player, false);
            plugin.setPlayerLoggedIn(uuid, true);
            return true;
        } else {
            // 登录失败
            int attempts = loginAttempts.getOrDefault(uuid, 0) + 1;
            loginAttempts.put(uuid, attempts);

            if (attempts >= MAX_ATTEMPTS) {
                player.kickPlayer("登录尝试次数过多");
            } else {
                player.sendMessage("密码错误，还剩 " + (MAX_ATTEMPTS - attempts) + " 次尝试机会");
            }
            return true;
        }
    }

    // 为玩家设置30秒超时任务
    public void setupTimeoutTask(Player player) {
        UUID uuid = player.getUniqueId();
        cancelTimeoutTask(uuid);
        plugin.setFrozen(player, true);

        timeoutTasks.put(uuid, Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!plugin.isPlayerLoggedIn(uuid)) {
                player.kickPlayer("登录超时（30秒内未完成登录）");
                timeoutTasks.remove(uuid);
                loginAttempts.remove(uuid);
            }
        }, TIMEOUT_SECONDS * 20L));
    }

    // 取消超时任务
    private void cancelTimeoutTask(UUID uuid) {
        if (timeoutTasks.containsKey(uuid)) {
            timeoutTasks.get(uuid).cancel();
            timeoutTasks.remove(uuid);
        }
    }
}