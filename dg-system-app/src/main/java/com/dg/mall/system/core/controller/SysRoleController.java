package com.dg.mall.system.core.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dg.mall.core.page.PageVO;
import com.dg.mall.core.reqres.response.ResponseData;
import com.dg.mall.core.util.ValidationUtils;
import com.dg.mall.system.api.context.LoginUser;
import com.dg.mall.system.api.exception.enums.SystemExceptionEnum;
import com.dg.mall.system.context.LoginContext;
import com.dg.mall.system.entity.SysRole;
import com.dg.mall.system.service.SysRoleService;
import com.dg.mall.system.vo.req.RoleQueryVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @author helisen
 * @create 2019-08-01
 */
@RestController
@RequestMapping("/sysRole")
public class SysRoleController {

    @Autowired
    private SysRoleService roleService;

    @GetMapping(value = "/page")
    public ResponseData getRoleList(RoleQueryVO roleQueryVO){
        ValidationUtils.checkNotNull(roleQueryVO.getCurrent(), SystemExceptionEnum.NULL_POINT_EXCEPTION.getCode(),"当前页");
        ValidationUtils.checkNotNull(roleQueryVO.getSize(), SystemExceptionEnum.NULL_POINT_EXCEPTION.getCode(),"每页大小");
        PageVO pageVO = new PageVO(roleQueryVO.getCurrent(),roleQueryVO.getSize());
        IPage<SysRole> iPage = roleService.getRoleList(pageVO, roleQueryVO);
        return ResponseData.success(iPage);
    }

    @PostMapping
    public ResponseData saveRole(@RequestBody SysRole sysRole) {
        ValidationUtils.checkParam(StringUtils.isNotEmpty(sysRole.getRoleName()), SystemExceptionEnum.NULL_POINT_EXCEPTION.getCode(),"角色名");
        LoginUser loginUser = LoginContext.me().getLoginUser();
        sysRole.setCreatedUser(loginUser.getPhone());
        sysRole.setUpdatedUser(loginUser.getPhone());
        sysRole.setUpdatedTime(new Date());
        roleService.saveRole(sysRole);
        return ResponseData.success();
    }

    @PutMapping
    public ResponseData updateRole(@RequestBody SysRole sysRole) {
        ValidationUtils.checkNotNull(sysRole.getRoleId(), SystemExceptionEnum.NULL_POINT_EXCEPTION.getCode(),"角色id");
        ValidationUtils.checkParam(StringUtils.isNotEmpty(sysRole.getRoleName()), SystemExceptionEnum.NULL_POINT_EXCEPTION.getCode(),"角色名");
        roleService.updateRole(sysRole);
        return ResponseData.success();
    }

    @PutMapping(value = "/{roleId}")
    public ResponseData deleteRole(@PathVariable(value = "roleId") Integer roleId) {
        ValidationUtils.checkNotNull(roleId, SystemExceptionEnum.NULL_POINT_EXCEPTION.getCode(),"角色id");
        roleService.deleteRole(roleId);
        return ResponseData.success();
    }

    @GetMapping(value = "/{roleId}")
    public ResponseData search(@PathVariable("roleId") Integer roleId){
        SysRole sysRole = roleService.getRoleById(roleId);
        return ResponseData.success(sysRole);
    }
}

