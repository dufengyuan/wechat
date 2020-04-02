package com.chat.utils;

import java.io.Serializable;

public class JSONResult implements Serializable {
    private static final Integer ok = 200;
    private static final Integer error = 500;

    private Integer status;
    private String msg;
    private Object data;

    public static JSONResult ok(Object data,String msg) {
        return new JSONResult(ok,msg,data);
    }
    public static JSONResult error(Object data,String msg) {
        return new JSONResult(error,msg,data);
    }

    public JSONResult(Integer status, String msg, Object data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public static Integer getOk() {
        return ok;
    }

    public static Integer getError() {
        return error;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
