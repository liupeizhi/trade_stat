package com.doorway.tradememo.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * Note
 * Author:liupz
 * Date:2022/4/3
 */
public class DateFormatter {

    public static String NORMAL_FORMATTER = "yyyy-MM-dd HH:mm:ss";

    /**
     * 标准 yyyy-MM-dd HH:mm:ss
     * @param date
     * @return
     */
    public static String dateFormatter(Date date){
        return dateFormatter(date,NORMAL_FORMATTER);
    }

    public static String dateFormatter(Date date,String formatter){
        return dateToLocalDateTime(date).format(DateTimeFormatter.ofPattern(formatter));
    }

    /**
     * LocalDate转date
     *
     * @param localDate
     * @return
     */
    public static Date localDateToDate(LocalDate localDate){
        ZoneId zoneId = ZoneId.systemDefault();
        Date result = localDateToDate(localDate,zoneId);
        return result;
    }

    /**
     * LocalDate转date
     *
     * @param localDate
     * @param zoneId 时区
     * @return
     */
    public static Date localDateToDate(LocalDate localDate, ZoneId zoneId){
        Date result = Date.from(localDate.atStartOfDay().atZone(zoneId).toInstant());
        return result;
    }

    /**
     * LocalDateTime转Date
     *
     * @param localDateTime
     * @return
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime){
        ZoneId zoneId = ZoneId.systemDefault();
        Date result = localDateTimeToDate(localDateTime,zoneId);
        return result;
    }

    /**
     * LocalDateTime转Date
     *
     * @param localDateTime
     * @param zoneId 时区
     * @return
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime,ZoneId zoneId){
        Date result = Date.from(localDateTime.atZone(zoneId).toInstant());
        return result;
    }

    /**
     * Date转LocalDate
     *
     * @param date
     * @return
     */
    public static LocalDate dateToLocalDate(Date date){
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = dateToLocalDateTime(date,zoneId);
        return localDateTime.toLocalDate();
    }

    /**
     * Date转LocalDate
     *
     * @param date
     * @param zoneId 时区
     * @return
     */
    public static LocalDate dateToLocalDate(Date date,ZoneId zoneId){
        LocalDateTime localDateTime = dateToLocalDateTime(date,zoneId);
        return localDateTime.toLocalDate();
    }

    /**
     * Date转LocalDateTime
     *
     * @param date
     * @return
     */
    public static LocalDateTime dateToLocalDateTime(Date date){
        ZoneId zoneId = ZoneId.systemDefault();
        Instant instant = date.toInstant();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant,zoneId);
        return localDateTime;
    }

    /**
     * Date转LocalDateTime
     *
     * @param date
     * @return
     */
    public static LocalDateTime dateToLocalDateTime(Date date, ZoneId zoneId){
        Instant instant = date.toInstant();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant,zoneId);
        return localDateTime;
    }

    /**
     * String转LocalDate
     *
     * @param date
     * @return
     */
    public static LocalDate strToLocalDate(String date){
        DateTimeFormatter formatter1 = DateTimeFormatter.BASIC_ISO_DATE;
        LocalDate localDate = LocalDate.parse(date,formatter1);
        return localDate;
    }

    /**
     * String转LocalDateTime
     *
     * @param date
     * @return
     */
    public static LocalDateTime strToLocalDateTime(String date){
        DateTimeFormatter formatter1 = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime localDateTime = LocalDateTime.parse(date,formatter1);
        return localDateTime;
    }

    /**
     * String转LocalDate
     *
     * @param date
     * @param formatter
     * @return
     */
    public static LocalDate strToLocalDate(String date, String formatter){
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern(formatter);
        LocalDate localDate = LocalDate.parse(date,formatter1);
        return localDate;
    }

    /**
     * String转LocalDateTime
     *
     * @param date
     * @param formatter
     * @return
     */
    public static LocalDateTime strToLocalDateTime(String date, String formatter){
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern(formatter);
        LocalDateTime localDateTime = LocalDateTime.parse(date,formatter1);
        return localDateTime;
    }

    public static Date strToDate(String date, String formatter){
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern(formatter);
        LocalDateTime localDateTime = LocalDateTime.parse(date,formatter1);
        return localDateTimeToDate(localDateTime);
    }

    // -------------------- 两个日期相差的时间 ----------------------------------//

    public static long diffSeconds(LocalDateTime start,LocalDateTime end){
        return ChronoUnit.SECONDS.between(start,end);
    }

    public static long diffMinutes(LocalDateTime start,LocalDateTime end){
        return ChronoUnit.MINUTES.between(start,end);
    }

    public static long diffHours(LocalDateTime start,LocalDateTime end){
        return ChronoUnit.HOURS.between(start,end);
    }

    public static long diffDays(LocalDateTime start,LocalDateTime end){
        return ChronoUnit.DAYS.between(start,end);
    }

    public static long diffMonths(LocalDateTime start,LocalDateTime end){
        return ChronoUnit.MONTHS.between(start,end);
    }

    public static long diffYears(LocalDateTime start,LocalDateTime end){
        return ChronoUnit.YEARS.between(start,end);
    }

    // -------------------- 日期加上指定时间 ----------------------------------//
    public static Date plusDays(LocalDateTime dateTime,int days){
        return plusDays(dateTime,days,ZoneId.systemDefault());
    }

    public static LocalDateTime plusDays(Date date,int days){
        LocalDateTime dateTime = dateToLocalDateTime(date);
        Date temp = plusDays(dateTime,days,ZoneId.systemDefault());
        return dateToLocalDateTime(temp);
    }

    public static Date plusDays(LocalDateTime dateTime,int days,ZoneId zoneId){
        LocalDateTime temp = dateTime.plusDays(days);
        return localDateTimeToDate(temp,zoneId);
    }

    public static Date plusMonths(LocalDateTime dateTime,int months){
        return plusMonths(dateTime,months,ZoneId.systemDefault());
    }

    public static Date plusMonths(LocalDateTime dateTime,long months,ZoneId zoneId){
        LocalDateTime temp = dateTime.plusMonths(months);
        return localDateTimeToDate(temp,zoneId);
    }

    public static Date plusYears(LocalDateTime dateTime,long years){
        return plusYears(dateTime,years,ZoneId.systemDefault());
    }

    public static Date plusYears(LocalDateTime dateTime,long years,ZoneId zoneId){
        LocalDateTime temp = dateTime.plusYears(years);
        return localDateTimeToDate(temp,zoneId);
    }

    public static Date plusSeconds(LocalDateTime dateTime,long seconds){
        return plusSeconds(dateTime,seconds,ZoneId.systemDefault());
    }

    public static Date plusSeconds(LocalDateTime dateTime,long seconds,ZoneId zoneId){
        LocalDateTime temp = dateTime.plusSeconds(seconds);
        return localDateTimeToDate(temp,zoneId);
    }

    public static Date plusMinutes(LocalDateTime dateTime,long minutes){
        return plusMinutes(dateTime,minutes,ZoneId.systemDefault());
    }

    public static Date plusMinutes(Date date,long minutes){
        LocalDateTime dateTime = dateToLocalDateTime(date);
        return plusMinutes(dateTime,minutes,ZoneId.systemDefault());
    }

    public static Date plusMinutes(LocalDateTime dateTime,long minutes,ZoneId zoneId){
        LocalDateTime temp = dateTime.plusMinutes(minutes);
        return localDateTimeToDate(temp,zoneId);
    }

    public static Date plusHours(LocalDateTime dateTime,long hours){
        return plusHours(dateTime,hours,ZoneId.systemDefault());
    }

    public static Date plusHours(LocalDateTime dateTime,long hours,ZoneId zoneId){
        LocalDateTime temp = dateTime.plusHours(hours);
        return localDateTimeToDate(temp,zoneId);
    }

    // -------------------- 日期减去指定时间 ----------------------------------//
    public static Date minusSeconds(LocalDateTime dateTime,long seconds){
        return minusSeconds(dateTime,seconds,ZoneId.systemDefault());
    }

    public static Date minusSeconds(LocalDateTime dateTime,long seconds,ZoneId zoneId){
        LocalDateTime temp = dateTime.minusSeconds(seconds);
        return localDateTimeToDate(temp,zoneId);
    }

    public static Date minusMinutes(LocalDateTime dateTime,long minutes){
        return minusMinutes(dateTime,minutes,ZoneId.systemDefault());
    }

    public static Date minusMinutes(LocalDateTime dateTime,long minutes,ZoneId zoneId){
        LocalDateTime temp = dateTime.minusMinutes(minutes);
        return localDateTimeToDate(temp,zoneId);
    }

    public static Date minusHours(LocalDateTime dateTime,long hours){
        return minusHours(dateTime,hours, ZoneId.systemDefault());
    }

    public static Date minusHours(LocalDateTime dateTime, long hours, ZoneId zoneId){
        LocalDateTime temp = dateTime.minusHours(hours);
        return localDateTimeToDate(temp,zoneId);
    }

    public static Date minusDays(Date date,int days){
        LocalDateTime dateTime = dateToLocalDateTime(date);
        return minusDays(dateTime,days);
    }

    public static Date minusDays(LocalDateTime dateTime,long days){
        return minusDays(dateTime,days,ZoneId.systemDefault());
    }

    public static Date minusDays(LocalDateTime dateTime,long days,ZoneId zoneId){
        LocalDateTime temp = dateTime.minusDays(days);
        return localDateTimeToDate(temp,zoneId);
    }

    public static Date minusMonths(LocalDateTime dateTime,long months){
        return minusMonths(dateTime,months,ZoneId.systemDefault());
    }

    public static Date minusMonths(LocalDateTime dateTime,long months,ZoneId zoneId){
        LocalDateTime temp = dateTime.minusMonths(months);
        return localDateTimeToDate(temp,zoneId);
    }

    public static Date minusYears(LocalDateTime dateTime,long years){
        return minusYears(dateTime,years,ZoneId.systemDefault());
    }

    public static Date minusYears(LocalDateTime dateTime,long years,ZoneId zoneId){
        LocalDateTime temp = dateTime.minusYears(years);
        return localDateTimeToDate(temp,zoneId);
    }
}
