package com.dg.mall.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dg.mall.core.reqres.response.ResponseData;
import com.dg.mall.core.util.ValidationUtils;
import com.dg.mall.logger.config.SysLog;
import com.dg.mall.system.api.context.LoginUser;
import com.dg.mall.system.api.context.SysMenuDTO;
import com.dg.mall.system.api.exception.enums.SystemExceptionEnum;
import com.dg.mall.system.context.LoginContext;
import com.dg.mall.system.entity.SysMenu;
import com.dg.mall.system.service.SysMenuService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


/**
 * @author mabo
 * @create 2019/8/2 9:07
 */
@RestController
@RequestMapping("/menu")
public class SysMenuController {

    @Autowired
    private SysMenuService sysMenuService;


    /**
     * 获取菜单树loginUser
     * @return
     */
    @GetMapping("/menuTree")
    public ResponseData getMenuTree(@RequestParam("tmpRoleIds")List<Integer> tmpRoleIds) {
        List<SysMenuDTO> menuTree = sysMenuService.getMenuTree(tmpRoleIds);
        return ResponseData.success(menuTree);
    }

    /**
     * 获取菜单详情
     * @param sysMenu
     * @return
     */
    @GetMapping("/detail")
    @SysLog(description = "获取菜单详情")
    public ResponseData getMenu(@RequestBody SysMenu sysMenu) {
        QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>(sysMenu);
        SysMenu one = sysMenuService.getOne(queryWrapper);
        return ResponseData.success(one);
    }


     /**
     * 新增菜单
     * @param sysMenu
     * @return
     */
    @PostMapping
    public ResponseData saveRole(@RequestBody SysMenu sysMenu) {
        ValidationUtils.checkParam(StringUtils.isNotEmpty(sysMenu.getTitle()), SystemExceptionEnum.NULL_POINT_EXCEPTION.getCode(), "菜单名");
        ValidationUtils.checkParam(sysMenu.getParentId() != null, SystemExceptionEnum.NULL_POINT_EXCEPTION.getCode(), "上一级菜单");
        LoginUser loginUser = LoginContext.me().getLoginUser();
        sysMenu.setCreatedUser(loginUser.getPhone());
        sysMenuService.saveMenu(sysMenu);
        return ResponseData.success();
    }

     /**
     * 修改菜单
     * @param sysMenu
     * @return
     */
    @PutMapping
    public ResponseData updateRole(@RequestBody SysMenu sysMenu) {
        ValidationUtils.checkNotNull(sysMenu.getId(), SystemExceptionEnum.NULL_POINT_EXCEPTION.getCode(), "菜单id");
        LoginUser loginUser = LoginContext.me().getLoginUser();
        sysMenu.setUpdatedUser(loginUser.getPhone());
        sysMenu.setUpdatedTime(new Date());
        sysMenuService.updateMenu(sysMenu);
        return ResponseData.success();
    }

     /**
     * 删除菜单
     * @param menuId
     * @return
     */
    @DeleteMapping(value = "/{menuId}")
    public ResponseData deleteRole(@PathVariable(value = "menuId") Integer menuId) {
        ValidationUtils.checkNotNull(menuId, SystemExceptionEnum.NULL_POINT_EXCEPTION.getCode(), "菜单id");
        SysMenu sysMenu = new SysMenu();
        sysMenu.setId(menuId);
        sysMenu.setIsDeleted(1);
        sysMenuService.updateById(sysMenu);
        return ResponseData.success();
    }

}
