package com.dg.mall.system.exception;

public class ResultUtil {
    public static ResultDTO success(Object obj){
        ResultDTO result = new ResultDTO();
        result.setStatus(0);
        result.setMsg("success");
        result.setData(obj);
        return result;
    }

    public static ResultDTO success(){
        return success(null);
    }

    public static ResultDTO error(Integer code,String msg){
        ResultDTO result = new ResultDTO();
        result.setStatus(code);
        result.setMsg(msg);
        result.setData(null);
        return result;
    }

    public static ResultDTO error(MyExceptionEnum exceptionEnum){
        ResultDTO result = new ResultDTO();
        result.setStatus(exceptionEnum.getCode());
        result.setMsg(exceptionEnum.getMsg());
        result.setData(null);
        return result;
    }

}
