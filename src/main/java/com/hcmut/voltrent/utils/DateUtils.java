package com.hcmut.voltrent.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
    public static final SimpleDateFormat VNPAY_DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");

    public static String convertToLocalDateTimeFormat(String time) throws Exception{
        LocalDateTime localDateTime = LocalDateTime.parse(time, formatter);
        return localDateTime.format(formatter);
    }

    public static String convertDateFormat(String originalValue, String fromFormat, String toFormat) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat(fromFormat);
        Date date = sdf.parse(originalValue);
        sdf = new SimpleDateFormat(toFormat);
        return sdf.format(date);
    }

    public static String formatVnTime(Calendar calendar) {
        return VNPAY_DATE_FORMAT.format(calendar.getTime());
    }

}
