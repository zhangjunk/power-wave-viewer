package com.cn.jk.powerwaveviewer.utils;

import com.cn.jk.powerwaveviewer.config.FtpConfig;
import com.cn.jk.powerwaveviewer.config.StationMappingConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Component
public class CommonUtils {

    // 1. 文件中提取的时间戳格式：年月日时分秒毫秒
// 1. 替换格式化器定义（删掉原来的FILE_TIME_FORMATTER）
    private static final String FILE_TIME_PATTERN = "yyyyMMddHHmmssSSS";
    // 2. 前端传入的日期格式：年月日
    private static final String SEARCH_DATE_PATTERN = "yyyy-MM-dd";


    @Autowired
    private FtpConfig ftpConfig;

    @Autowired
    private StationMappingConfig stationMappingConfig;

    public String getFullPath(String station){
        // 获取基础路径和站点值
        String basePath = ftpConfig.getDownloadDict();
        System.out.println("✅ 自动加载dat文件位置：" + basePath);
        String stationValue = stationMappingConfig.getValueByKey(station);

        // 校验空值（避免拼接出无效路径）
        if (basePath == null || basePath.isEmpty()) {
            return "基础路径不能为空！";
        }
        if ("未知映射".equals(stationValue)) {
            return "站点[" + station + "]未配置映射值！";
        }
        // 拼接完整路径（推荐用Paths）
        String fullPath = Paths.get(basePath, stationValue).toString();
        return fullPath;
    }

    /**
     * ✅ 对外核心方法：判断【文件的时间】是否在【前端传入的日期范围】内
     * @param fileName 文件名/文件完整路径 如：MerkPDEC3000_CH-2-4_20251225140853631.dat
     * @param frontStartDate 前端开始日期 如：2025-12-01
     * @param frontEndDate 前端结束日期 如：2025-12-31
     * @return true=在范围内，false=不在范围内/参数异常
     */
    public static boolean isFileTimeInDateRange(String fileName, String frontStartDate, String frontEndDate) {
        // 1. 提取文件中的时间戳字符串
        String fileTimeStr = extractTimeStampFromFileName(fileName);
        if (fileTimeStr.isEmpty() || fileTimeStr.length()!=17 ) {
            return false;
        }
        boolean tag = false;
        try{
            // 2. 把【文件时间戳】转成 LocalDateTime 对象
            LocalDateTime fileLocalDateTime = parseFileTime(fileTimeStr);

            // 3. 把【前端开始日期】转成 当天00:00:00.000
            LocalDateTime startDateTime = parseSearchTime(frontStartDate);
            // 4. 把【前端结束日期】转成 当天23:59:59.999
            LocalDateTime endDateTime = parseSearchTime(frontEndDate)
                    .plusHours(23).plusMinutes(59).plusSeconds(59).plusNanos(999_000_000);

            // 5. ✔核心判断：文件时间 是否 >= 开始时间 且 <= 结束时间 【闭区间】
            tag = fileLocalDateTime.isAfter(startDateTime.minusNanos(1))
                    && fileLocalDateTime.isBefore(endDateTime.plusNanos(1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tag;
    }

    /**
     * ✅ 健壮版：从文件名中提取时间戳字符串（复用最优方案，无正则开销）
     */
    public static String extractTimeStampFromFileName(String fileName) {
        // 空值校验
        if (fileName == null || fileName.trim().isEmpty()) {
            return "";
        }
        // 处理完整路径，获取纯文件名
        String pureFileName = new File(fileName).getName();
        // 获取最后一个下划线和最后一个点的下标
        int lastUnderlineIndex = pureFileName.lastIndexOf("_");
        int lastDotIndex = pureFileName.lastIndexOf(".");
        // 下标合法性校验：必须有下划线、有点、且下划线在点前面
        if (lastUnderlineIndex == -1 || lastDotIndex == -1 || lastUnderlineIndex >= lastDotIndex) {
            return "";
        }
        // 截取时间戳
        return pureFileName.substring(lastUnderlineIndex + 1, lastDotIndex);
    }

    public static LocalDateTime parseFileTime(String fileTimeStr) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat(FILE_TIME_PATTERN); // 方法内创建，线程安全
        Date date = sdf.parse(fileTimeStr);
        // Date 转 LocalDateTime（兼容新旧API）
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
    public static LocalDateTime parseSearchTime(String searchTimeStr) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat(SEARCH_DATE_PATTERN); // 方法内创建，线程安全
        Date date = sdf.parse(searchTimeStr);
        // Date 转 LocalDateTime（兼容新旧API）
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
