package com.teunjojo;

import org.bukkit.Bukkit;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class RestartScheduler {
    private final SimpleAutoRestart plugin;
    private final Utility util = new Utility();
    private final MiniMessage mm = MiniMessage.miniMessage();
    private Set<ZonedDateTime> scheduledRestarts = new HashSet<ZonedDateTime>();
    private Set<ZonedDateTime> canceledRestarts = new HashSet<ZonedDateTime>();
    private Timer timer = new Timer(true);

    public RestartScheduler(SimpleAutoRestart plugin) {
        this.plugin = plugin;
    }

    public boolean scheduleRestart(String _restartTime, Map<Long, String> _messages, Map<Long, String> _titles,
            Map<Long, String> _subtitles, List<String> _commands) {

        ZonedDateTime nextRestart = parseRestartTime(_restartTime);
        ZonedDateTime now = ZonedDateTime.now();

        // Check if a restart is already scheduled for this time
        if (isRestartScheduled(nextRestart)) {
            plugin.getLogger().warning("A restart is already scheduled for: " + _restartTime);
            return false;
        }

        Duration duration = Duration.between(now, nextRestart);
        long initialDelayInSeconds = duration.getSeconds();

        // Schedule the restart messages
        for (Long delay : _messages.keySet()) {
            if (delay > initialDelayInSeconds)
                continue;
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (getNextRestart() == nextRestart && !isRestartCanceled(nextRestart)) {
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
                    if (getNextRestart() == nextRestart && !isRestartCanceled(nextRestart)) {
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
                if (getNextRestart() == nextRestart && !isRestartCanceled(nextRestart)) {
                    _commands.forEach(command -> {
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                        });
                    });
                }
                scheduledRestarts.remove(nextRestart);
                canceledRestarts.remove(nextRestart);
            }
        }, initialDelayInSeconds * 1000);

        // Store the scheduled restart time
        scheduledRestarts.add(nextRestart);
        canceledRestarts.remove(nextRestart);

        plugin.getLogger().info("Reboot set for: " + _restartTime);
        return true;
    }

    public boolean isRestartScheduled(ZonedDateTime dateTime) {
        return scheduledRestarts.contains(dateTime);
    }

    public boolean isRestartScheduled(String _restartTime) {
        ZonedDateTime restartTime = parseRestartTime(_restartTime);
        return isRestartScheduled(restartTime);
    }

    public void cancelRestart(ZonedDateTime dateTime) {
        scheduledRestarts.remove(dateTime);
        canceledRestarts.add(dateTime);
    }

    public void cancelRestart(String _restartTime) {
        ZonedDateTime restartTime = parseRestartTime(_restartTime);
        cancelRestart(restartTime);
    }

    public boolean isRestartCanceled(ZonedDateTime dateTime) {
        return canceledRestarts.contains(dateTime);
    }

    public boolean isRestartCanceled(String _restartTime) {
        ZonedDateTime restartTime = parseRestartTime(_restartTime);
        return isRestartCanceled(restartTime);
    }

    public void resumeRestart(ZonedDateTime dateTime) {
        canceledRestarts.remove(dateTime);
        scheduledRestarts.add(dateTime);
    }

    public void resumeRestart(String _restartTime) {
        ZonedDateTime restartTime = parseRestartTime(_restartTime);
        resumeRestart(restartTime);
    }

    public ZonedDateTime getNextRestart() {
        // Find the earliest scheduled restart
        ZonedDateTime nextRestart = null;
        ZonedDateTime nextScheduledRestart = null;
        for (ZonedDateTime scheduledRestart : scheduledRestarts) {
            if (nextScheduledRestart == null || scheduledRestart.isBefore(nextScheduledRestart)) {
                nextScheduledRestart = scheduledRestart;
            }
        }

        ZonedDateTime nextCanceledRestart = null;
        for (ZonedDateTime canceledRestart : canceledRestarts) {
            if (nextCanceledRestart == null || canceledRestart.isBefore(nextCanceledRestart)) {
                nextCanceledRestart = canceledRestart;
            }
        }

        if (nextScheduledRestart != null && (nextCanceledRestart == null
                || nextScheduledRestart.isBefore(nextCanceledRestart))) {
            nextRestart = nextScheduledRestart;
        } else {
            nextRestart = nextCanceledRestart;
        }

        return nextRestart;
    }

    public ZonedDateTime parseRestartTime(String _restartTime) {
        String[] parts = _restartTime.split(";");
        String dayPart = parts[0];
        String timePart = parts[1];

        int hour = Integer.parseInt(timePart.split(":")[0]);
        int minute = Integer.parseInt(timePart.split(":")[1]);

        ZonedDateTime now = ZonedDateTime.now();

        int currentDayOfWeek = now.getDayOfWeek().getValue();
        int targetDayOfWeek = util.weekDayToInt(dayPart);

        ZonedDateTime nextRestart = now.withHour(hour).withMinute(minute);

        // remove seconds and nanoseconds
        nextRestart = nextRestart.withSecond(0).withNano(0);

        int daysUntilTarget = 0;

        if (targetDayOfWeek == 0) { // Daily
            if (nextRestart.isBefore(now)) {
                daysUntilTarget = 1; // Schedule for the next day if the time has already passed today
            }
        } else { // Specific day
            daysUntilTarget = (targetDayOfWeek - currentDayOfWeek + 7) % 7;
            if (daysUntilTarget == 0 && nextRestart.isBefore(now)) {
                daysUntilTarget += 7; // Schedule for next week if the time has already passed today
            }
        }

        // Adjust the nextRestart date to the correct day
        nextRestart = nextRestart.plusDays(daysUntilTarget);

        return nextRestart;
    }

    public Set<ZonedDateTime> getScheduledRestarts() {
        return scheduledRestarts;
    }

    public Set<ZonedDateTime> getCanceledRestarts() {
        return canceledRestarts;
    }

    public void reset() {
        scheduledRestarts.clear();
        canceledRestarts.clear();
        // reset the timer
        timer.cancel();
        timer = new Timer();
    }
}