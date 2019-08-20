package com.dg.mall.system.core.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dg.mall.core.util.ConvertBeanUtil;
import com.dg.mall.core.util.ValidationUtils;
import com.dg.mall.jwt.utils.JwtTokenUtil;
import com.dg.mall.model.exception.ServiceException;
import com.dg.mall.system.api.context.SysMenuBasicDTO;
import com.dg.mall.system.api.context.SysMenuDTO;
import com.dg.mall.system.api.exception.enums.SystemExceptionEnum;
import com.dg.mall.system.api.resp.UserMenuResp;
import com.dg.mall.system.entity.SysMenu;
import com.dg.mall.system.entity.SysRole;
import com.dg.mall.system.entity.SysUser;
import com.dg.mall.system.mapper.SysMenuMapper;
import com.dg.mall.system.service.SysMenuService;
import com.dg.mall.system.service.SysRoleService;
import com.dg.mall.system.service.SysUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author mabo
 * @create 2019/8/2 9:09
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {


    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysMenuService sysMenuService;

    @Autowired
    private SysUserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    @Override
    public List<SysMenuDTO> getMenuTree(List<Integer> tmpRoleIds) {
        QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted", "0");
        List<SysMenu> sysMenus = sysMenuMapper.selectList(queryWrapper);
        List<SysMenuDTO> result = parseMenuTree(sysMenus);
        // 查询个人权限树
        if (CollectionUtil.isNotEmpty(tmpRoleIds)) {
            List<SysMenu> menus = sysMenuService.selectUrlsByRoleId(tmpRoleIds);
            for (SysMenuDTO sysMenuAll : result) {
                recursiveRoleTree(sysMenuAll, menus);
            }
        }
        return result;
    }

    public void recursiveRoleTree(SysMenuDTO parent, List<SysMenu> list) {
        for (SysMenu menu : list) {
            if (parent.getId().equals(menu.getId())){
                //权限树权限回显
                parent.setPermission(1);
            }
            if (!CollectionUtils.isEmpty(parent.getChildren())){
                List<SysMenuDTO> children = parent.getChildren();
                for (SysMenuDTO sysMenuDTO:children) {
                    if (sysMenuDTO.getId().equals(menu.getId())) {
                        sysMenuDTO.setPermission(1);
                    }
                    recursiveRoleTree(sysMenuDTO, list);
                }
            }
        }
    }
    @Override
    public List<SysMenuDTO> userMenuTree(String phone) {
        Optional.ofNullable(phone).orElseThrow(() -> new ServiceException(SystemExceptionEnum.USER_NOT_FOUND));
        // 查询启用用户角色的信息
        SysUser sysUser = userService.getOne(new QueryWrapper<SysUser>().lambda().eq(SysUser::getPhone, Integer.valueOf(phone)).eq(SysUser::getIsDeleted, false));
        ValidationUtils.checkParam(sysUser != null, SystemExceptionEnum.USER_NOT_FOUND.getCode());
        List<SysRole> roles = sysRoleService.selectRolesByUserId(sysUser.getUserId());
        Optional.ofNullable(roles).orElseThrow(() -> new ServiceException(SystemExceptionEnum.USER_NOT_FOUND));
        List<SysMenuDTO> result = new ArrayList<>(roles.size());
        List<Integer> tmpRoleIds = new ArrayList<>(roles.size());
        roles.forEach(r -> {
            tmpRoleIds.add(r.getRoleId());
        });

        // 查询个人权限树
        if (CollectionUtil.isNotEmpty(tmpRoleIds)) {
            List<SysMenu> menus = sysMenuService.selectUrlsByRoleId(tmpRoleIds);
            if (CollectionUtil.isNotEmpty(menus)) {
                result = parseMenuTree(menus);
            }
        }
        return result;
    }

    @Override
    public UserMenuResp getUserMenuByToken(String token) {
        String userId;
        try {
            userId = jwtTokenUtil.getUserIdFromToken(token);
        } catch (Exception e) {
            throw new ServiceException(SystemExceptionEnum.USER_NOT_FOUND);
        }
        Optional.ofNullable(userId).orElseThrow(() -> new ServiceException(SystemExceptionEnum.USER_NOT_FOUND));
        // 查询用户信息
        SysUser sysUser = userService.getOne(new QueryWrapper<SysUser>().lambda().eq(SysUser::getUserId, Integer.valueOf(userId)).eq(SysUser::getIsDeleted, false).eq(SysUser::getIsUsed,true));
        ValidationUtils.checkParam(sysUser != null, SystemExceptionEnum.USER_NOT_FOUND.getCode());
        // 查询启用用户的信息
        List<SysRole> roles = sysRoleService.selectRolesByUserId(Integer.valueOf(userId));
        ValidationUtils.checkParam(!CollectionUtils.isEmpty(roles), SystemExceptionEnum.COMMENT_SERVICE_EXCEPTION.getCode(),"用户未关联任何角色");
//        Optional.ofNullable(roles).orElseThrow(() -> new ServiceException(SystemExceptionEnum.USER_NOT_FOUND));
        List<SysMenuDTO> result = new ArrayList<>();
        List<Integer> tmpRoleIds = new ArrayList<>();
        roles.forEach(r -> {
            tmpRoleIds.add(r.getRoleId());
        });
        List<SysMenu> sysMenus = sysMenuMapper.selectUrlsByRoleId(tmpRoleIds);
        ConvertBeanUtil.copyListBeanPropertiesToList(sysMenus,result, SysMenuDTO.class);
        UserMenuResp resp = new UserMenuResp();
        resp.setUserName(sysUser.getName());
        resp.setList(result);
        return resp;
    }



    @Override
    public List<SysMenu> selectUrlsByRoleId(List<Integer> roleIds) {
        return sysMenuMapper.selectUrlsByRoleId(roleIds);
    }



    /**
     * @方法名: parseMenuTree<br>
     * @描述: 组装菜单<br>
     * @param list 数据库里面获取到的全量菜单列表
     * @return
     */
    public static List<SysMenuDTO> parseMenuTree(List<SysMenu> list){
        List<SysMenuDTO> result = new ArrayList<SysMenuDTO>();
        // 1、获取第一级节点
        for (SysMenu sysMenu : list) {
            if(sysMenu.getParentId().intValue()==0) {
                SysMenuDTO sysMenuDTO=  new SysMenuDTO();
                BeanUtils.copyProperties(sysMenu,sysMenuDTO);
                sysMenuDTO.setMeta(new SysMenuBasicDTO(sysMenu.getTitle(),sysMenu.getIcon()));
                result.add(sysMenuDTO);
            }
        }
        // 2、递归获取子节点
        for (SysMenuDTO parent : result) {
            parent = recursiveTree(parent, list);
        }
        return result;
    }

    public static SysMenuDTO recursiveTree(SysMenuDTO parent, List<SysMenu> list) {
        for (SysMenu menu : list) {
            if(parent.getId().equals(menu.getParentId())) {
                SysMenuDTO sysMenuDTO=  new SysMenuDTO();
                BeanUtils.copyProperties(menu,sysMenuDTO);
                sysMenuDTO.setMeta(new SysMenuBasicDTO(menu.getTitle(),menu.getIcon()));
                sysMenuDTO = recursiveTree(sysMenuDTO, list);
                parent.getChildren().add(sysMenuDTO);
            }
        }
        return parent;
    }


    @Override
    public void saveMenu(SysMenu sysMenu) {
        QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(SysMenu::getTitle,sysMenu.getTitle())
                .eq(SysMenu::getParentId,sysMenu.getParentId())
                .eq(SysMenu::getIsDeleted,0);
        SysMenu menu = this.checkMenuName(queryWrapper);
        if(menu == null){
            sysMenuMapper.insert(sysMenu);
        }

    }

    @Override
    public void updateMenu(SysMenu sysMenu) {
        QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(SysMenu::getTitle,sysMenu.getTitle())
                .eq(SysMenu::getIsDeleted,0)
                .eq(SysMenu::getParentId,sysMenu.getParentId())
                .ne(SysMenu::getId,sysMenu.getId());
        SysMenu menu = this.checkMenuName(queryWrapper);
        if(menu == null){
            sysMenuMapper.updateById(sysMenu);
        }
    }

    private SysMenu checkMenuName(QueryWrapper<SysMenu> queryWrapper){
        SysMenu sysMenu = sysMenuMapper.selectOne(queryWrapper);
        ValidationUtils.checkParam(sysMenu == null, SystemExceptionEnum.COMMENT_SERVICE_EXCEPTION.getCode(),"相同父菜单下菜单名重复");
        if(sysMenu == null){
            return sysMenu;
        }
        return null;
    }
}
