package com.teunjojo;

import org.bukkit.command.*;
import org.bukkit.entity.Player;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommandMain implements TabExecutor {

    private SimpleAutoRestart plugin;
    private final RestartScheduler restartScheduler;
    private final Utility util = new Utility();
    private final MiniMessage mm = MiniMessage.miniMessage();

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE - HH:mm");

    public CommandMain(SimpleAutoRestart plugin) {
        this.plugin = plugin;
        this.restartScheduler = plugin.getRestartScheduler();
    }

    @Override
    public boolean onCommand(CommandSender _sender, Command command, String label, String[] args) {

        Audience sender = plugin.adventure().sender(_sender);

        if (args.length == 0) {
            return false;
        }

        switch (args[0]) {
            case "cancel":
                return commandCancelRestart(sender);
            case "resume":
                return commandResumeRestart(sender);
            case "status":
                return commandStatus(sender);
            case "set":
                if (!commandSetRestart(sender, args))
                    sender.sendMessage(mm.deserialize("Usage: /" + label + " set <hour> <minute> [day]"));

                return true;
            case "reload":
                return commandReload(sender);
            default:
                break;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            ArrayList<String> completions = new ArrayList<>();

            switch (args[0]) {
                case "cancel":
                    break;
                case "resume":
                    break;
                case "status":
                    break;
                case "set":
                    completions.addAll(tabCompleteDate(args, 1));
                    break;
                case "reload":
                    break;
                default:
                    completions.add("cancel");
                    completions.add("resume");
                    completions.add("status");
                    completions.add("set");
                    completions.add("reload");
                    break;
            }
            return completions;

        }
        return null;
    }

    private boolean commandCancelRestart(Audience sender) {
        ZonedDateTime restart = restartScheduler.getNextRestart();

        if (restartScheduler.isRestartCanceled(restart)) {
            sender.sendMessage(mm.deserialize("<red>Next auto restart is already canceled"));
            return true;
        }

        String formattedString = restart.format(formatter);

        restartScheduler.cancelRestart(restart);

        sender.sendMessage(mm.deserialize("Restart for " + formattedString + " is canceled"));
        return true;
    }

    private boolean commandResumeRestart(Audience sender) {
        ZonedDateTime restart = restartScheduler.getNextRestart();

        if (!restartScheduler.isRestartCanceled(restart)) {
            sender.sendMessage(mm.deserialize("<red>Next auto restart is not canceled"));
            return true;
        }

        String formattedString = restart.format(formatter);

        restartScheduler.resumeRestart(restart);

        sender.sendMessage(mm.deserialize("Restart for " + formattedString + " is resumed"));
        return true;
    }

    private boolean commandStatus(Audience sender) {

        Set<ZonedDateTime> allRestarts = new HashSet<>();
        allRestarts.addAll(restartScheduler.getScheduledRestarts());
        allRestarts.addAll(restartScheduler.getCanceledRestarts());

        if (allRestarts.isEmpty()) {
            sender.sendMessage(mm.deserialize("No restarts are scheduled."));
            return true;
        }

        // sort the restarts
        ArrayList<ZonedDateTime> sortedRestarts = new ArrayList<>(allRestarts);
        sortedRestarts.sort(ZonedDateTime::compareTo);

        boolean scheduledReached = false;

        sender.sendMessage(mm.deserialize("Scheduled Restarts:"));
        for (ZonedDateTime restart : sortedRestarts) {
            String formattedString = restart.format(formatter);
            if (restartScheduler.isRestartCanceled(restart)) {
                sender.sendMessage(mm.deserialize("<strikethrough>" + formattedString + " (Canceled)"));
            } else {
                if (scheduledReached) {
                    sender.sendMessage(mm.deserialize("<gray><italic>" + formattedString + " (Scheduled)"));
                    continue;
                }
                sender.sendMessage(mm.deserialize("<b>" + formattedString + " (Scheduled)"));
                scheduledReached = true;

            }
        }

        return true;
    }

    private boolean commandSetRestart(Audience sender, String[] args) {

        // Remove the first argument
        String[] restartArgs = Arrays.copyOfRange(args, 1, args.length);

        String _restartTime = buildRestartTime(sender, restartArgs);

        if (_restartTime == null) {
            return false;
        }

        if (restartScheduler.isRestartScheduled(_restartTime)) {
            sender.sendMessage(mm.deserialize("<red>A restart is already scheduled for this time!"));
            return false;
        }

        boolean success = restartScheduler.scheduleRestart(_restartTime, plugin.getMessages(), plugin.getTitles(),
                plugin.getSubtitles(), plugin.getCommands());

        if (!success) {
            sender.sendMessage(mm.deserialize("<red>Invalid time format!"));
            return false;
        }

        sender.sendMessage(mm.deserialize("Auto restart is scheduled at " + _restartTime));
        sender.sendMessage(mm.deserialize(
                "<yellow>Note that this scheduled time is not saved and will be reset on server restart."));

        return true;

    }

    private boolean commandReload(Audience sender) {
        plugin.reload();
        sender.sendMessage(mm.deserialize("Configuration reloaded successfully."));
        return true;
    }

    private ArrayList<String> tabCompleteDate(String[] args, int startIndex) {
        ArrayList<String> completions = new ArrayList<>();

        switch (args.length - startIndex) {
            case 1:
                for (int i = 0; i < 24; i++) {
                    completions.add(String.valueOf(i));
                }
                break;
            case 2:
                for (int i = 0; i < 60; i++) {
                    completions.add(String.valueOf(i));
                }
                break;
            case 3:
                completions.add("Daily");
                completions.add("Monday");
                completions.add("Tuesday");
                completions.add("Wednesday");
                completions.add("Thursday");
                completions.add("Friday");
                completions.add("Saturday");
                completions.add("Sunday");
                break;
            default:
                break;
        }
        return completions;
    }

    private String buildRestartTime(Audience sender, String[] args) {
        String day = "Daily"; // Default day
        int hour = -1;
        int minute = -1;

        if (args.length < 2) {
            sender.sendMessage(mm.deserialize("<red>Not enough arguments!"));
            return null;
        }

        if (args.length >= 3) {
            day = args[2];
        }

        try {
            hour = Integer.parseInt(args[0]);
            minute = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(mm.deserialize("<red>Hour and minute must be numbers!"));
            return null;
        }

        if (util.weekDayToInt(day) == -1) {
            sender.sendMessage(mm.deserialize(
                    "<red>Invalid day! Use Daily, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday or Sunday."));
            return null;
        }

        String _restartTime = day + ";" + String.format("%02d", hour) + ":" + String.format("%02d", minute);
        return _restartTime;
    }
}
