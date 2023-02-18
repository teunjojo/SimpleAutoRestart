package com.teunjojo;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bstats.bukkit.Metrics;

public final class SimpleAutoRestart extends JavaPlugin {
    public static long serverStartMillis;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.getConfig();
        FileConfiguration config = this.getConfig();
        saveConfig();

        String restartTime = (config.getString("restartTime"));

        String[] timef = restartTime.split(":");
        int hour = Integer.parseInt(timef[0]);
        int minute = Integer.parseInt(timef[1]);

        int pluginId = 17760;
        Metrics metrics = new Metrics(this, pluginId);

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime nextRestart = now.withHour(hour).withMinute(minute).withSecond(0);
        if (now.compareTo(nextRestart) > 0)
        nextRestart = nextRestart.plusDays(1);

        Duration duration = Duration.between(now, nextRestart);
        long initialDelay = duration.getSeconds();
        WarnReboot("Restarting in 5 minutes", (int) initialDelay - 5*60, false);
        WarnReboot("Restarting in 3 minutes", (int) initialDelay - 3*60, false);
        WarnReboot("Restarting in 2 minutes", (int) initialDelay - 2*60, false);
        WarnReboot("Restarting in 1 minute", (int) initialDelay - 1*60, false);
        WarnReboot("Restarting in 3 seconds", (int) initialDelay - 3, false);
        WarnReboot("Restarting in 2 seconds", (int) initialDelay - 2, false);
        WarnReboot("Restarting in 1 second", (int) initialDelay - 1, false);
        WarnReboot("Restarting now", (int) initialDelay, true);
    }

    @Override
    public void onDisable() {
    }

    public void WarnReboot(String message, int seconds, Boolean reboot) {
        Timer timer;
        timer = new Timer();
        timer.schedule(new WarnTask(message, reboot), seconds * 1000);
    }

    class WarnTask extends TimerTask {
        private final String message;
        private final Boolean reboot;

        WarnTask(String message, Boolean reboot) {
            this.message = message;
            this.reboot = reboot;
        }

        public void run() {
            Bukkit.broadcastMessage(message);
            if (reboot)
                Bukkit.spigot().restart();
        }
    }
}