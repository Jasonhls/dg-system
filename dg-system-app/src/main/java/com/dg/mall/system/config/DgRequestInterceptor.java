package com.dg.mall.system.config;

import com.dg.mall.system.context.LoginUserHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @description: 拦截器
 * @author: wangqiang
 * @create: 2019-08-09 09:52
 **/
public class DgRequestInterceptor implements HandlerInterceptor {

    /**
     * 释放资源
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if(LoginUserHolder.get() != null){
            LoginUserHolder.remove();
        }
    }
}
