package com.cn.jk.powerwaveviewer.entity;

import java.util.List;
import java.util.Objects;

public class WaveChartData {
    private List<Object> chartData;
    private List<String> legend;

    public void setChartData(List<Object> chartData) {
        this.chartData = chartData;
    }

    private List<String> category;
    private float maxWave;
    private float minWave;
    private float intervalX;
    private float intervalY;
    private int amplitudeUnit;
    private byte[] dischargeProb; // 放电类型概率（8字节，0=正常，1=尖端放电...7=其它）
    private String AmplitudeUnitStr;//赋值单位
    // PRPS特有字段
    private int powerCycleCount; // 工频周期数p（PRPS必备，PRPD=0）
    private int phaseWindowCount; // 相位窗数m（必备）



    public void setLegend(List<String> legend) {
        this.legend = legend;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

    public void setMaxWave(float maxWave) {
        this.maxWave = maxWave;
    }

    public void setMinWave(float minWave) {
        this.minWave = minWave;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof WaveChartData)) return false;
        WaveChartData other = (WaveChartData) o;
        if (!other.canEqual(this)) return false;
        if (Float.compare(getMaxWave(), other.getMaxWave()) != 0) return false;
        if (Float.compare(getMinWave(), other.getMinWave()) != 0) return false;
        Object thischartData = getChartData(), otherchartData = other.getChartData();
        if (!Objects.equals(thischartData, otherchartData))
            return false;
        Object thislegend = getLegend(), otherlegend = other.getLegend();
        if ((thislegend == null) ? (otherlegend != null) : !thislegend.equals(otherlegend)) return false;
        Object thiscategory = getCategory(), othercategory = other.getCategory();
        return !((thiscategory == null) ? (othercategory != null) : !thiscategory.equals(othercategory));
    }

    protected boolean canEqual(Object other) {
        return other instanceof WaveChartData;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + Float.floatToIntBits(getMaxWave());
        result = result * 59 + Float.floatToIntBits(getMinWave());
        Object chartData = (Object) getChartData();
        result = result * 59 + ((chartData == null) ? 43 : chartData.hashCode());
        Object legend = (Object) getLegend();
        result = result * 59 + ((legend == null) ? 43 : legend.hashCode());
        Object category = (Object) getCategory();
        return result * 59 + ((category == null) ? 43 : category.hashCode());
    }

    public String toString() {
        return "WaveChartData(chartData=" + getChartData() + ", legend=" + getLegend() + ", category=" + getCategory() + ", maxWave=" + getMaxWave() + ", minWave=" + getMinWave() + ")";
    }

    public List<Object> getChartData() {
        return this.chartData;
    }

    public List<String> getLegend() {
        return this.legend;
    }

    public List<String> getCategory() {
        return this.category;
    }

    public float getMaxWave() {
        return this.maxWave;
    }

    public float getMinWave() {
        return this.minWave;
    }

    public float getIntervalX() {
        return intervalX;
    }

    public void setIntervalX(float intervalX) {
        this.intervalX = intervalX;
    }

    public float getIntervalY() {
        return intervalY;
    }

    public void setIntervalY(float intervalY) {
        this.intervalY = intervalY;
    }

    public int getAmplitudeUnit() {
        return amplitudeUnit;
    }

    public void setAmplitudeUnit(int amplitudeUnit) {
        this.amplitudeUnit = amplitudeUnit;
    }

    public byte[] getDischargeProb() {
        return dischargeProb;
    }

    public void setDischargeProb(byte[] dischargeProb) {
        this.dischargeProb = dischargeProb;
    }

    public String getAmplitudeUnitStr() {
        return AmplitudeUnitStr;
    }

    public void setAmplitudeUnitStr(String amplitudeUnitStr) {
        AmplitudeUnitStr = amplitudeUnitStr;
    }

    public int getPowerCycleCount() {
        return powerCycleCount;
    }

    public void setPowerCycleCount(int powerCycleCount) {
        this.powerCycleCount = powerCycleCount;
    }

    public int getPhaseWindowCount() {
        return phaseWindowCount;
    }

    public void setPhaseWindowCount(int phaseWindowCount) {
        this.phaseWindowCount = phaseWindowCount;
    }
}
