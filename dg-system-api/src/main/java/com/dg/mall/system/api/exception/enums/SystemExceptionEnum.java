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
package com.dg.mall.system.api.exception.enums;


import com.dg.mall.model.exception.AbstractBaseExceptionEnum;

/**
 * 鉴权相关的错误异常
 *
 * @author fengshuonan
 * @date 2018-08-26-下午3:19
 */
public enum SystemExceptionEnum implements AbstractBaseExceptionEnum {

    /**
     *
     */
    COMMENT_SERVICE_EXCEPTION(1000,"%s"),
    NULL_POINT_EXCEPTION(1001,"%s不能为空"),
    USER_NOT_FOUND(3110, "用户不存在！"),
    USER_TOKEN_ERROR(3112, "token 无效！"),
    USER_STATUS_ERROR(3113, "用户状态异常，请联系管理员"),
    USER_STATUS_IS_NOT_USED(3114, "用户未启用"),
    USER_PHONE_IS_NOT_NULL(3115, "用户手机号不能为空"),
    USER_EMAIL_IS_NOT_NULL(3116, "用户邮箱不能为空"),
    USER_PHONE_EFFECTIVE_IS_EXIST(3117, "用户手机号已存在"),
    USER_PHONE_IS_EXIST(3118, "用户手机号已存在，请联系管理员"),
    USER_EMAIL_IS_EXIST(3119, "用户邮箱已经存在"), 
    USER_ROLE_GENERAL_ADMINISTRATOR(3120, "用户所在角色下必须只有一个普通管理员"), 
    USER_NOT_DELETE(3121, "无法删除当前登录用户"), 
    INVALID_PWD(3111, "密码错误！"),
    SERVICE_ERROR(500, "系统异常");

    private int code;
    private String message;

    SystemExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
