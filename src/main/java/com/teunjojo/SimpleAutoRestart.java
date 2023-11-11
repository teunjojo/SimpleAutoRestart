package com.teunjojo;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
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

        int pluginId = 17760;
        Metrics metrics = new Metrics(this, pluginId);

        List<String> restartTimes;
        Object restartTimeObject = config.get("restartTime");

        if (restartTimeObject instanceof String) {
            // Handle single string case
            restartTimes = new ArrayList<>();
            restartTimes.add((String) restartTimeObject);
        } else if (restartTimeObject instanceof List) {
            // Handle list case
            restartTimes = (List<String>) restartTimeObject;
        } else {
            // Handle unexpected type or null
            getLogger().warning("Invalid format for 'restartTime' in the configuration file.");
            // Provide a default value or handle the situation accordingly
            restartTimes = new ArrayList<>();
        }

        for (String restartTime : restartTimes) {
            String[] timef = restartTime.split(":");
            int hour = Integer.parseInt(timef[0]);
            int minute = Integer.parseInt(timef[1]);

            ZonedDateTime now = ZonedDateTime.now();
            ZonedDateTime nextRestart = now.withHour(hour).withMinute(minute).withSecond(0);
            if (now.compareTo(nextRestart) > 0)
                nextRestart = nextRestart.plusDays(1);

            Duration duration = Duration.between(now, nextRestart);
            long initialDelay = duration.getSeconds();
            setSchedule((int) initialDelay);
            getLogger().info("Reboot set for: " + restartTime);
        }
    }

    @Override
    public void onDisable() {
    }

    public void setSchedule(int seconds) {
        Timer timer = new Timer();
        long in5Minutes = Math.max(0, (seconds - 5 * 60) * 1000);
        long in3Minutes = Math.max(0, (seconds - 3 * 60) * 1000);
        long in2Minutes = Math.max(0, (seconds - 2 * 60) * 1000);
        long in1Minute = Math.max(0, (seconds - 1 * 60) * 1000);
        long in3Second = Math.max(0, (seconds - 3) * 1000);
        long in2Second = Math.max(0, (seconds - 2) * 1000);
        long in1Second = Math.max(0, (seconds - 1) * 1000);

        timer.schedule(new WarnTask("Restarting in 5 minutes", false), in5Minutes);
        timer.schedule(new WarnTask("Restarting in 3 minutes", false), in3Minutes);
        timer.schedule(new WarnTask("Restarting in 2 minutes", false), in2Minutes);
        timer.schedule(new WarnTask("Restarting in 1 minute", false), in1Minute);
        timer.schedule(new WarnTask("Restarting in 3 seconds", false), in3Second);
        timer.schedule(new WarnTask("Restarting in 2 seconds", false), in2Second);
        timer.schedule(new WarnTask("Restarting in 1 second", false), in1Second);

        timer.schedule(new WarnTask("Restarting now", true), seconds * 1000);
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