package com.teunjojo;

import org.bukkit.Bukkit;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class RestartScheduler {
    private final SimpleAutoRestart plugin;
    private boolean restartCanceled = false;
    private final Utility util = new Utility();
    private final MiniMessage mm = MiniMessage.miniMessage();

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
        int targetDayOfWeek = util.weekDayToInt(dayPart);

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
                    if (!RestartScheduler.this.isRestartCanceled()) {
                        Audience adventureAudience = plugin.adventure().all();

                        String messageRaw = _messages.get(delay);

                        // Parse the message using MiniMessage and convert legacy formatting
                        Component messageLegacy = LegacyComponentSerializer.legacyAmpersand()
                                .deserialize(messageRaw.replace('§', '&'));

                        String serializedMessage = mm.serialize(messageLegacy).replace("\\", "");

                        Component messageFinal = mm.deserialize(serializedMessage);

                        adventureAudience.sendMessage(messageFinal);
                    }
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
                    if (!RestartScheduler.this.isRestartCanceled()) {
                        Audience adventurePlayers = plugin.adventure().players();

                        String titleRaw = _titles.get(delay);
                        String subtitleRaw = _subtitles.get(delay);

                        // Parse the message using MiniMessage and convert legacy formatting
                        Component titleLegacy = LegacyComponentSerializer.legacyAmpersand()
                                .deserialize(titleRaw.replace('§', '&'));
                        Component subtitleLegacy = LegacyComponentSerializer.legacyAmpersand()
                                .deserialize(subtitleRaw.replace('§', '&'));

                        String serializedTitle = mm.serialize(titleLegacy).replace("\\", "");
                        String serializedSubtitle = mm.serialize(subtitleLegacy).replace("\\", "");

                        Component titleFinal = mm.deserialize(serializedTitle);
                        Component subtitleFinal = mm.deserialize(serializedSubtitle);

                        Title title = Title.title(titleFinal, subtitleFinal);

                        adventurePlayers.showTitle(title);
                    }
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
}
