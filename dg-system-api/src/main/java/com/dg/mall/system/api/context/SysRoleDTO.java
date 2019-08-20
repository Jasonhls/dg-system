package com.dg.mall.system.api.context;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * SysRole服务传输对象
 * </p>
 *
 * @author jobob
 * @since 2019-08-01
 */
@Data
@AllArgsConstructor
public class SysRoleDTO implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 角色id
     */
    private Integer roleId;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色概述
     */
    private String roleDescription;
}
