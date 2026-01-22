package com.cn.jk.powerwaveviewer.service.impl;

import com.cn.jk.powerwaveviewer.entity.DataWaveNewVO;
import com.cn.jk.powerwaveviewer.entity.GraphData;
import com.cn.jk.powerwaveviewer.entity.UHFPDatFile;
import com.cn.jk.powerwaveviewer.entity.WaveChartData;
import com.cn.jk.powerwaveviewer.service.PartialDischargeDataParser;
import com.cn.jk.powerwaveviewer.service.parser.UHFPDatParser;
import com.cn.jk.powerwaveviewer.utils.LittleEndianParser;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UHFPPartialDischargeDataParserImpl implements PartialDischargeDataParser {
    public void prpdWaveData(WaveChartData waveChartData, byte[] waveData) {
        if (waveData == null || waveData.length == 0) {
            return;
        }
        UHFPDatParser uhfpDatParser = new UHFPDatParser();
        UHFPDatFile uhfpDatFile = uhfpDatParser.parseDatFileByByte(waveData);
        List<GraphData> graphList = uhfpDatFile.getGraphList();
        List<Object> chartData = new ArrayList();
        float maxWave = 0;
        float minWave = 0;

        int m = 0;
        int n = 0;
        for (int i = 0;i<graphList.size();i++){
            GraphData graphData = graphList.get(i);
            if (33== graphData.getGraphType()){
                chartData = (List<Object>) graphData.getGraphData();
                maxWave = graphData.getAmplitudeMax();
                minWave = graphData.getAmplitudeMin();
                int unit = graphData.getAmplitudeUnit();
                m = graphData.getPhaseWindowCount();
                n = graphData.getQuantAmplitudeCount();
                break;
            }
        }

        float intervalX = 360/m;
        float intervalY = (maxWave-minWave)/n;
        waveChartData.setChartData(chartData);
        waveChartData.setMaxWave(maxWave);
        waveChartData.setMinWave(minWave);
        waveChartData.setIntervalX(intervalX);
        waveChartData.setIntervalY(intervalY);
    }

    public void prpsWaveData(WaveChartData waveChartData, byte[] waveData) {
        if (waveData == null || waveData.length == 0) {
            return;
        }
        UHFPDatParser uhfpDatParser = new UHFPDatParser();
        UHFPDatFile uhfpDatFile = uhfpDatParser.parseDatFileByByte(waveData);
        List<GraphData> graphList = uhfpDatFile.getGraphList();
        List<Object> chartData = new ArrayList();
        float maxWave = 0;
        float minWave = 0;

        int powerCycleCount = 0;
        int phaseWindowCount = 0;
        for (int i = 0;i<graphList.size();i++){
            GraphData graphData = graphList.get(i);
            if (34== graphData.getGraphType()){
                chartData = (List<Object>) graphData.getGraphData();
                powerCycleCount = graphData.getPowerCycleCount();
                phaseWindowCount = graphData.getPhaseWindowCount();
                maxWave = graphData.getAmplitudeMax();
                minWave = graphData.getAmplitudeMin();
                int unit = graphData.getAmplitudeUnit();
                break;
            }
        }
        waveChartData.setChartData(chartData);
        waveChartData.setMaxWave(maxWave);
        waveChartData.setMinWave(minWave);
        waveChartData.setPowerCycleCount(powerCycleCount);
        waveChartData.setPhaseWindowCount(phaseWindowCount);
    }

    public void sybxWaveData(WaveChartData waveChartData, byte[] waveData) {

        if (waveData == null || waveData.length == 0) {
            return;
        }
        UHFPDatParser uhfpDatParser = new UHFPDatParser();
        UHFPDatFile uhfpDatFile = uhfpDatParser.parseDatFileByByte(waveData);
        List<GraphData> graphList = uhfpDatFile.getGraphList();
        List<Object> chartData = new ArrayList();
        float maxWave = 0;
        float minWave = 0;

        int powerCycleCount = 0;
        int phaseWindowCount = 0;
        for (int i = 0;i<graphList.size();i++){
            GraphData graphData = graphList.get(i);
            if (34== graphData.getGraphType()){
                chartData = (List<Object>) graphData.getGraphData();
                powerCycleCount = graphData.getPowerCycleCount();
                phaseWindowCount = graphData.getPhaseWindowCount();
                maxWave = graphData.getAmplitudeMax();
                minWave = graphData.getAmplitudeMin();
                int unit = graphData.getAmplitudeUnit();
                break;
            }
        }
        waveChartData.setChartData(chartData);
        waveChartData.setMaxWave(maxWave);
        waveChartData.setMinWave(minWave);
        waveChartData.setPowerCycleCount(powerCycleCount);
        waveChartData.setPhaseWindowCount(phaseWindowCount);
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