package com.teunjojo;

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

    public boolean scheduleRestart(String _restartTime, Map<Long, String> _messages, Map<Long, String> _titles,
            Map<Long, String> _subtitles, List<String> _commands) {

        // If format follows old pattern HH:MM, correct it to Daily;HH:MM
        if (_restartTime.matches("^([01]\\d|2[0-3]):([0-5]\\d)$")) {
            _restartTime = "Daily;" + _restartTime;
        }

        String[] parts = _restartTime.split(";");
        String dayPart = parts[0];
        String timePart = parts[1];

        int hour = Integer.parseInt(timePart.split(":")[0]);
        int minute = Integer.parseInt(timePart.split(":")[1]);

        ZonedDateTime now = ZonedDateTime.now();

        int currentDayOfWeek = now.getDayOfWeek().getValue();
        int targetDayOfWeek = weekDayToInt(dayPart);

        if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
            return false;
        }

        if (targetDayOfWeek == -1) {
            return false;
        }

        ZonedDateTime nextRestart = now.withHour(hour).withMinute(minute).withSecond(0);

        int daysUntilTarget = 0;

        if (targetDayOfWeek == 0) { // Daily
            if (nextRestart.isBefore(now)) {
                daysUntilTarget = 1; // Schedule for the next day if the time has already passed today
            }
        } else { // Specific day
            daysUntilTarget = (targetDayOfWeek - currentDayOfWeek + 7) % 7;
            if (nextRestart.isBefore(now)) {
                daysUntilTarget = 7; // Schedule for next week if the time has already passed today
            }
        }

        nextRestart = nextRestart.plusDays(daysUntilTarget);

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
                if (!RestartScheduler.this.isRestartCanceled())
                    _commands.forEach(command -> {
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                        });
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

    public int weekDayToInt(String day) {
        switch (day) {
            case "Daily":
                return 0;
            case "Monday":
                return 1;
            case "Tuesday":
                return 2;
            case "Wednesday":
                return 3;
            case "Thursday":
                return 4;
            case "Friday":
                return 5;
            case "Saturday":
                return 6;
            case "Sunday":
                return 7;
            default:
                return -1;
        }
    }
}
