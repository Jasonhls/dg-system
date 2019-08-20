package com.dg.mall.system.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dg.mall.core.page.PageVO;
import com.dg.mall.system.entity.SysRole;
import com.dg.mall.system.entity.SysUserRole;
import com.dg.mall.system.vo.SysUserRoleVO;
import feign.Param;

import java.util.List;

/**
 * <p>
 * 用户角色关联表 Mapper 接口
 * </p>
 *
 * @author wlq
 * @since 2019-08-01
 */
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {
	/**
	 * 查询用户信息（必要条件：以角色为条件查询）
	 * @param page
	 * @param userVO
	 * @return
	 */
    List<SysUserRoleVO> selectUserPageByRoleId(PageVO<SysUserRoleVO> page, @Param("userVO") SysUserRoleVO userVO);

    /**
              *  查询用户信息(以用户为维度)
     * @param page
     * @param userVO
     * @return
     */
    List<SysUserRoleVO> queryUserListPage(PageVO<SysUserRoleVO> page, @Param("userVO") SysUserRoleVO userVO);
    
    
    /**
         *    根据角色查询是否已存在普通管理员的用户
     * @param userVO
     * @return
     */
    List<SysUserRoleVO> queryUserListByRoleId(@Param("userVO") SysUserRoleVO userVO);
	
	
	
    /**
        * 更新用户对应的角色
     * @param userVO
     * @return
     */
	int updateUserRoleByUserId(@Param("userVO") SysUserRoleVO userVO);
	
	
	/**
	 * 根据userid查询角色信息
	 * @param userVO
	 * @return
	 */
	SysRole selectRoleByUserId(@Param("userId") Integer userId);
	
	
	/**
	 * 查询所有角色信息 / 或根据登录用人查询旗下角色信息
	 * @param sysRole
	 * @return
	 */
	List<SysRole> queryRolesByRoleCreateUser(@Param("sysRole") SysRole sysRole);
	
	
    /**
     * 根据userid查询用户信息	
     * @param userId
     * @return
     */
	SysUserRoleVO queryUserByUserId(@Param("userId") Integer userId);

	/**
	 * 根据角色id，翻转用户的启用状态
	 * @param sysRole
	 */
	void switchIsUsedByRoleId(@Param("sysRole") SysRole sysRole);
}
