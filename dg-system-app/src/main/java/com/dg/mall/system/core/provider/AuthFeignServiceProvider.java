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
package com.dg.mall.system.core.provider;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dg.mall.core.reqres.response.ResponseData;
import com.dg.mall.jwt.utils.JwtTokenUtil;
import com.dg.mall.logger.util.LogUtil;
import com.dg.mall.model.constants.RedisKeyConstants;
import com.dg.mall.model.exception.ServiceException;
import com.dg.mall.system.api.context.LoginUser;
import com.dg.mall.system.api.exception.enums.SystemExceptionEnum;
import com.dg.mall.system.api.feign.AuthFeignService;
import com.dg.mall.system.api.req.LoginReq;
import com.dg.mall.system.api.req.ToeknReq;
import com.dg.mall.system.api.resp.UserMenuResp;
import com.dg.mall.system.entity.SysUser;
import com.dg.mall.system.service.LoginUserService;
import com.dg.mall.system.service.SysMenuService;
import com.dg.mall.system.service.SysUserService;
import com.google.common.collect.Maps;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.TimeUnit;


@RestController
@RequestMapping("/api/authService")
@Primary
public class AuthFeignServiceProvider implements AuthFeignService {

    @Autowired
    private SysUserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private LoginUserService loginUserService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SysMenuService sysMenuService;



    @Override
    public ResponseData login(@Validated @RequestBody LoginReq loginReq) {
        SysUser user = userService.getOne(new QueryWrapper<SysUser>().lambda().eq(SysUser::getPhone, loginReq.getUserName()));
        if(user == null){
            throw new ServiceException(SystemExceptionEnum.USER_NOT_FOUND);
        }
        if(user.getIsDeleted()){
            throw new ServiceException(SystemExceptionEnum.USER_STATUS_ERROR);
        }

        if(!user.getIsUsed()){
            throw new ServiceException(SystemExceptionEnum.USER_STATUS_IS_NOT_USED);
        }


        if(!loginReq.getPassword().equals(user.getPassword())){
            throw new ServiceException(SystemExceptionEnum.INVALID_PWD);
        }
        //判断该用户是否存在已经登录的token ，存在的话将旧token删除
        String userToken = (String) redisTemplate.opsForValue().get(RedisKeyConstants.SYSTEM_USER_TOKEN_KEY.concat(user.getPhone()));
        if(userToken != null){
            redisTemplate.delete(RedisKeyConstants.SYSTEM_USER_TOKEN_KEY.concat(user.getPhone()));
        }

        Map<String, Object> claims = Maps.newHashMap();
        claims.put("sub", user.getName());
        claims.put("userPhone", user.getPhone());
        String token = jwtTokenUtil.generateToken(String.valueOf(user.getUserId()),claims);
        redisTemplate.opsForValue().set(RedisKeyConstants.SYSTEM_USER_TOKEN_KEY.concat(String.valueOf(user.getPhone())), token, 1, TimeUnit.DAYS);
        return ResponseData.success(token);
    }

    @Override
    public boolean checkToken(@Validated @RequestBody ToeknReq tokenReq) {
        try {
            Claims claims = jwtTokenUtil.getClaimFromToken(tokenReq.getToken());
            String phone = String.valueOf(claims.get("userPhone"));
            String userToken = (String) redisTemplate.opsForValue().get(RedisKeyConstants.SYSTEM_USER_TOKEN_KEY.concat(phone));
            if(userToken == null){
                LogUtil.info("用户手机号:{},本次请求token:{}，redis中不存在该token。", phone, tokenReq.getToken());
                return false;
            }
            if(!userToken.equals(tokenReq.getToken())){
                LogUtil.info("校验token不一致，用户手机号:{},本次请求token:{}，redis中的token。", phone, tokenReq.getToken(), userToken);
                return false;
            }
            return jwtTokenUtil.checkToken(tokenReq.getToken());
        } catch (Exception e) {
            throw new ServiceException(SystemExceptionEnum.USER_TOKEN_ERROR);
        }
    }

    @Override
    public void logout(@Validated @RequestBody ToeknReq tokenReq) {
        try {
            Claims claims = jwtTokenUtil.getClaimFromToken(tokenReq.getToken());
            String phone = String.valueOf(claims.get("userPhone"));
            redisTemplate.delete(RedisKeyConstants.SYSTEM_USER_TOKEN_KEY.concat(phone));
        } catch (Exception e) {
           throw new ServiceException(SystemExceptionEnum.SERVICE_ERROR);
        }
    }

    @Override
    public LoginUser getLoginUserByToken(@RequestParam("token") String token) {
        LoginUser loginUser = loginUserService.getLoginUserByToken(token);
        return loginUser;
    }

    @Override
    public UserMenuResp getUserMenuByToken(String token) {
        return sysMenuService.getUserMenuByToken(token);
    }
}
