package com.cn.jk.powerwaveviewer.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    // 纯日期格式
    public static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    // 带时分秒格式
    public static final DateTimeFormatter FORMAT_DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ========== 场景1：本月自然月 ==========
    /** 获取当月1号 00:00:00 */
    public static LocalDateTime getMonthStartDateTime() {
        return YearMonth.now().atDay(1).atStartOfDay();
    }

    /** 获取当月最后一天 23:59:59.999 */
    public static LocalDateTime getMonthEndDateTime() {
        return YearMonth.now().atEndOfMonth().atTime(23, 59, 59, 999_000_000);
    }

    /** 获取当月1号 字符串 yyyy-MM-dd */
    public static String getMonthStartDate() {
        return YearMonth.now().atDay(1).format(FORMAT_DATE);
    }

    // ========== 场景2：严格近30天 ==========
    /** 获取30天前此刻时间 */
    public static LocalDateTime get30DaysAgoDateTime() {
        return LocalDateTime.now().minusDays(30);
    }

    /** 获取30天前日期 字符串 yyyy-MM-dd */
    public static String get30DaysAgoDate() {
        return LocalDate.now().minusDays(30).format(FORMAT_DATE);
    }

    // ========== 通用 ==========
    /** 获取今天日期 字符串 yyyy-MM-dd */
    public static String getTodayDate() {
        return LocalDate.now().format(FORMAT_DATE);
    }

    /** 获取当前时间 字符串 yyyy-MM-dd HH:mm:ss */
    public static String getNowDateTime() {
        return LocalDateTime.now().format(FORMAT_DATETIME);
    }
}
