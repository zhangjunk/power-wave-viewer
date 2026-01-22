package com.cn.jk.powerwaveviewer.entity;

import java.io.Serializable;
import java.util.List;

public class TableDataInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private long total;
    private List<?> rows;
    private int code;
    private String msg;
    private Object data;
    private String encrypt;
    private Integer encType;

    public TableDataInfo() {
    }

    public TableDataInfo(List<?> list, int total) {
        this.rows = list;
        this.total = total;
    }


    public long getTotal() {
        return this.total;
    }


    public void setTotal(long total) {
        this.total = total;
    }


    public List<?> getRows() {
        return this.rows;
    }


    public void setRows(List<?> rows) {
        this.rows = rows;
    }


    public int getCode() {
        return this.code;
    }


    public void setCode(int code) {
        this.code = code;
    }


    public String getMsg() {
        return this.msg;
    }


    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return this.data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getEncrypt() {
        return this.encrypt;
    }

    public void setEncrypt(String encrypt) {
        this.encrypt = encrypt;
    }

    public Integer getEncType() {
        return this.encType;
    }

    public void setEncType(Integer encType) {
        this.encType = encType;
    }
}
