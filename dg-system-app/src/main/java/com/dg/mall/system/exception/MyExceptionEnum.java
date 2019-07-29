package com.dg.mall.system.exception;

public enum MyExceptionEnum {
    UNKONW_ERROR(-1,"未知错误"),
    USER_NOT_FIND(-101,"用户不存在");

    MyExceptionEnum() {
    }

    MyExceptionEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private Integer code;
    private String msg;

    public Integer getCode(){
        return code;
    }

    public String getMsg(){
        return msg;
    }
}
