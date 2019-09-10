package com.dg.mall.system.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dg.mall.core.page.PageVO;
import com.dg.mall.system.entity.SysRole;
import com.dg.mall.system.entity.SysUser;
import com.dg.mall.system.vo.SysUserRoleVO;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author wlq
 * @since 2019-08-01
 */
public interface SysUserService extends IService<SysUser> {
    /**
     * 保存用户信息
     * @param user
     */
    public void saveUser(SysUserRoleVO userVO);

    /**
     * 更新用户信息
     * @param user 用户对象
     */
    public void updateUser(SysUserRoleVO userVO);


    /**
     * 删除用户信息
     * @param userId 用户id
     */
    public void deleteUser(Integer userId);


    /**
       * 查询用户信息（必要条件：以角色为条件查询）
     * @param userVO 用户vo
     * @return
     */
    public PageVO<SysUserRoleVO> queryUserByRoleId(SysUserRoleVO userVO);
    
    
    /**
         *  查询用户信息(以用户为维度)
     * @param userVO 用户vo
     * @return
     */
    public PageVO<SysUserRoleVO> queryUserList(SysUserRoleVO userVO);
    
    
    /**
     *  根据userid查询角色信息
     * @param userId
     * @return
     */
    public SysRole queryRoleByUserId(Integer userId);


    /**
     * 查询所有角色信息 / 或根据登录用人查询旗下角色信息
     * @param sysRole
     * @return
     */
    public List<SysRole> queryRolesByRoleCreateUser(SysRole sysRole);
    
    
    /**
      * 查询审核范围
     * @return
     */
    public List<Map<String, Object>>   selectScopeAudit();


    /**
     * 根据userid查询用户信息
     * @param userId
     * @return
     */
    public  SysUserRoleVO  queryUserByUserId(Integer userId);

}
