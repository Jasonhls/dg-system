package com.dg.mall.system.vo;


import com.dg.mall.system.entity.SysMenu;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author jmabo
 * @since 2019-08-01
 */
@Data
public class SysMenuVO {

    private Integer id;

    private String name;

    private Integer parentId;

    private Integer sort;

    private String path;

   // private String title;

    private String matchRoute;

    private String redirect;

    private String url;

   // private String icon;

    private Integer type;

    private String memo;

    private String createdUser;

    private Date createdTime;

    private String updatedUser;

    private Date updatedTime;

    private Integer isDeleted;

    private List<SysMenuVO> children = new ArrayList<SysMenuVO>();

    private SysMenuBasicVO meta;

    private String mark;

}
