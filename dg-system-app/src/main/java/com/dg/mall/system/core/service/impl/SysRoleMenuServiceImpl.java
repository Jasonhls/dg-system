package com.dg.mall.system.core.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dg.mall.core.util.ValidationUtils;
import com.dg.mall.system.api.context.LoginUser;
import com.dg.mall.system.api.exception.enums.SystemExceptionEnum;
import com.dg.mall.system.context.LoginContext;
import com.dg.mall.system.entity.SysRoleMenu;
import com.dg.mall.system.mapper.SysRoleMenuMapper;
import com.dg.mall.system.service.SysMenuService;
import com.dg.mall.system.service.SysRoleMenuService;
import com.dg.mall.system.vo.SysRoleMenuVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author mabo
 * @create 2019/8/5 16:46
 */
@Service
public class SysRoleMenuServiceImpl  extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu> implements SysRoleMenuService {
    @Autowired
    private  SysRoleMenuMapper sysRoleMenuMapper;

    @Autowired
    private SysMenuService sysMenuService;

    @Override
    public void updateRoleMenu(SysRoleMenuVO sysRoleMenuVO) {
        //先删除该角色下的权限信息
        Map<String, Object> columnMap = new HashMap<String, Object>();
        columnMap.put("role_id", sysRoleMenuVO.getRoleId());
        LoginUser loginUser = LoginContext.me().getLoginUser();
        ValidationUtils.checkParam(!CollectionUtils.isEmpty(loginUser.getRoles()), SystemExceptionEnum.COMMENT_SERVICE_EXCEPTION.getCode(), "用户角色未开启");
        ValidationUtils.checkParam(!loginUser.getRoles().get(0).getRoleId().equals(sysRoleMenuVO.getRoleId()), SystemExceptionEnum.COMMENT_SERVICE_EXCEPTION.getCode(), "不能修改自身的角色权限");
        int result = sysRoleMenuMapper.deleteByMap(columnMap);

        //判断前端传递的节点ID 是否包含父节点，如果不包含手动添加
//        List<String> ids= new ArrayList<>();
//        sysRoleMenuVO.getMenuIds().forEach(menuId -> {
//            SysMenu sysMenuParam = new SysMenu();
//            sysMenuParam.setId(Integer.valueOf(menuId));
//            QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>(sysMenuParam);
//            SysMenu sysMenu = sysMenuService.getOne(queryWrapper);
//            if (sysMenu != null && !sysRoleMenuVO.getMenuIds().contains(sysMenu.getParentId().toString())) {
//                ids.add(sysMenu.getParentId().toString());
//            }
//        });
//        ids.addAll(sysRoleMenuVO.getMenuIds());
        //重新生成该角色下的权限信息
        sysRoleMenuVO.getMenuIds().forEach(menuId -> {
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setMenuId(Integer.valueOf(menuId));
            sysRoleMenu.setRoleId(sysRoleMenuVO.getRoleId());
            sysRoleMenu.setCreatedTime(new Date());
            sysRoleMenu.setCreatedUser(loginUser.getPhone());
            sysRoleMenuMapper.insert(sysRoleMenu);
        });
    }

    @Override
    public List<SysRoleMenu> getRoleMenu(SysRoleMenuVO sysRoleMenuVO) {
        Map<String, Object> columnMap = new HashMap<String, Object>();
        columnMap.put("role_id", sysRoleMenuVO.getRoleId());
        List<SysRoleMenu> sysRoleMenus = sysRoleMenuMapper.selectByMap(columnMap);
        return sysRoleMenus;
    }
}
