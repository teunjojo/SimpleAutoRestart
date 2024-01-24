package com.teunjojo;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bstats.bukkit.Metrics;

public final class SimpleAutoRestart extends JavaPlugin {

    /**
     * Function executed when the plugin is enabling..
     */
    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.getConfig();
        FileConfiguration config = this.getConfig();

        int pluginId = 17760;
        // bStats metrics
        new Metrics(this, pluginId);

        List<String> restartTimes;
        Object restartTimeObject = config.get("restartTime");

        // Check if the restart time is a string or a list
        if (restartTimeObject instanceof String) {
            // Handle a single string case
            restartTimes = new ArrayList<>();
            restartTimes.add((String) restartTimeObject);
        } else if (restartTimeObject instanceof List) {
            // Handle a list case
            restartTimes = (List<String>) restartTimeObject;
        } else {
            // Handle an unexpected type or null
            getLogger().warning("Invalid format for 'restartTime' in the configuration file.");
            // Provide a default value or handle the situation accordingly
            restartTimes = new ArrayList<>();
        }

        // Check if the messages are in the old format
        if (config.getString("messages.now") != null) {
            // Convert the old format to the new format
            config.set("messages.0", config.getString("messages.now"));
            config.set("messages.1", config.getString("messages.1sec"));
            config.set("messages.2", config.getString("messages.2sec"));
            config.set("messages.3", config.getString("messages.3sec"));
            config.set("messages.60", config.getString("messages.1min"));
            config.set("messages.120", config.getString("messages.2min"));
            config.set("messages.180", config.getString("messages.3min"));
            config.set("messages.300", config.getString("messages.5min"));
            // Remove the old format
            config.set("messages.now", null);
            config.set("messages.1sec", null);
            config.set("messages.2sec", null);
            config.set("messages.3sec", null);
            config.set("messages.1min", null);
            config.set("messages.2min", null);
            config.set("messages.3min", null);
            config.set("messages.5min", null);
            // Save and reload the config
            saveConfig();
            reloadConfig();
        }

        Map<Long, String> messages = new HashMap<>();
        for (String key : config.getConfigurationSection("messages").getKeys(false)) {
            messages.put(Long.parseLong(key), config.getString("messages." + key));
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
            long initialDelayInSeconds = duration.getSeconds();

            // Schedule the restart messages
            Timer timer = new Timer();
            for (Long delay : messages.keySet()) {
                if (delay > initialDelayInSeconds)
                    continue;
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Bukkit.broadcastMessage(messages.get(delay));
                    }
                }, (initialDelayInSeconds - delay) * 1000);
            }

            // Schedule the restart itself
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Bukkit.spigot().restart();
                }
            }, initialDelayInSeconds * 1000);

            getLogger().info("Reboot set for: " + restartTime);
        }
    }

}