package com.teunjojo;

import java.util.*;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bstats.bukkit.Metrics;

public final class SimpleAutoRestart extends JavaPlugin {

    SimpleAutoRestart plugin = this;

    private final RestartScheduler restartScheduler = new RestartScheduler(plugin);

    private List<String> restartTimes = new ArrayList<>();
    private Map<Long, String> messages = new HashMap<>();
    private Map<Long, String> titles = new HashMap<>();
    private Map<Long, String> subtitles = new HashMap<>();
    private List<String> commands = new ArrayList<>();

    /**
     * Function executed when the plugin is enabling.
     */
    @Override
    public void onEnable() {

        // bStats metrics
        int pluginId = 17760;
        new Metrics(this, pluginId);

        // Check for updates
        new UpdateChecker(this).getVersion((version) -> {
            version = version.replaceFirst("v", "");
            if (!this.getDescription().getVersion().equals(version)) {
                getLogger().warning("A new version of SimpleAutoRestart is available: v" + version + " (Current version: v" + this.getDescription().getVersion() + ")");
            }
        });

        // Register SimpleAutoRestart commands
        this.getCommand("autorestart").setExecutor(new CommandMain(plugin));
        this.getCommand("simpleautorestart").setExecutor(new CommandMain(plugin));
        this.getCommand("sar").setExecutor(new CommandMain(plugin));

        // Load the configuration
        if (loadConfig() == null) {
            getLogger().warning("Failed to load the configuration file.");
            return;
        }

        // Schedule the restarts
        for (String restartTime : this.restartTimes) {
            if (!restartScheduler.scheduleRestart(restartTime, messages, titles, subtitles, commands)) {
                getLogger().severe("Failed to schedule the restart for: " + restartTime);
            }
        }
    }

    public FileConfiguration loadConfig () {
        this.saveDefaultConfig();
        FileConfiguration config = this.getConfig();
        Object restartTimeObject = config.get("restartTime");

        // Check if the restart time is a string or a list
        if (restartTimeObject instanceof String) {
            // Add the single restart time to the list
            this.restartTimes.add((String) restartTimeObject);
        } else if (restartTimeObject instanceof List) {
            // Save the list of restart times
            this.restartTimes = (List<String>) restartTimeObject;
        } else {
            // If unknown format, log a warning and set the restart times to an empty list
            getLogger().warning("Invalid format for 'restartTime' in the configuration file.");
            this.restartTimes = new ArrayList<>();
            return null;
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

        // Load the messages from the config
        if (config.getConfigurationSection("messages") != null) {
            for (String key : config.getConfigurationSection("messages").getKeys(false)) {
                this.messages.put(Long.parseLong(key), config.getString("messages." + key));
            }
        }
        // Load the titles from the config
        if (config.getConfigurationSection("titles") != null) {
            for (String key : config.getConfigurationSection("titles").getKeys(false)) {
                this.titles.put(Long.parseLong(key), config.getString("titles." + key));
            }
        }
        // Load the subtitles from the config
        if (config.getConfigurationSection("subtitles") != null) {
            for (String key : config.getConfigurationSection("subtitles").getKeys(false)) {
                this.subtitles.put(Long.parseLong(key), config.getString("subtitles." + key));
            }
        }

        // Load the commands from the config
        this.commands = config.getStringList("commands");
        if (this.commands.isEmpty()) {
            // If no stop commands are provided, add a default one
            config.set("commands", new ArrayList<String>() {{
                add("restart");
            }});
            saveConfig();
            reloadConfig();
            this.commands = config.getStringList("commands");
        }
        return config;
    }

    public RestartScheduler getRestartScheduler() {
        return restartScheduler;
    }

    public List<String> getRestartTimes() {
        return restartTimes;
    }

    public Map<Long, String> getMessages() {
        return messages;
    }

    public Map<Long, String> getTitles() {
        return titles;
    }

    public Map<Long, String> getSubtitles() {
        return subtitles;
    }

    public List<String> getCommands() {
        return commands;
    }
}