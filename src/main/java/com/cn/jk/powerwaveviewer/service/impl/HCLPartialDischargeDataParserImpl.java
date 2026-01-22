package com.cn.jk.powerwaveviewer.service.impl;

import com.cn.jk.powerwaveviewer.entity.DataWaveNewVO;
import com.cn.jk.powerwaveviewer.entity.WaveChartData;
import com.cn.jk.powerwaveviewer.service.PartialDischargeDataParser;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HCLPartialDischargeDataParserImpl implements PartialDischargeDataParser {
    public void prpdWaveData(WaveChartData waveChartData, byte[] waveData) {
        Map<String, DataWaveNewVO> countMap = new HashMap<>();
        List<Object> chartData = new ArrayList();
        float max = 0.0F;
        float min = 0.0F;
        List<Float> maxList = new ArrayList<>();
        for (int i = 0; i < waveData.length / 8; i++) {
            byte[] tmp1 = new byte[4];
            tmp1[0] = waveData[i * 8];
            tmp1[1] = waveData[i * 8 + 1];
            tmp1[2] = waveData[i * 8 + 2];
            tmp1[3] = waveData[i * 8 + 3];
            float y11 = bytes2Float(tmp1);

            byte[] tmp2 = new byte[4];
            tmp2[0] = waveData[i * 8 + 4];
            tmp2[1] = waveData[i * 8 + 5];
            tmp2[2] = waveData[i * 8 + 6];
            tmp2[3] = waveData[i * 8 + 7];
            float y22 = bytes2Float(tmp2);

            float y = Math.max(y11, y22);
            maxList.add(Float.valueOf(y));
            if (i == 0) {
                max = y;
                min = y;
            }
            if (y > max) {
                max = y;
            }
            if (y < min) {
                min = y;
            }
        }

        float maxWave = 0.0F;
        float minWave = 0.0F;
        for (int j = 0; j < maxList.size(); j++) {
            DataWaveNewVO p;
            float x = (j % 360);
            float y = ((Float) maxList.get(j)).floatValue();
            if (j == 0) {
                maxWave = y;
                minWave = y;
            }
            if (y > maxWave) {
                maxWave = y;
            }
            if (y < minWave) {
                minWave = y;
            }

            String key = x + "," + y;

            if (countMap.containsKey(key)) {
                p = countMap.get(key);
                p.setCount(p.getCount() + 1);
            } else {
                p = new DataWaveNewVO();
                p.setCount(1);
                p.setWavex(x);
                p.setWavey(y);
            }
            countMap.put(key, p);
        }
        countMap.forEach((key, value) -> {
            Object[] obj = {Float.valueOf(value.getWavex()), Float.valueOf(value.getWavey()), Integer.valueOf(value.getCount())};
            chartData.add(obj);
        });
        waveChartData.setChartData(chartData);
        waveChartData.setMaxWave(maxWave);
        waveChartData.setMinWave(minWave);
    }

    public void prpsWaveData(WaveChartData waveChartData, byte[] waveData) {
        if (waveData == null || waveData.length == 0) {
            return;
        }

        List<Object> chartData = new ArrayList();
        float max = 0.0F;
        float min = 0.0F;
        List<Float> maxList = new ArrayList<>();
        for (int i = 0; i < waveData.length / 8; i++) {
            byte[] tmp1 = new byte[4];
            tmp1[0] = waveData[i * 8];
            tmp1[1] = waveData[i * 8 + 1];
            tmp1[2] = waveData[i * 8 + 2];
            tmp1[3] = waveData[i * 8 + 3];
            float y11 = bytes2Float(tmp1);
            byte[] tmp2 = new byte[4];
            tmp2[0] = waveData[i * 8 + 4];
            tmp2[1] = waveData[i * 8 + 5];
            tmp2[2] = waveData[i * 8 + 6];
            tmp2[3] = waveData[i * 8 + 7];
            float y22 = bytes2Float(tmp2);
            //双通道冗余采集：取两个通道的最大值，幅值越大，信号越真实，干扰越小
            float y = Math.max(y11, y22);
            maxList.add(Float.valueOf(y));
            if (i == 0) {
                max = y;
                min = y;
            }
            if (y > max) {
                max = y;
            }
            if (y < min) {
                min = y;
            }
        }

        float maxWave = 0.0F;
        float minWave = 0.0F;
        for (int j = 0; j < maxList.size(); j++) {
            float x = (j % 360);
            float y = ((Float) maxList.get(j)).floatValue();
            if (j == 0) {
                maxWave = y;
                minWave = y;
            }
            if (y > maxWave) {
                maxWave = y;
            }
            if (y < minWave) {
                minWave = y;
            }

            //Object[] prps = {Float.valueOf(x), Float.valueOf(y), Integer.valueOf(j / 360)};
            //Object[] prps = {Float.valueOf(x), Integer.valueOf(j / 360), Float.valueOf(y)};
            //工频周期、相位角度、电压幅值
            Object[] prps = {Integer.valueOf(j / 360), Float.valueOf(x), Float.valueOf(y)};
            chartData.add(prps);
        }
        waveChartData.setChartData(chartData);
        waveChartData.setMaxWave(maxWave);
        waveChartData.setMinWave(minWave);
        waveChartData.setPowerCycleCount(maxList.size()/360);
    }

    public void sybxWaveData(WaveChartData waveChartData, byte[] waveData) {
        List<Object> chartData = new ArrayList();
        float max = 0.0F;
        float min = 0.0F;
        List<Float> yList = new ArrayList<>();
        List<Integer> xList = new ArrayList<>();
        int i;
        for (i = 0; i < waveData.length / 8; i++) {
            byte[] tmp1 = new byte[4];
            tmp1[0] = waveData[i * 8];
            tmp1[1] = waveData[i * 8 + 1];
            tmp1[2] = waveData[i * 8 + 2];
            tmp1[3] = waveData[i * 8 + 3];
            float y11 = bytes2Float(tmp1);

            byte[] tmp2 = new byte[4];
            tmp2[0] = waveData[i * 8 + 4];
            tmp2[1] = waveData[i * 8 + 5];
            tmp2[2] = waveData[i * 8 + 6];
            tmp2[3] = waveData[i * 8 + 7];
            float y22 = bytes2Float(tmp2);

            yList.add(Float.valueOf(y11));
            yList.add(Float.valueOf(-y22));
            if (i == 0) {
                max = y11;
                min = y22;
            }
            if (y11 > max) {
                max = y11;
            }
            if (y22 < min) {
                min = y22;
            }
        }

        for (i = 1; i <= 720; i++) {
            xList.add(Integer.valueOf(i));
        }

        chartData.add(xList);
        chartData.add(yList);

        waveChartData.setChartData(chartData);
        waveChartData.setMaxWave(max);
        waveChartData.setMinWave(min);
    }

    public static float bytes2Float(byte[] arr) {
        int accum = 0;
        accum |= (arr[0] & 0xFF) << 0;
        accum |= (arr[1] & 0xFF) << 8;
        accum |= (arr[2] & 0xFF) << 16;
        accum |= (arr[3] & 0xFF) << 24;
        return Float.intBitsToFloat(accum);
    }
}