package com.dg.mall.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dg.mall.core.page.PageVO;
import com.dg.mall.system.api.context.SysRoleDTO;
import com.dg.mall.system.entity.SysRole;
import com.dg.mall.system.vo.req.RoleQueryVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jobob
 * @since 2019-08-01
 */
public interface SysRoleService extends IService<SysRole> {

    IPage<SysRole> getRoleList(PageVO pageVO, RoleQueryVO roleQueryVO);

    void saveRole(SysRole sysRole);

    void updateRole(SysRole sysRole);

    void deleteRole(Integer roleId);

    SysRole getRoleById(Integer roleId);

    List<SysRole> selectRolesByUserId(Integer userId);

    List<SysRoleDTO> getRoleTreee(RoleQueryVO roleQueryVO);
}
