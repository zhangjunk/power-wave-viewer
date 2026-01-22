package com.cn.jk.powerwaveviewer.controller;

import com.cn.jk.powerwaveviewer.entity.AjaxResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BaseController {
    protected final Logger logger = LoggerFactory.getLogger(getClass());


    public AjaxResult success() {
        return AjaxResult.success();
    }


    public AjaxResult success(String message) {
        return AjaxResult.success(message);
    }


    public AjaxResult success(Object data) {
        return AjaxResult.success(data);
    }


    public AjaxResult error() {
        return AjaxResult.error();
    }


    public AjaxResult error(String message) {
        return AjaxResult.error(message);
    }


    public AjaxResult warn(String message) {
        return AjaxResult.warn(message);
    }


    protected AjaxResult toAjax(int rows) {
        return (rows > 0) ? AjaxResult.success() : AjaxResult.error();
    }


    protected AjaxResult toAjax(boolean result) {
        return result ? success() : error();
    }
}

