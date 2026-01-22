package com.cn.jk.powerwaveviewer.entity;

import lombok.Data;

import java.util.List;

/**
 * .dat文件整体模型（对应规范附表5）
 */
@Data
public class UHFPDatFile {
    // 头部必备字段
    private int fileLength; // 文件总长度（含CRC32）
    private byte[] specVersion; // 规范版本号（4字节，X.X.X.X）
    private long fileCreateTime; // 文件生成时间（YYYYMMDDhhmmssfff）
    private String stationName; // 站点名称（UNICODE，118字节）
    private String stationCode; // 站点编码（ASCII，42字节）
    private Integer weather; // 天气（可选，1字节）
    private Float temperature; // 环境温度（可选，float）
    private Byte humidity; // 环境湿度（可选，int8）
    private String manufacturer; // 装置厂家（UNICODE，32字节）
    private String deviceModel; // 装置型号（UNICODE，32字节）
    private byte[] deviceVersion; // 装置版本号（可选，4字节）
    private String deviceSerial; // 装置序列号（ASCII，32字节）
    private float systemFrequency; // 系统频率（float，Hz）
    private short graphCount; // 图谱数量N

    // 头部预留字段（224字节，跳过不解析）
    // 图谱数据列表
    private List<GraphData> graphList;

    // 尾部字段
    private byte[] reservedTail; // 尾部预留（32字节）
    private int crc32; // CRC32校验码
}