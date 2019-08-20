package com.dg.mall.system.core.vo.req;

import lombok.Data;

/**
 * 角色分页列表查询入参
 */
@Data
public class RoleQueryVO {
    /**
     * 角色id
     */
    private Integer roleId;
    /**
     * 角色名
     */
    private String roleName;
    /**
     * 模糊查询关键词
     */
    private String keyWords;
    /**
     * 起始时间
     */
    private String startTime;
    /**
     * 终止时间
     */
    private String endTime;
    /**
     * 角色描述
     */
    private String roleDescription;
    /**
     * 创建人
     */
    private String createdUser;
    /**
     * 当前页
     */
    private Long current;
    /**
     * 每页大小
     */
    private Long size;
}
