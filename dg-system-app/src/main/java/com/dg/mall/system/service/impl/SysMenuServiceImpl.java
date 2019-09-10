package com.dg.mall.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dg.mall.core.util.ValidationUtils;
import com.dg.mall.jwt.utils.JwtTokenUtil;
import com.dg.mall.model.exception.ServiceException;
import com.dg.mall.system.api.context.LoginUser;
import com.dg.mall.system.api.context.SysMenuBasicDTO;
import com.dg.mall.system.api.context.SysMenuDTO;
import com.dg.mall.system.api.context.SysRoleDTO;
import com.dg.mall.system.api.exception.enums.SystemExceptionEnum;
import com.dg.mall.system.context.LoginContext;
import com.dg.mall.system.core.constants.SystemConstants;
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

import java.util.*;
import java.util.stream.Collectors;

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


    @Override
    public List<SysMenuDTO> getMenuTree(List<Integer> tmpRoleIds) {
        QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted", "0");
        List<SysMenu> sysMenus = new ArrayList<>();
        LoginUser loginUser = LoginContext.me().getLoginUser();
        SysRoleDTO sr = loginUser.getRoles().get(0);
        //超级管理员
        if (SystemConstants.SUPER_MANAGER.equals(sr.getRoleName())) {
            sysMenus = sysMenuMapper.selectList(queryWrapper);

        } else {
            //普通管理员
            List<Integer> roles = new ArrayList<>();
            roles.add(sr.getRoleId());
            List<SysMenu> menus = sysMenuService.selectUrlsByRoleId(roles);
            sysMenus = getMenuList(menus);
        }
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
        ValidationUtils.checkParam(sysMenu == null, SystemExceptionEnum.REPEAT_EXCEPTION.getCode(),"相同父菜单下菜单名");
        if(sysMenu == null){
            return sysMenu;
        }
        return null;
    }

    @Override
    public List<SysMenu> getMenuList(List<SysMenu> menus) {
        //判断节点ID 是否包含父节点，如果不包含手动添加
        List<Integer> collect = menus.stream().map(SysMenu::getId).collect(Collectors.toList());
        Set<Integer> ids = new HashSet<>();
        ids.addAll(collect);
        menus.forEach(sysMenuParam -> {
            Integer parentId = sysMenuParam.getParentId();
            while(parentId != null && !ids.contains(parentId)){
                ids.add(parentId);
                QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(SysMenu::getId,parentId).eq(SysMenu::getIsDeleted,false);
                SysMenu sm = sysMenuService.getOne(queryWrapper);
                if(sm == null){
                    break;
                }
                parentId = sm.getParentId();
            }
        });
        List<SysMenu> sysMenus = (List<SysMenu>)sysMenuService.listByIds(ids);
        return  sysMenus;
        }
    }
