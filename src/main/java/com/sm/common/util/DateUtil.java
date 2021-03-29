package com.sm.common.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class DateUtil {

    private static final String DATE_FORMAT_PATTERN = "yyyy/MM/dd";
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_PATTERN);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    public static Date addMonthInCurrentDate(int month){
        LocalDate localDate = LocalDate.now();
        localDate = localDate.plusMonths(month);
        Date date = Date.from(localDate.atStartOfDay( ZoneId.systemDefault()).toInstant());
        return date;
    }

    public static long getDateDifference(String date){
        LocalDate currentDate = LocalDate.now();
        return ChronoUnit.DAYS.between(currentDate, LocalDate.parse(date, formatter));
    }

    public static String formatDateInString(Date date){
        return DATE_FORMAT.format(date);
    }
}
