package com.bean;

import java.sql.CallableStatement;
import java.sql.SQLException;

/**
 * @Description :存储过程返回类
 * @Reference :
 * @Author : yihang.lv
 * @CreateDate : 2019-07-05 14:34
 * @Modify:
 **/
public class SPResult {
    private int code;
    private String msg = "";
    private Object tag;

    public SPResult(CallableStatement stmt, int paramIndex) throws SQLException {
        code = stmt.getInt(paramIndex);
        String returnMsg = stmt.getString(paramIndex + 1);
        msg = returnMsg;
    }

    public SPResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public SPResult() {
        code = 0;
    }

    /**
     * 直接在其他程序中创建SPResult时调用此方法
     */
    public SPResult(String message) {
        code = 0;
        msg = message;
    }

    public boolean isSuccessful() {
        return code == 0;
    }

    public void setCode(int c) {
        this.code = c;
    }

    public int getCode() {
        return code;
    }

    public void setMessage(String s) {
        this.msg = s;
    }

    public String getMessage() {
        return msg;
    }

    public String getDebugMessage() {
        return msg;
    }

    public String toString() {
        return "[" + msg + "]";
    }

    public String toJSONString() {
        return "{\"code\":" + code + ",\"msg\":" + msg + "}";
    }

    public Object getTag() {
        return this.tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }
}
