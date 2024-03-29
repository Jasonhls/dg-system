/**
 * Copyright 2018-2020 stylefeng & fengshuonan (sn93@qq.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dg.mall.system.context;


import com.dg.mall.core.util.HttpContext;
import com.dg.mall.core.util.SpringContextHolder;
import com.dg.mall.core.util.ToolUtil;
import com.dg.mall.model.exception.ServiceException;
import com.dg.mall.model.exception.enums.CoreExceptionEnum;
import com.dg.mall.system.api.context.LoginUser;
import com.dg.mall.system.service.LoginUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;


/**
 * 登录信息上下文
 *
 * @author fengshuonan
 * @date 2018-02-05 16:58
 */
@Component
public class LoginContext implements AbstractLoginContext {

    @Autowired
    private LoginUserService loginUserService;

    public static LoginContext me() {
        return SpringContextHolder.getBean(LoginContext.class);
    }

    /**
     * 获取当前用户的token
     * <p>
     * 先判断header中是否有Authorization字段，
     * 如果header中没有这个字段，则检查请求参数中是否带token，
     * 如果任意一个地方有这个值，则返回这个值
     * 两个地方都没有token，则抛出没有登录用户异常
     */
    @Override
    public String getCurrentUserToken() {
        HttpServletRequest request = HttpContext.getRequest();
        if (request == null) {
            throw new ServiceException(CoreExceptionEnum.NO_CURRENT_USER);
        }

        //如果请求是在http环境下，则有request对象
        String authorization = request.getHeader("Manage_Authorization");
        if (ToolUtil.isNotEmpty(authorization)) {
            return authorization;
        } else {
            String token = request.getParameter("token");
            if (ToolUtil.isNotEmpty(token)) {
                return token;
            } else {
                throw new ServiceException(CoreExceptionEnum.NO_CURRENT_USER);
            }
        }
    }

    /**
     * 获取当前用户
     * <p>
     * 先从ThreadLocal中拿user，如果有值就直接返回，没取到再去调用远程服务,调用完远程服务把获取到的user放到Threadlocal里
     */
    @Override
    public <T> T getLoginUser() {
        LoginUser currentUser = LoginUserHolder.get();
        if (currentUser != null) {
            return (T) currentUser;
        } else {
            String token = getCurrentUserToken();
            LoginUser abstractLoginUser  = this.loginUserService.getLoginUserByToken(token);
            LoginUserHolder.set(abstractLoginUser);
            return (T) abstractLoginUser;
        }
    }

//    /**
//     * 获取当前登录用户的账户id
//     */
//    public Long getUserAccountId() {
//        AbstractLoginUser abstractLoginUser = this.getLoginUser();
//        if (abstractLoginUser == null) {
//            return null;
//        } else {
//            return abstractLoginUser.getUserUniqueId();
//        }
//    }

}
