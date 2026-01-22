package com.cn.jk.powerwaveviewer.entity;

 import java.util.HashMap;
 import java.util.Objects;

 public class AjaxResult extends HashMap<String, Object>
 {
   private static final long serialVersionUID = 1L;
   public static final String CODE_TAG = "code";
   public static final String MSG_TAG = "msg";
   public static final String DATA_TAG = "data";

   public AjaxResult() {}

   public AjaxResult(int code, String msg) {
     super.put("code", Integer.valueOf(code));
     super.put("msg", msg);
   }
   public AjaxResult(int code, String msg, Object data) {
     super.put("code", Integer.valueOf(code));
     super.put("msg", msg);
     if (data != null)
     {
       super.put("data", data);
     }
   }
   public static AjaxResult success() {
     return success("操作成功");
   }

   public static AjaxResult success(Object data) {
     return success("操作成功", data);
   }
   public static AjaxResult success(String msg) {
     return success(msg, null);
   }
   public static AjaxResult success(String msg, Object data) {
     return new AjaxResult(200, msg, data);
   }
   public static AjaxResult warn(String msg) {
     return warn(msg, null);
   }
   public static AjaxResult warn(String msg, Object data) {
     return new AjaxResult(601, msg, data);
   }
   public static AjaxResult error() {
     return error("操作失败");
   }
   public static AjaxResult error(String msg) {
     return error(msg, null);
   }
   public static AjaxResult error(String msg, Object data) {
     return new AjaxResult(500, msg, data);
   }
   public static AjaxResult error(int code, String msg) {
     return new AjaxResult(code, msg, null);
   }
   public boolean isSuccess() {
     return !isError();
   }
   public boolean isError() {
     return Objects.equals(Integer.valueOf(500), get("code"));
   }
   public AjaxResult put(String key, Object value) {
     super.put(key, value);
     return this;
   }
 }
