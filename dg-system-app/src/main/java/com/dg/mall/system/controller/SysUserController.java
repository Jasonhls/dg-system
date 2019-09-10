package com.dg.mall.system.controller;

import com.dg.mall.core.page.PageVO;
import com.dg.mall.core.reqres.response.ResponseData;
import com.dg.mall.logger.config.SysLog;
import com.dg.mall.system.entity.SysRole;
import com.dg.mall.system.service.SysUserService;
import com.dg.mall.system.vo.SysUserRoleVO;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
	@SysLog(description = "成员管理-添加系统用户",keyParam = "phone",paramDescription = "手机号码")
	public ResponseData saveUser(@RequestBody SysUserRoleVO userVO) {
		sysUserService.saveUser(userVO);
		return ResponseData.success();
	}
	
	
	/**
	 *     更新用户信息
	 * @return
	 */
    @PutMapping
	@SysLog(description = "成员管理-更新系统用户",keyParam = "userId",paramDescription = "用户id")
	public ResponseData updateUserByPhone(@RequestBody SysUserRoleVO userVO) {
		sysUserService.updateUser(userVO);
		return ResponseData.success();
	}
	
	
	/**
	 *     删除用户信息
	 * @return
	 */
    @DeleteMapping(value = "/{userId}")
	@SysLog(description = "成员管理-删除系统用户",keyParam = "userId",paramDescription = "用户id")
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
	@SysLog(description = "分页查询系统用户信息")
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
	@SysLog(description = "成员管理-根据userid查询角色信息")
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
	 @SysLog(description = "查询当前用户创建的所有角色信息")
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
	@SysLog(description = "成员管理-根据userid查询用户信息")
    public ResponseData queryUserByUserId(@PathVariable Integer userId) {
    	return ResponseData.success(sysUserService.queryUserByUserId(userId));
    }
    
    
    /**
      *  查询审核范围
     * @return
     */
    @GetMapping(value = "/scopeAudit")
	@SysLog(description = "成员管理-查询审核范围")
    public ResponseData selectScopeAudit() {
    	//目前写死，后期从数据字段表中取
    	List<Map<String, Object>>  ls = sysUserService.selectScopeAudit();
    	return ResponseData.success(ls);
    }
    
}

