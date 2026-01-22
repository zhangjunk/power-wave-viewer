package com.cn.jk.powerwaveviewer.service;

import com.cn.jk.powerwaveviewer.entity.WaveChartData;

public interface PartialDischargeDataParser {
    void prpdWaveData(WaveChartData waveChartData, byte[] waveData);
    void prpsWaveData(WaveChartData waveChartData, byte[] waveData);
    void sybxWaveData(WaveChartData waveChartData, byte[] waveData);
}
