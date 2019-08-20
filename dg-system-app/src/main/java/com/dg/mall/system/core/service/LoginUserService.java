package com.dg.mall.system.core.service;

import com.dg.mall.system.api.context.LoginUser;

/**
 * @description: 获取登录用户信息接口
 * @author: wangqiang
 * @create: 2019-08-09 09:13
 **/
public interface LoginUserService {

    LoginUser getLoginUserByToken(String token);
}
