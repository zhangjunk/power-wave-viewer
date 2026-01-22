package com.cn.jk.powerwaveviewer.entity;
public class DataWaveNewVO {
  private float wavex;
  public void setWavex(float wavex) {
      this.wavex = wavex;
  }
  private float wavey;
  private int count;
  public void setWavey(float wavey) {
      this.wavey = wavey;
  }
  public void setCount(int count) {
      this.count = count;
  }
  public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof DataWaveNewVO)) return false;
      DataWaveNewVO other = (DataWaveNewVO)o;
      return !other.canEqual(this) ? false : ((Float.compare(getWavex(), other.getWavex()) != 0) ? false : ((Float.compare(getWavey(), other.getWavey()) != 0) ? false : (!(getCount() != other.getCount()))));
  }
  protected boolean canEqual(Object other) {
      return other instanceof DataWaveNewVO;
  }
  public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + Float.floatToIntBits(getWavex());
      result = result * 59 + Float.floatToIntBits(getWavey());
      return result * 59 + getCount();
  }
  public String toString() {
      return "DataWaveNewVO(wavex=" + getWavex() + ", wavey=" + getWavey() + ", count=" + getCount() + ")";
  }

  public float getWavex() { return this.wavex; }
  public float getWavey() { return this.wavey; } public int getCount() {
    return this.count;
  }
}
