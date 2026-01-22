package com.cn.jk.powerwaveviewer.handle;


import com.cn.jk.powerwaveviewer.entity.WaveChartData;

@FunctionalInterface
public interface DataWaveHandler {
    void handle(WaveChartData paramWaveChartData, byte[] paramArrayOfByte);
}