package com.kd.novel.backend.vo;


import com.kd.novel.backend.config.constants.DefaultConstants;

import java.io.Serializable;

public class JsonResult implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer code = DefaultConstants.SUCCESS_SERVICE;
    private String message = "ok";
    private Object data;

    public JsonResult(String msg) {
        this.message = msg;
    }

    public JsonResult(Object data) {
        this.data = data;
    }

    public JsonResult(Throwable e) {
        this.code = 0;
        this.message = e.getMessage();
    }

    public JsonResult(Integer code, String msg) {
        this.code = code;
        this.message = msg;
    }

    public static JsonResult oK() {
        return oK(null);
    }

    public static JsonResult oK(Object data) {
        return new JsonResult(data);
    }

    public static JsonResult build() {
        return build(DefaultConstants.SUCCESS_SERVICE, "");
    }

    public static JsonResult build(Integer status, String msg) {
        return new JsonResult(status, msg);

    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
