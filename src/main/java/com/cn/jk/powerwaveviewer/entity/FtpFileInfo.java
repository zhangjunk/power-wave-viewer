package com.cn.jk.powerwaveviewer.entity;

import java.io.Serializable;

public class FtpFileInfo implements Serializable {
  private String fileName;
  private Integer fileType;
  private String createTime;

  public void setFileName(String fileName) {
      this.fileName = fileName;
  }
  private Long fileSize;
  private Long timestamp;
  private Boolean hasChildren;
  private String filePath;
  public void setFileType(Integer fileType) {
      this.fileType = fileType;
  }
  public void setCreateTime(String createTime) {
      this.createTime = createTime;
  }
  public void setFileSize(Long fileSize) {
      this.fileSize = fileSize;
  }
  public void setTimestamp(Long timestamp) {
      this.timestamp = timestamp;
  }
  public void setHasChildren(Boolean hasChildren) {
      this.hasChildren = hasChildren;
  }
  public void setFilePath(String filePath) {
      this.filePath = filePath;
  }
  public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof FtpFileInfo)) return false;
      FtpFileInfo other = (FtpFileInfo)o;
      if (!other.canEqual(this)) return false;
      Object fileType = getFileType(), other$fileType = other.getFileType();
      if ((fileType == null) ? (other$fileType != null) : !fileType.equals(other$fileType)) return false;
      Object fileSize = getFileSize(), otherfileSize = other.getFileSize();
      if ((fileSize == null) ? (otherfileSize != null) : !fileSize.equals(otherfileSize)) return false;
      Object timestamp = getTimestamp(), othertimestamp = other.getTimestamp();
      if ((timestamp == null) ? (timestamp != null) : !timestamp.equals(othertimestamp)) return false;
      Object thishasChildren = getHasChildren(), otherhasChildren = other.getHasChildren();
      if ((thishasChildren == null) ? (otherhasChildren != null) : !thishasChildren.equals(otherhasChildren)) return false;
      Object thisfileName = getFileName(), otherfileName = other.getFileName();
      if ((thisfileName == null) ? (otherfileName != null) : !thisfileName.equals(otherfileName)) return false;
      Object thiscreateTime = getCreateTime(), othercreateTime = other.getCreateTime();
      if ((thiscreateTime == null) ? (othercreateTime != null) : !thiscreateTime.equals(othercreateTime)) return false;
      Object thisfilePath = getFilePath(), otherfilePath = other.getFilePath();
      return !((thisfilePath == null) ? (otherfilePath != null) : !thisfilePath.equals(otherfilePath));
  }
  protected boolean canEqual(Object other) {
      return other instanceof FtpFileInfo;
  }
  public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $fileType = getFileType();
      result = result * 59 + (($fileType == null) ? 43 : $fileType.hashCode());
      Object $fileSize = getFileSize();
      result = result * 59 + (($fileSize == null) ? 43 : $fileSize.hashCode());
      Object $timestamp = getTimestamp();
      result = result * 59 + (($timestamp == null) ? 43 : $timestamp.hashCode());
      Object $hasChildren = getHasChildren();
      result = result * 59 + (($hasChildren == null) ? 43 : $hasChildren.hashCode());
      Object $fileName = getFileName();
      result = result * 59 + (($fileName == null) ? 43 : $fileName.hashCode());
      Object $createTime = getCreateTime();
      result = result * 59 + (($createTime == null) ? 43 : $createTime.hashCode());
      Object $filePath = getFilePath();
      return result * 59 + (($filePath == null) ? 43 : $filePath.hashCode());
  }
  public String toString() {
      return "FtpFileInfo(fileName=" + getFileName() + ", fileType=" + getFileType() + ", createTime=" + getCreateTime() + ", fileSize=" + getFileSize() + ", timestamp=" + getTimestamp() + ", hasChildren=" + getHasChildren() + ", filePath=" + getFilePath() + ")";
  }

  public String getFileName() {
    return this.fileName;
  } public Integer getFileType() {
    return this.fileType;
  } public String getCreateTime() {
    return this.createTime;
  } public Long getFileSize() {
    return this.fileSize;
  } public Long getTimestamp() {
    return this.timestamp;
  } public Boolean getHasChildren() {
    return this.hasChildren;
  } public String getFilePath() {
    return this.filePath;
  }
}
