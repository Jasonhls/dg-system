package com.dg.mall.system.controller;

import com.dg.mall.core.reqres.response.ResponseData;
import com.dg.mall.core.reqres.response.SuccessResponseData;
import com.dg.mall.logger.config.SysLog;
import com.dg.mall.system.api.context.LoginUser;
import com.dg.mall.system.context.LoginContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 登录用户相关操作
 * @author: wangqiang
 * @create: 2019-08-09 10:30
 **/
@RestController
public class LoginController {

    /**
     * 获取当前登录用户
     * @return
     */
    @GetMapping("loginUser")
    @SysLog(description = "获取当前登录用户信息")
    public ResponseData getLoginUser(){
        LoginUser loginUser = LoginContext.me().getLoginUser();
        return new SuccessResponseData(loginUser);
    }
}
