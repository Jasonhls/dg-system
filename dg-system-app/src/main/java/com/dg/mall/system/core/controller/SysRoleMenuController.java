package com.dg.mall.system.core.controller;

import com.dg.mall.core.reqres.response.ResponseData;
import com.dg.mall.core.reqres.response.SuccessResponseData;
import com.dg.mall.core.util.ValidationUtils;
import com.dg.mall.system.api.exception.enums.SystemExceptionEnum;
import com.dg.mall.system.entity.SysRoleMenu;
import com.dg.mall.system.service.SysRoleMenuService;
import com.dg.mall.system.vo.SysRoleMenuVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author mabo
 * @create 2019/8/5 16:44
 */
@RestController
@RequestMapping("/roleMenu")
public class SysRoleMenuController {

    @Autowired
    private SysRoleMenuService sysRoleMenuService;

     /**
     * 设置角色权限
     * @param sysRoleMenu
     * @return
     */
    @PutMapping
    public ResponseData updateRoleMenu(@RequestBody SysRoleMenuVO sysRoleMenu) {
        ValidationUtils.checkNotNull(sysRoleMenu.getRoleId(), SystemExceptionEnum.NULL_POINT_EXCEPTION.getCode(), "角色id");
        if (CollectionUtils.isEmpty(sysRoleMenu.getMenuIds())) {
            return SuccessResponseData.success();
        }
        sysRoleMenuService.updateRoleMenu(sysRoleMenu);
        return ResponseData.success();
    }

     /**
     * 查询角色权限
     * @param sysRoleMenu
     * @return
     */
    @GetMapping(value = "/getRoleMenu")
    public ResponseData getRoleMenu(@RequestBody SysRoleMenuVO sysRoleMenu) {
        ValidationUtils.checkNotNull(sysRoleMenu.getRoleId(), SystemExceptionEnum.NULL_POINT_EXCEPTION.getCode(), "角色id");
        List<SysRoleMenu> roleMenu = sysRoleMenuService.getRoleMenu(sysRoleMenu);
        return ResponseData.success(roleMenu);
    }

}
