package com.hcmut.voltrent.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
    public static final SimpleDateFormat VNPAY_DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
    public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Logger log = LoggerFactory.getLogger(DateUtils.class);

    public static LocalDateTime convertToLocalDateTimeFormat(String time) {
        try {
            return LocalDateTime.parse(time, formatter);
        } catch (Exception e) {
            log.error("Error convert date str {} to {} format: {}", time, DATE_TIME_FORMAT, e.getMessage());
            return null;
        }
    }

    public static long toEpochMilli(LocalDateTime dateTime) {
        return dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    // Convert date string to LocalDateTime at start of day
    public static LocalDate convertToLocalDateFormatWithEx(String time) throws Exception {
        return LocalDate.parse(time, dateFormatter);
    }

    public static LocalDate convertToLocalDateFromDateTimeWithEx(String dateTimeStr) throws Exception {
        return LocalDateTime.parse(dateTimeStr, formatter).toLocalDate();
    }

    public static LocalDate convertToLocalDateFromDateTime(String dateTimeStr) {
        try {
            return LocalDateTime.parse(dateTimeStr, formatter).toLocalDate();
        } catch (Exception e) {
            log.error("Error convert date str {} to {} format: {}", dateTimeStr, DATE_FORMAT, e.getMessage());
            return null;
        }
    }

    public static LocalDate convertToLocalDateFormat(String time) {
        try {
            return LocalDate.parse(time);
        } catch (Exception e) {
            log.error("Error convert date str {} to {} format: {}", time, DATE_FORMAT, e.getMessage());
            return null;
        }
    }

    public static LocalDate convertToLocalDateFormat(LocalDateTime time) {
        try {
            return time.toLocalDate();
        } catch (Exception e) {
            log.error("Error convert date str {} to {} format: {}", time, DATE_FORMAT, e.getMessage());
            return null;
        }

    }
    public static boolean isDateBeforeNow(String date) throws Exception {
        LocalDate localDate = convertToLocalDateFromDateTime(date);
        return localDate.isBefore(LocalDate.now());
    }

    public static boolean isTimeRangeInvalid(String startTime, String endTime) throws Exception {
        LocalDate start = convertToLocalDateFromDateTime(startTime);
        LocalDate end = convertToLocalDateFromDateTime(endTime);
        return start.isAfter(end);
    }

    public static String convertDateFormat(String originalValue, String fromFormat, String toFormat) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat(fromFormat);
        Date date = sdf.parse(originalValue);
        sdf = new SimpleDateFormat(toFormat);
        return sdf.format(date);
    }

    public static String formatVnTime(Calendar calendar) {
        VNPAY_DATE_FORMAT.setTimeZone(calendar.getTimeZone()); //  use the VN timezone
        return VNPAY_DATE_FORMAT.format(calendar.getTime());
    }

}
