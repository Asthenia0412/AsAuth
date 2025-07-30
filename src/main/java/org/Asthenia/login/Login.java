package org.Asthenia.login;

import org.Asthenia.login.Handler.Commands;
import org.Asthenia.login.Listener.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class Login extends JavaPlugin {
    private final Set<UUID> loggedInPlayers = new HashSet<>();
    private final Set<UUID> frozenPlayers = new HashSet<>();
    private Commands commands;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.commands = new Commands(this);

        // 注册命令和事件
        registerCommands();
        registerEvents();
    }

    private void registerCommands() {
        try {
            // 获取Bukkit的CommandMap
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());

            // 注册/login命令
            registerCommand(commandMap, "login", (sender, command, label, args) -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("只有玩家可以使用该命令");
                    return true;
                }
                return commands.handleLogin(player, args);
            });

            // 注册/register命令
            registerCommand(commandMap, "register", (sender, command, label, args) -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("只有玩家可以使用该命令");
                    return true;
                }
                return commands.handleRegister(player, args);
            });

        } catch (Exception e) {
            getLogger().severe("命令注册失败: " + e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    private void registerCommand(CommandMap commandMap, String name, CommandExecutor executor) {
        Command command = new Command(name) {
            @Override
            public boolean execute(CommandSender sender, String label, String[] args) {
                return executor.onCommand(sender, this, label, args);
            }
        };
        commandMap.register(name, "asauth", command);
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    public boolean isPlayerLoggedIn(UUID uuid) {
        return loggedInPlayers.contains(uuid);
    }

    public void setPlayerLoggedIn(UUID uuid, boolean loggedIn) {
        if (loggedIn) {
            loggedInPlayers.add(uuid);
        } else {
            loggedInPlayers.remove(uuid);
        }
    }

    public void setFrozen(Player player, boolean frozen) {
        UUID uuid = player.getUniqueId();
        if (frozen) {
            frozenPlayers.add(uuid);
            player.setWalkSpeed(0);
            player.setFlySpeed(0);
            player.setInvulnerable(true);
        } else {
            frozenPlayers.remove(uuid);
            player.setWalkSpeed(0.2f);
            player.setFlySpeed(0.1f);
            player.setInvulnerable(false);
        }
    }

    public boolean isFrozen(UUID uuid) {
        return frozenPlayers.contains(uuid);
    }

    @FunctionalInterface
    private interface CommandExecutor {
        boolean onCommand(CommandSender sender, Command command, String label, String[] args);
    }
}