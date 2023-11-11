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

        Message msg = manageMessage();

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
            setSchedule((int) initialDelay, msg);
            getLogger().info("Reboot set for: " + restartTime);
        }
    }

    /**
     * Manage the messages.
     * @return the message object that contains all the configured messages.
     */
    private Message manageMessage() {
        String min5 = getConfig().getString("messages.5min");
        if(min5 == null) min5 = "Restarting in 5 minutes";
        String min3 = getConfig().getString("messages.3min");
        if(min3 == null) min3 = "Restarting in 3 minutes";
        String min2 = getConfig().getString("messages.2min");
        if(min2 == null) min2 = "Restarting in 2 minutes";
        String min1 = getConfig().getString("messages.1min");
        if(min1 == null) min1 = "Restarting in 1 minute";
        String sec3 = getConfig().getString("messages.3sec");
        if(sec3 == null) sec3 = "Restarting in 3 seconds";
        String sec2 = getConfig().getString("messages.2sec");
        if(sec2 == null) sec2 = "Restarting in 2 seconds";
        String sec1 = getConfig().getString("messages.1sec");
        if(sec1 == null) sec1 = "Restarting in 1 second";
        String now = getConfig().getString("messages.now");
        if(now == null) now = "Restarting now";

        return new Message(min5, min3, min2, min1, sec3, sec2, sec1, now);
    }

    /**
     * Message class.
     * <p>
     *      This class is used to store the messages broadcast.
     */
    static class Message {

        private final String min_5;
        private final String min_3;
        private final String min_2;
        private final String min_1;
        private final String sec_3;
        private final String sec_2;
        private final String sec_1;
        private final String now;

        public String getMin_5() {
            return min_5;
        }
        public String getMin_3() {
            return min_3;
        }
        public String getMin_2() {
            return min_2;
        }
        public String getMin_1() {
            return min_1;
        }
        public String getSec_3() {
            return sec_3;
        }
        public String getSec_2() {
            return sec_2;
        }
        public String getSec_1() {
            return sec_1;
        }
        public String getNow() {
            return now;
        }

        /**
         * Constructor.
         * @param min5 the message for 5 minutes before the reboot.
         * @param min3 the message for 3 minutes before the reboot.
         * @param min2 the message for 2 minutes before the reboot.
         * @param min1 the message for 1 minute before the reboot.
         * @param sec3 the message for 3 seconds before the reboot.
         * @param sec2 the message for 2 seconds before the reboot.
         * @param sec1 the message for 1 second before the reboot.
         * @param now the message for the reboot.
         */
        public Message(final String min5, final String min3, final String min2, final String min1, final String sec3, final String sec2, final String sec1, final String now) {
            this.min_5 = min5;
            this.min_3 = min3;
            this.min_2 = min2;
            this.min_1 = min1;
            this.sec_3 = sec3;
            this.sec_2 = sec2;
            this.sec_1 = sec1;
            this.now = now;
        }
    }

    /**
     * Set the schedule for the reboot.
     * @param seconds the amount seconds before the reboot.
     * @param msg the message object that contains all the configured messages.
     */
    public void setSchedule(int seconds, final Message msg) {
        Timer timer = new Timer();
        long in5Minutes = Math.max(0, (seconds - 5 * 60) * 1000);
        long in3Minutes = Math.max(0, (seconds - 3 * 60) * 1000);
        long in2Minutes = Math.max(0, (seconds - 2 * 60) * 1000);
        long in1Minute = Math.max(0, (seconds - 1 * 60) * 1000);
        long in3Second = Math.max(0, (seconds - 3) * 1000);
        long in2Second = Math.max(0, (seconds - 2) * 1000);
        long in1Second = Math.max(0, (seconds - 1) * 1000);

        timer.schedule(new WarnTask(msg.getMin_5(), false), in5Minutes);
        timer.schedule(new WarnTask(msg.getMin_3(), false), in3Minutes);
        timer.schedule(new WarnTask(msg.getMin_2(), false), in2Minutes);
        timer.schedule(new WarnTask(msg.getMin_1(), false), in1Minute);
        timer.schedule(new WarnTask(msg.getSec_3(), false), in3Second);
        timer.schedule(new WarnTask(msg.getSec_2(), false), in2Second);
        timer.schedule(new WarnTask(msg.getSec_1(), false), in1Second);

        timer.schedule(new WarnTask(msg.getNow(), true), seconds * 1000L);
    }

    /**
     * WarnTask class.
     * <p>
     *     This class is used to broadcast the messages and reboot if the parameter reboot of the constructor is true.
     */
    static class WarnTask extends TimerTask {
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