package com.cn.jk.powerwaveviewer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ftp")
public class FtpConfig {
  private String downloadDict;

  public void setDownloadDict(String downloadDict) {
    this.downloadDict = downloadDict;
  }

  public String toString() {
    return "FtpConfig(downloadDict=" + getDownloadDict() + ")";
  }
  public int hashCode() {
    int PRIME = 59;
    int result = 1;
    Object downloadDict = getDownloadDict();
    return result * 59 + ((downloadDict == null) ? 43 : downloadDict.hashCode());
  }
  @Override
  public boolean equals(Object o) {
    // 1. 自反性：对象和自身比较，直接返回true
    if (o == this) {
      return true;
    }

    // 2. 检查是否是同一个类型（排除null和其他类型）
    if (!(o instanceof FtpConfig)) {
      return false;
    }

    // 3. 类型强转
    FtpConfig other = (FtpConfig) o;

    // 4. 调用 canEqual 方法（通常是 Lombok/编译器生成，用于子类判断）
    if (!other.canEqual(this)) {
      return false;
    }

    // 5. 获取两个对象的 downloadDict 属性
    Object thisDownloadDict = this.getDownloadDict();
    Object otherDownloadDict = other.getDownloadDict();

    // 6. 核心：判断 downloadDict 属性是否相等（处理null的情况）
    // 原代码的三元表达式 + 取反 拆解成易读的逻辑
    if (thisDownloadDict == null) {
      // 自身为null时，对方也必须为null才相等
      return otherDownloadDict == null;
    } else {
      // 自身不为null时，调用equals比较内容
      return thisDownloadDict.equals(otherDownloadDict);
    }
  }

  // 配套的 canEqual 方法（反编译代码中用到，必须补上）
  protected boolean canEqual(Object other) {
    // 核心：判断other是否是FtpConfig类型（或其子类）
    return other instanceof FtpConfig;
  }

  public String getDownloadDict() {
    return this.downloadDict;
  }
}
