package com.dg.mall.system.core.controller;

import com.dg.mall.core.page.PageVO;
import com.dg.mall.core.reqres.response.ResponseData;
import com.dg.mall.system.entity.SysRole;
import com.dg.mall.system.service.SysUserService;
import com.dg.mall.system.vo.SysUserRoleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author wlq
 * @since 2019-08-01
 */
@RestController
@RequestMapping("/sysUser")
public class SysUserController {

    
    @Autowired
	private SysUserService sysUserService;
    
	
	/**
	 *     保存用户信息
	 * @param user
	 * @return
	 */
    @PostMapping
	public ResponseData saveUser(@RequestBody SysUserRoleVO userVO) {
		sysUserService.saveUser(userVO);
		return ResponseData.success();
	}
	
	
	/**
	 *     更新用户信息
	 * @return
	 */
    @PutMapping
	public ResponseData updateUserByPhone(@RequestBody SysUserRoleVO userVO) {
		sysUserService.updateUser(userVO);
		return ResponseData.success();
	}
	
	
	/**
	 *     删除用户信息
	 * @return
	 */
    @DeleteMapping(value = "/{userId}")
	public ResponseData updateUserByPhone(@PathVariable(value = "userId") Integer userId) {
		sysUserService.deleteUser(userId);
		return ResponseData.success();
	}
	
	/**
	 *      查询用户信息(条件查询)
	 * @param userVO
	 * @return
	 */
    @GetMapping(value = "/page")
	public ResponseData  queryUserInfo(SysUserRoleVO userVO){
		PageVO<SysUserRoleVO>  page =null;
		page = sysUserService.queryUserByRoleId(userVO);
		
		return ResponseData.success(page);
	}
	
    
    /**
        *根据userid查询角色信息
     * @param userId
     * @return
     */
    @GetMapping(value = "/role/{userId}")
    public ResponseData queryRolesByUserId(@PathVariable Integer userId) {
    	SysRole lsRole = sysUserService.queryRoleByUserId(userId);
    	return ResponseData.success(lsRole);
    }
    
    
	/**
	     *查询所有角色信息 / 或根据登录用人查询旗下角色信息
	  * @param userId
	  * @return
	  */
	 @GetMapping(value = "/roles")
	 public ResponseData queryRoles(SysRole sysRole) {
	 	List<SysRole> lsRole = sysUserService.queryRolesByRoleCreateUser(sysRole);
	 	return ResponseData.success(lsRole);
	 }
	 
	 
    /**
     * 根据userid查询用户信息
     * @param userId
     * @return
     */
    @GetMapping(value = "user/{userId}")
    public ResponseData queryUserByUserId(@PathVariable Integer userId) {
    	return ResponseData.success(sysUserService.queryUserByUserId(userId));
    }
    
    
    /**
      *  查询审核范围
     * @return
     */
    @GetMapping(value = "/scopeAudit")
    public ResponseData selectScopeAudit() {
    	//目前写死，后期从数据字段表中取
    	List<Map<String, Object>>  ls = sysUserService.selectScopeAudit();
    	return ResponseData.success(ls);
    }
    
}

