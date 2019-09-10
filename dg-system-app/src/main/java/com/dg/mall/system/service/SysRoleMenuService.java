package com.dg.mall.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dg.mall.system.entity.SysRoleMenu;
import com.dg.mall.system.vo.SysRoleMenuVO;

import java.util.List;

/**
 * @author mabo
 * @create 2019/8/5 16:46
 */
public interface SysRoleMenuService extends IService<SysRoleMenu> {

    void updateRoleMenu(SysRoleMenuVO sysRole);

    List<SysRoleMenu> getRoleMenu(SysRoleMenuVO sysRole);
}
