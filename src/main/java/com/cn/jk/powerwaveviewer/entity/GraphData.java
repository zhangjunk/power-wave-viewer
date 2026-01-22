package com.cn.jk.powerwaveviewer.entity;

import lombok.Data;

/**
 * 图谱数据模型（对应规范附表6/7，含PRPD/PRPS/峰值统计图）
 */
@Data
public class GraphData {
    // 公共必备字段（所有图谱通用）
    private int graphType; // 图谱类型编码（0x20=多图谱，0x21=PRPD，0x22=PRPS，0x23=峰值统计）
    private int graphDataLength; // 图谱总长度（从类型编码到数据结束）
    private long graphCreateTime; // 图谱生成时间（YYYYMMDDhhmmssfff）
    private int graphProperty; // 图谱性质（0x01=检测图谱，0x02=背景噪声）
    private String deviceName; // 被测设备名称（UNICODE，118字节）
    private String deviceCode; // 被测设备编码（ASCII，42字节）
    private Short channelFlag; // 检测通道标志（可选，int16）
    private int dataType; // 存储数据类型编码（0x06=float，对应附表3）
    private int amplitudeUnit; // 幅值单位编码（如0x03=dBmV，对应附表4）
    private float amplitudeMin; // 幅值下限（float）
    private float amplitudeMax; // 幅值上限（float）
    private Integer frequencyBand; // 频带（可选，1字节）
    private Float freqMin; // 下限频率（可选，float）
    private Float freqMax; // 上限频率（可选，float）
    private int phaseWindowCount; // 相位窗数m（必备）
    private byte[] dischargeProb; // 放电类型概率（8字节，0=正常，1=尖端放电...7=其它）
    private String backgroundFileName; // 背景文件名称（可选，ASCII，54字节）

    // 预留字段（跳过不解析）
    private int reservedLength; // 预留字段长度（根据图谱类型不同）

    // 图谱具体数据（PRPD/PRPS/峰值统计）
    private Object graphData; // 实际数据（二维数组：PRPD=d[m][n]，PRPS=d[p][m]，峰值统计=d[2*m]）

    // PRPD特有字段
    private int quantAmplitudeCount; // 量化幅值n（PRPD必备，PRPS=0）

    // PRPS特有字段
    private int powerCycleCount; // 工频周期数p（PRPS必备，PRPD=0）

    // 峰值统计图特有字段（无额外字段，数据为2*m长度数组）
}