package com.teunjojo;

public class Utility {
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
