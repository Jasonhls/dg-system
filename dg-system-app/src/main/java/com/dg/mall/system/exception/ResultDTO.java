package com.dg.mall.system.exception;

import lombok.Data;

@Data
public class ResultDTO<T> {
    private Integer status;
    private String msg;
    private T data;
}
