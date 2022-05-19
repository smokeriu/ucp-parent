package org.ssiu.ucp.util.base;


import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;


public class TimeUtil {

    private static final PeriodFormatter defaultPeriodFormatter;

    static {
        defaultPeriodFormatter = new PeriodFormatterBuilder()
                .appendDays().appendSuffix("day")
                .appendHours().appendSuffix("hour")
                .appendMinutes().appendSuffix("min")
                .appendSeconds().appendSuffix("sec")
                .toFormatter();
    }

    public static String now(String userFormat) {
        return DateTime.now(DateTimeZone.getDefault()).toString(userFormat);
    }

    public static String getBeforeTime(String userFormat, String strPeriod) {
        final DateTime now = DateTime.now(DateTimeZone.getDefault());
        final Period period = getPeriod(strPeriod);
        return now.minus(period).toString(userFormat);
    }

    public static String getBeforeTimeByGiven(String givenTime, String userFormat, String strPeriod) {
        final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(userFormat)
                .withZone(DateTimeZone.getDefault());
        final DateTime parse = DateTime.parse(givenTime, dateTimeFormatter);
        final Period period = getPeriod(strPeriod);
        return parse.minus(period).toString(dateTimeFormatter);
    }

    private static Period getPeriod(String strPeriod) {
        return defaultPeriodFormatter.parsePeriod(strPeriod.trim());
    }
}
