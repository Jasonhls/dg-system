package com.dg.mall.system.api.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @description: 登录请求对象
 * @author: wangqiang
 * @create: 2019-08-08 10:56
 **/
@Data
public class LoginReq {

    /**
     * 登录账号
     */
    @NotBlank(message = "用户名不能为空")
    private String userName;

    /**
     * 登录密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
}
