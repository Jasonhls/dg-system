package com.dg.mall.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author 你的名字
 * @since 2019-08-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("dg_sys_menu")
public class SysMenu implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 自增菜单ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 父菜单ID
     */
    private Integer parentId;

    /**
     * 路径
     */
    private String path;

    /**
     * 路由
     */
    private String matchRoute;

    /**
     * 定向路由
     */
    private String redirect;

    /**
     * 后端接口路径
     */
    private String url;

    /**
     * 前端标识
     */
    private String mark;

    /**
     * 标题
     */
    private String title;

    /**
     * 图标
     */
    private String icon;

    /**
     * 备注
     */
    private String memo;

    /**
     * 菜单类型（1目录，2.3菜单（3为按钮的直接菜单），4按钮）
     */
    private Integer type;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 创建人
     */
    private String createdUser;

    /**
     * 更新时间
     */
    private Date updatedTime;

    /**
     * 更新人
     */
    private String updatedUser;

    /**
     * 是否删除(0未删除，1已删除)
     */
    private Integer isDeleted;

}
