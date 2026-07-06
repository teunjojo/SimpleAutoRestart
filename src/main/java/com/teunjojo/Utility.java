package com.teunjojo;

import org.bukkit.Bukkit;

import net.kyori.adventure.text.minimessage.MiniMessage;

public class Utility {

    private static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static final MiniMessage mm = MiniMessage.miniMessage();

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

    public void runTask(Runnable task) {
        if (isFolia()) {
            Bukkit.getGlobalRegionScheduler().execute(SimpleAutoRestart.getInstance(), task);
        } else {
            Bukkit.getScheduler().runTask(SimpleAutoRestart.getInstance(), task);
        }
    }
}
