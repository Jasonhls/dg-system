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
package com.dg.mall.system.modular.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author stylefeng
 * @since 2018-08-26
 */
@TableName("sys_user")
@Data
public class SysUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId("USER_ID")
    private Long userId;
    /**
     * 头像
     */
    @TableField("AVATAR")
    private String avatar;
    /**
     * 账号
     */
    @TableField("ACCOUNT")
    private String account;
    /**
     * 密码
     */
    @TableField("PASSWORD")
    private String password;
    /**
     * md5密码盐
     */
    @TableField("SALT")
    private String salt;
    /**
     * 名字
     */
    @TableField("NAME")
    private String name;
    /**
     * 生日
     */
    @TableField("BIRTHDAY")
    private Date birthday;
    /**
     * 性别（M：男 F：女）
     */
    @TableField("SEX")
    private String sex;
    /**
     * 电子邮件
     */
    @TableField("EMAIL")
    private String email;
    /**
     * 电话
     */
    @TableField("PHONE")
    private String phone;
    /**
     * 状态(1：启用  2：冻结  3：删除）
     */
    @TableField("STATUS")
    private Integer status;
    /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
    private Date createTime;
    /**
     * 更新时间
     */
    @TableField(value = "UPDATE_TIME", fill = FieldFill.UPDATE)
    private Date updateTime;

}
