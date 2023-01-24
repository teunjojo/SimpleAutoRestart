package com.teunjojo;

import java.util.Calendar;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class AutoRestart extends JavaPlugin {
    public static long serverStartMillis;

    @Override
    public void onEnable() {
        serverStartMillis = System.currentTimeMillis();
        this.saveDefaultConfig();
        this.getConfig();
        FileConfiguration config = this.getConfig();
        saveConfig();

        int restartHour = (config.getInt("hour"));
        Plugin plugin = this;

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                long upTimeMillis = System.currentTimeMillis() - serverStartMillis;
                if (upTimeMillis > 3600000) {
                    Calendar cal = Calendar.getInstance();
                    if (cal.get(Calendar.HOUR_OF_DAY) == restartHour)
                        startCountdown();
                }
            }
        }, 0L, 1200L);
    }

    @Override
    public void onDisable() {
    }

    public void startCountdown() {
        Bukkit.broadcastMessage("Restarting in 5 minutes");
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
            Bukkit.broadcastMessage("Restarting in 3 minutes");
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
                Bukkit.broadcastMessage("Restarting in 2 minutes");
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
                    Bukkit.broadcastMessage("Restarting in 1 minute");
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
                        Bukkit.broadcastMessage("Restarting in 3");
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 100, 0);
                            player.sendTitle("Restarting in 3", "", 0, 1, 0);
                        }
                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
                            Bukkit.broadcastMessage("Restarting in 2");
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 100,
                                        0);
                                player.sendTitle("Restarting in 2", "", 0, 1, 0);
                            }
                            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
                                Bukkit.broadcastMessage("Restarting in 1");
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                                            100, 0);
                                    player.sendTitle("Restarting in 1", "", 0, 1, 0);
                                }
                                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
                                    Bukkit.broadcastMessage("Restarting now");
                                    for (Player player : Bukkit.getOnlinePlayers()) {
                                        player.playSound(player.getLocation(),
                                                Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 100, 0.2F);
                                        player.sendTitle("Restarting now", "", 0, 1, 0);
                                    }
                                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
                                        Bukkit.spigot().restart();
                                    }, 20L);
                                }, 20L);
                            }, 20L);
                        }, 20L);
                    }, 1160L);
                }, 1200);
            }, 1200L);
        }, 2400L);
    }

}