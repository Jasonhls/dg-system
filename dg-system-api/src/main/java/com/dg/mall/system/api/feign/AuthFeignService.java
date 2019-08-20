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
package com.dg.mall.system.api.feign;


import com.dg.mall.core.reqres.response.ResponseData;
import com.dg.mall.system.api.context.LoginUser;
import com.dg.mall.system.api.req.LoginReq;
import com.dg.mall.system.api.req.ToeknReq;
import com.dg.mall.system.api.resp.UserMenuResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 鉴权服务,提供颁发,校验,注销等方法
 *
 * @author fengshuonan
 * @date 2018-02-07 9:57
 */
@FeignClient(name = "dg-system", path = "/api/authService")
public interface AuthFeignService {

    /**
     * <p>登录(验证账号密码)</p>
     * <p>若登录成功则返回token,若登录不成功则返回null</p>
     */
    @PostMapping("login")
    ResponseData login(@RequestBody LoginReq loginReq);

    /**
     * 校验token(true-校验成功,false-校验失败)
     */
    @PostMapping("checkToken")
    boolean checkToken(@RequestBody ToeknReq tokenReq);

    /**
     * 注销token
     */
    @PostMapping("logout")
    void logout(@RequestBody ToeknReq tokenReq);

    /**
     * 通过token获取用户信息
     */
    @PostMapping("getLoginUserByToken")
    LoginUser getLoginUserByToken(@RequestParam("token") String token);


    /**
     * 通过token获取用户权限树信息
     */
    @PostMapping("getUserMenuByToken")
    UserMenuResp getUserMenuByToken(@RequestParam("token") String token);

    

}
