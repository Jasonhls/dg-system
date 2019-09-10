package com.dg.mall.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dg.mall.core.util.ValidationUtils;
import com.dg.mall.jwt.utils.JwtTokenUtil;
import com.dg.mall.model.enums.YesOrNotEnum;
import com.dg.mall.model.exception.ServiceException;
import com.dg.mall.system.api.context.LoginUser;
import com.dg.mall.system.api.context.SysMenuDTO;
import com.dg.mall.system.api.context.SysRoleDTO;
import com.dg.mall.system.api.exception.enums.SystemExceptionEnum;
import com.dg.mall.system.core.constants.SystemConstants;
import com.dg.mall.system.entity.SysMenu;
import com.dg.mall.system.entity.SysRole;
import com.dg.mall.system.entity.SysUser;
import com.dg.mall.system.service.LoginUserService;
import com.dg.mall.system.service.SysMenuService;
import com.dg.mall.system.service.SysRoleService;
import com.dg.mall.system.service.SysUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description: 获取当前登录用户
 * @author: wangqiang
 * @create2019-08-09 09:14
 **/
@Service
public class LoginUserServiceImImpl implements LoginUserService {

    @Autowired
    private SysUserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysMenuService sysMenuService;


    @Override
    public LoginUser getLoginUserByToken(String token) {
        LoginUser user = new LoginUser();
        String userId;
        try {
            userId = jwtTokenUtil.getUserIdFromToken(token);
        }catch (Exception e){
            throw new ServiceException(SystemExceptionEnum.USER_NOT_FOUND);
        }
        Optional.ofNullable(userId).orElseThrow(() -> new ServiceException(SystemExceptionEnum.USER_NOT_FOUND));
        // 查询启用用户的信息
        SysUser sysUser = userService.getOne(new QueryWrapper<SysUser>().lambda().eq(SysUser::getUserId,Integer.valueOf(userId)).eq(SysUser::getIsDeleted,false));
        BeanUtils.copyProperties(sysUser,user);
        List<SysRole> roles = sysRoleService.selectRolesByUserId(Integer.valueOf(userId));
        ValidationUtils.checkParam(!CollectionUtils.isEmpty(roles),SystemExceptionEnum.COMMENT_SERVICE_EXCEPTION.getCode(),"用户未关联任何角色");
        List<SysMenuDTO> result = new ArrayList<>(roles.size());
        List<Integer> tmpRoleIds = new ArrayList<>(roles.size());
        List<SysRoleDTO> sysRoleDTOS = new ArrayList<>();
        try {
            roles.forEach(r -> {
                //如果用户拥有超级管理员角色，修改标示位
                if(SystemConstants.SUPER_MANAGER.equals(r.getRoleName())){
                    user.setSuperAdministrator(YesOrNotEnum.Y.getCode());
                }
                user.setRoleId(r.getRoleId());
                user.setRoleName(r.getRoleName());
                sysRoleDTOS.add(new SysRoleDTO(r.getRoleId(), r.getRoleName(), r.getRoleDescription()));
                tmpRoleIds.add(r.getRoleId());
            });

            // 查询个人权限树
            if (CollectionUtil.isNotEmpty(tmpRoleIds)) {
                List<SysMenu> menus = sysMenuService.selectUrlsByRoleId(tmpRoleIds);
                List<SysMenu> sysMenus = sysMenuService.getMenuList(menus);
                if (CollectionUtil.isNotEmpty(sysMenus)) {
                    result = SysMenuServiceImpl.parseMenuTree(sysMenus);
                }
            }
            user.setRoles(sysRoleDTOS);
            user.setMenus(result);
        } catch (Exception e) {
            throw new ServiceException(SystemExceptionEnum.SERVICE_ERROR.getCode(),"获取个人权限异常");

        }
        return user;
    }
}
