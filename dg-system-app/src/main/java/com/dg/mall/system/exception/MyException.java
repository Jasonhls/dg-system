package com.dg.mall.system.exception;

import lombok.Data;

@Data
public class MyException extends RuntimeException{
    private Integer code;
    private String message;

    public MyException() {
    }

    public MyException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
