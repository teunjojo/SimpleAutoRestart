package com.teunjojo;

import jdk.tools.jlink.plugin.Plugin;
import org.bukkit.Bukkit;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class RestartScheduler {
    private final SimpleAutoRestart plugin;
    private boolean restartCanceled = false;

    public RestartScheduler(SimpleAutoRestart plugin) {
        this.plugin = plugin;
    }

    public boolean scheduleRestart(String _restartTime, Map<Long, String> _messages, Map<Long, String> _titles, Map<Long, String> _subtitles, List<String> _commands) {

        int hour = Integer.parseInt(_restartTime.split(":")[0]);
        int minute = Integer.parseInt(_restartTime.split(":")[1]);

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime nextRestart = now.withHour(hour).withMinute(minute).withSecond(0);
        if (now.compareTo(nextRestart) > 0)
            nextRestart = nextRestart.plusDays(1);

        Duration duration = Duration.between(now, nextRestart);
        long initialDelayInSeconds = duration.getSeconds();

        // Schedule the restart messages
        Timer timer = new Timer();
        for (Long delay : _messages.keySet()) {
            if (delay > initialDelayInSeconds)
                continue;
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!RestartScheduler.this.isRestartCanceled())
                        Bukkit.broadcastMessage(_messages.get(delay));
                }
            }, (initialDelayInSeconds - delay) * 1000);
        }

        // Schedule the restart title
        for (Long delay : _titles.keySet()) {
            if (delay > initialDelayInSeconds)
                continue;
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!RestartScheduler.this.isRestartCanceled())
                        Bukkit.getOnlinePlayers().forEach(player -> {
                            player.sendTitle(_titles.get(delay), null, 10, 70, 20);
                        });
                }
            }, (initialDelayInSeconds - delay) * 1000);
        }

        // Schedule the restart subtitle
        for (Long delay : _subtitles.keySet()) {
            if (delay > initialDelayInSeconds)
                continue;
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!RestartScheduler.this.isRestartCanceled())
                        Bukkit.getOnlinePlayers().forEach(player -> {
                            player.sendTitle(null, _subtitles.get(delay), 10, 70, 20);
                        });
                }
            }, (initialDelayInSeconds - delay) * 1000);
        }

        // Schedule the restart itself
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Bukkit.getScheduler().runTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        if (!RestartScheduler.this.isRestartCanceled())
                            _commands.forEach(command -> {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                            });
                    }
                });
            }
        }, initialDelayInSeconds * 1000);

        plugin.getLogger().info("Reboot set for: " + _restartTime);
        return true;
    }

    public boolean isRestartCanceled() {
        return restartCanceled;
    }

    public void setRestartCanceled(boolean restartCanceled) {
        this.restartCanceled = restartCanceled;
    }
}
