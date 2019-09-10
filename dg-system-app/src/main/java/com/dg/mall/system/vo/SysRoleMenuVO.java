package com.dg.mall.system.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author mabo
 * @create 2019/8/5 16:33
 */

@Data
public class SysRoleMenuVO  {


    private Integer id;

    private Integer roleId;

    private Integer menuId;

    private String createdUser;

    private Date createdTime;

    private String updatedUser;

    private Date updatedTime;

    /**
     * 配置权限选择的菜单节点集合
     */
    private List<String> menuIds = new ArrayList<String>();

}
