package com.dg.mall.system.core.vo;

import lombok.Data;

/**
 * @author mabo
 * @create 2019/8/7 15:14
 */
@Data
public class SysMenuBasicVO {

    private String title;

    private String icon;

    public SysMenuBasicVO(String title, String icon) {
        this.title = title;
        this.icon = icon;
    }
}
