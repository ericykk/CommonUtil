package com.eric.util.date;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hpf on 11/13/17.
 */
public class TimeUtils {

    private static final Pattern HOUR_PATTERN = Pattern.compile("[1-9]{1,2}小时");

    private static final Pattern MIN_PATTERN = Pattern.compile("[1-9]{1,2}分");

    private static final Pattern SECOND_PATTERN = Pattern.compile("[1-9]{1,2}秒");

    public static String changeTimeGapInMsToStr(long gap) {
        StringBuilder sb = new StringBuilder();
        long diff = gap / 1000;
        if (diff >= 24 * 3600) {
            sb.append(diff / 24 / 3600);
            sb.append("天");
            diff = diff % (24 * 3600);
        }
        if (diff >= 3600 && diff < 24 * 3600) {
            sb.append(diff / 3600);
            sb.append("小时");
            diff = diff % 3600;
        }
        if (diff >= 60 && diff < 3600) {
            sb.append(diff / 60);
            sb.append("分钟");
            diff = diff % 60;
        }
        if (diff < 60 && diff >= 0) {
            sb.append(diff);
            sb.append("秒");
        }
        return sb.toString();
    }

    public static Long convertTimeStrToTimestamp(String timeStr) {
        Long timestamp = 0L;
        Matcher matcher = HOUR_PATTERN.matcher(timeStr);
        if (matcher.find()) {
            timestamp += Long.parseLong(matcher.group().split("小时")[0]) * 3600 * 1000;
        }
        matcher = MIN_PATTERN.matcher(timeStr);
        if (matcher.find()) {
            timestamp += Long.parseLong(matcher.group().split("分")[0]) * 60 * 1000;
        }
        matcher = SECOND_PATTERN.matcher(timeStr);
        if (matcher.find()) {
            timestamp += Long.parseLong(matcher.group().split("秒")[0]) * 1000;
        }
        return timestamp;
    }
}
