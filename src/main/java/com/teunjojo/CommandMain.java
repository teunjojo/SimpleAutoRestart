package com.teunjojo;

import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandMain implements TabExecutor {

    private SimpleAutoRestart plugin;
    private final RestartScheduler restartScheduler;

    public CommandMain(SimpleAutoRestart plugin) {
        this.plugin = plugin;
        this.restartScheduler = plugin.getRestartScheduler();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

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
                if (!commandSetRestart(sender, args)) {
                    sender.sendMessage("Usage: /" + label + " set <hour> <minute> [day]");
                }
                return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            ArrayList<String> completions = new ArrayList<>();

            switch (args.length) {
                case 1:
                    completions.add("cancel");
                    completions.add("resume");
                    completions.add("status");
                    completions.add("set");
                    break;
                case 2:
                    for (int i = 0; i < 24; i++) {
                        completions.add(String.valueOf(i));
                    }
                    break;
                case 3:
                    for (int i = 0; i < 60; i++) {
                        completions.add(String.valueOf(i));
                    }
                    break;
                case 4:
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
        return null;
    }

    private boolean commandCancelRestart(CommandSender sender) {
        if (restartScheduler.isRestartCanceled()) {
            sender.sendMessage("Next auto restart is already canceled");
            return true;
        }
        sender.sendMessage("Next auto restart is canceled");
        restartScheduler.setRestartCanceled(true);
        return true;
    }

    private boolean commandResumeRestart(CommandSender sender) {
        if (!restartScheduler.isRestartCanceled()) {
            sender.sendMessage("Next auto restart is already scheduled");
            return true;
        }
        sender.sendMessage("Next auto restart resumed");
        restartScheduler.setRestartCanceled(false);
        return true;
    }

    private boolean commandStatus(CommandSender sender) {
        if (restartScheduler.isRestartCanceled()) {
            sender.sendMessage("Next auto restart is canceled");
        } else {
            sender.sendMessage("Next auto restart is scheduled");
        }
        return true;
    }

    private boolean commandSetRestart(CommandSender sender, String[] args) {
        String day = "Daily";
        if (args.length == 4) {
            day = args[3];
        }
        if (args.length < 3) {
            return false;
        }

        int hour = Integer.parseInt(args[1]);
        int minute = Integer.parseInt(args[2]);

        if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
            return false;
        }

        String _restartTime = day + ";" + String.format("%02d", hour) + ":" + String.format("%02d", minute);

        sender.sendMessage("Auto restart is scheduled at " + _restartTime);
        restartScheduler.scheduleRestart(_restartTime, plugin.getMessages(), plugin.getTitles(),
                plugin.getSubtitles(), plugin.getCommands());
        restartScheduler.setRestartCanceled(false);
        return true;

    }
}
