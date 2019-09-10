package com.dg.mall.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dg.mall.system.api.context.SysMenuDTO;
import com.dg.mall.system.api.resp.UserMenuResp;
import com.dg.mall.system.entity.SysMenu;
import com.dg.mall.system.vo.SysMenuVO;

import java.util.List;

/**
 * @author mabo
 * @create 2019/8/2 9:08
 */
public interface SysMenuService  extends IService<SysMenu> {

    List<SysMenuDTO> getMenuTree(List<Integer> tmpRoleIds);

    List<SysMenu> selectUrlsByRoleId(List<Integer> roleIds);

    void saveMenu(SysMenu sysMenu);

    void updateMenu(SysMenu sysMenu);

    List<SysMenu> getMenuList(List<SysMenu> sysMenus);
}
