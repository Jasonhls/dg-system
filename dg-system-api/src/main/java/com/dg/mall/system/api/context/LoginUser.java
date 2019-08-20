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
package com.dg.mall.system.api.context;


import lombok.Data;

import java.util.List;


/**
 * 当前用户的登录信息
 */
@Data
public class LoginUser {

    /**
     * 主键id
     */
    private Integer userId;

    /**
     * 手机号/账号
     */
    private String phone;

    /**
     * 名称
     */
    private String name;

    /**
     * 性别（1：男 0：女）
     */
    private Integer sex;

    private String email;

    /**
     * 状态(0:未启用 1：启用  2：冻结  3：删除）
     */
    private Integer status;
    /**
     * 头像照片
     *
     */
    private String photo;

    /**
     * 是否普通管理员（1：是，0：否）
     */
    private Boolean isGeneralAdministrator;

    /**
     * 是否具备审核（1：是，0：否）
     */
    private Boolean isToexamine;

    /**
     * 备注
     */
    private String remark;

    /**
     * 角色集合
     */
    private List<SysRoleDTO> roles;

    /**
     * 菜单集合
     */
    private List<SysMenuDTO> menus;
}
