package io.github.toberocat.core.utility.calender;

import io.github.toberocat.MainIF;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class TimeCore {
    private static SimpleDateFormat userTimeFormat;
    private static SimpleDateFormat machineTimeFormat;
    private static String format;

    public static boolean init() {
        format = MainIF.getConfigManager().getValue("general.timeFormat");

        if (format.length() - format.replace("/", "").length() != 2) { // Checks for 3 / in string
            MainIF.getIF().SaveShutdown("&cCouldn't read &6general.timeFormat&c. Received &6"+format+"&c. The date must be seperated by two &6/&c. Like &6dd/MM/yyyy");
            return false;
        }

        String[] split = Arrays.stream(format.split("/")).filter(x -> !StringUtils.isBlank(x)).toArray(String[]::new);

        if (split.length < 3) {
            MainIF.getIF().SaveShutdown("&cCouldn't read &6general.timeFormat&c. Received &6"+format+"&c. You need to specify the day, the month and the year &6(dd/MM/yyyy)&c seperated by an &6/");
            return false;
        }

        List<String> lst = Arrays.asList(split);
        if (!lst.contains("dd") || !lst.contains("MM") || !lst.contains("yyyy")) {
            MainIF.getIF().SaveShutdown("&cCouldn't read &6general.timeFormat&c. Received &6"+format+"&c. The date needs to contain a &6dd&c, &6MM&c, &6yyyy&c seperated by an &6/");
            return false;
        }

        userTimeFormat = new SimpleDateFormat(format);
        machineTimeFormat = new SimpleDateFormat("dd/MM/yyyy");
        return true;
    }

    public static String dateToString(int[] date) {
        String result = "";
        for (int d : date) {
            result += d + " ";
        }
        return result.trim().replace(" ", "/");
    }

    public static int[] getUserDate() {
        String date = userTimeFormat.format(Calendar.getInstance().getTime());

        String[] dateSplit = date.split("/");

        return new int[] {
                Integer.parseInt(dateSplit[0]),
                Integer.parseInt(dateSplit[1]),
                Integer.parseInt(dateSplit[2])
        };
    }

    public static int[] userFormatToMachine(int[] userFormat) {
        String[] formatSplit = format.split("/");

        int dayIndex = ArrayUtils.indexOf(formatSplit, "dd");
        int monthIndex = ArrayUtils.indexOf(formatSplit, "MM");
        int yearIndex = ArrayUtils.indexOf(formatSplit, "yyyy");

        return new int[] { userFormat[dayIndex], userFormat[monthIndex], userFormat[yearIndex] };
    }

    public static int[] machineFormatToUser(int[] machineFormat) {
        String[] formatSplit = format.split("/");

        int dayIndex = ArrayUtils.indexOf(formatSplit, "dd");
        int monthIndex = ArrayUtils.indexOf(formatSplit, "MM");
        int yearIndex = ArrayUtils.indexOf(formatSplit, "yyyy");

        int[] date = new int[3];
        date[dayIndex] = machineFormat[0];
        date[monthIndex] = machineFormat[1];
        date[yearIndex] = machineFormat[2];

        return date;
    }

    public static int[] getMachineDate() {
        String date = machineTimeFormat.format(Calendar.getInstance().getTime());

        String[] dateSplit = date.split("/");

        return new int[] {Integer.parseInt(dateSplit[0]),
                Integer.parseInt(dateSplit[1]),
                Integer.parseInt(dateSplit[2])};
    }


    /**
     *
     * @param date The date should be machine format
     * @return
     */
    public static int[] dateStringToDate(String date) {
        if (isVaildMachineTime(date)) {
            String[] sDate = date.split("/");
            int[] i = new int[] {Integer.parseInt(sDate[0]), Integer.parseInt(sDate[1]), Integer.parseInt(sDate[2])};
            if (isVaildMachineTime(i)) {
                return i;
            }
        }
        return null;
    }

    public static int SecondsToMillis(int seconds) {
        return seconds * 1000;
    }

    public static int MinutesToMillis(int minutes) {
        return minutes * 60 * 1000;
    }

    public static int SecondsToTicks(int seconds) {
        return seconds * 20;
    }

    public static boolean isVaildMachineTime(String date) {
        if (date.split("/").length != 3) return false;
        return true;
    }
    public static boolean isVaildMachineTime(int[] date) {
        if (date.length != 3) return false;
        if (date[0] < 0 || date[0] > 31) return false;
        if (date[1] < 0 || date[1] > 12) return false;
        if (date[2] < 0) return false;
        return true;
    }

    public static int[] getDateDifference(int[] start, int[] end) {
        return new int[] {start[0] - end[0], start[1] - end[1], start[2] - end[2]};
    }

    public static int getDays(int[] date) {

        return date[0] + date[1] * 30 + date[2] * 360;
    }}
