package com.dg.mall.system.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dg.mall.core.page.PageVO;
import com.dg.mall.core.reqres.response.ResponseData;
import com.dg.mall.core.util.ValidationUtils;
import com.dg.mall.logger.config.SysLog;
import com.dg.mall.system.api.context.SysRoleDTO;
import com.dg.mall.system.api.exception.enums.SystemExceptionEnum;
import com.dg.mall.system.entity.SysRole;
import com.dg.mall.system.service.SysRoleService;
import com.dg.mall.system.vo.req.RoleQueryVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
    @SysLog(description = "分页查询系统角色")
    public ResponseData getRoleList(RoleQueryVO roleQueryVO){
        ValidationUtils.checkNotNull(roleQueryVO.getCurrent(),SystemExceptionEnum.NULL_POINT_EXCEPTION.getCode(),"当前页");
        ValidationUtils.checkNotNull(roleQueryVO.getSize(),SystemExceptionEnum.NULL_POINT_EXCEPTION.getCode(),"每页大小");
        PageVO pageVO = new PageVO(roleQueryVO.getCurrent(),roleQueryVO.getSize());
        IPage<SysRole> iPage = roleService.getRoleList(pageVO, roleQueryVO);
        return ResponseData.success(iPage);
    }

    @PostMapping
    @SysLog(description = "添加系统角色",keyParam = "roleName",paramDescription = "角色名称")
    public ResponseData saveRole(@RequestBody SysRole sysRole) {
        ValidationUtils.checkParam(StringUtils.isNotEmpty(sysRole.getRoleName()),SystemExceptionEnum.NULL_POINT_EXCEPTION.getCode(),"角色名");
        ValidationUtils.checkNotNull(sysRole.getParentRoleId(),SystemExceptionEnum.NULL_POINT_EXCEPTION.getCode(),"父角色id");
        roleService.saveRole(sysRole);
        return ResponseData.success();
    }

    @PutMapping
    @SysLog(description = "更新系统角色",keyParam = "roleId",paramDescription = "角色id")
    public ResponseData updateRole(@RequestBody SysRole sysRole) {
        ValidationUtils.checkNotNull(sysRole.getRoleId(),SystemExceptionEnum.NULL_POINT_EXCEPTION.getCode(),"角色id");
        ValidationUtils.checkParam(StringUtils.isNotEmpty(sysRole.getRoleName()),SystemExceptionEnum.NULL_POINT_EXCEPTION.getCode(),"角色名");
        ValidationUtils.checkNotNull(sysRole.getParentRoleId(),SystemExceptionEnum.NULL_POINT_EXCEPTION.getCode(),"父角色id");
        SysRole roleById = roleService.getRoleById(sysRole.getRoleId());
        ValidationUtils.checkParam(roleById != null && sysRole.getParentRoleId().equals(roleById.getParentRoleId()),
                SystemExceptionEnum.COMMENT_SERVICE_EXCEPTION.getCode(),"父角色id不能修改");
        roleService.updateRole(sysRole);
        return ResponseData.success();
    }

    @PutMapping(value = "/{roleId}")
    @SysLog(description = "删除系统角色", keyParam = "roleId", paramDescription = "角色id")
    public ResponseData deleteRole(@PathVariable(value = "roleId") Integer roleId) {
        ValidationUtils.checkNotNull(roleId,SystemExceptionEnum.NULL_POINT_EXCEPTION.getCode(),"角色id");
        roleService.deleteRole(roleId);
        return ResponseData.success();
    }

    @GetMapping(value = "/{roleId}")
    @SysLog(description = "查询系统角色",keyParam = "roleId",paramDescription = "角色id")
    public ResponseData search(@PathVariable("roleId") Integer roleId){
        SysRole sysRole = roleService.getRoleById(roleId);
        return ResponseData.success(sysRole);
    }

    /**
     * 查询角色树
     * @param roleQueryVO
     * @return
     */
    @GetMapping(value = "/tree")
    public ResponseData getRoleTree(RoleQueryVO roleQueryVO){
        List<SysRoleDTO> roleTreee = roleService.getRoleTreee(roleQueryVO);
        return ResponseData.success(roleTreee == null ? new ArrayList<SysRoleDTO>() : roleTreee);
    }

}

