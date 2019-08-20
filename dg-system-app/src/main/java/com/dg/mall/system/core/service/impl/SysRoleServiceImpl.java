package com.dg.mall.system.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dg.mall.core.page.PageVO;
import com.dg.mall.core.util.ValidationUtils;
import com.dg.mall.system.api.context.LoginUser;
import com.dg.mall.system.api.exception.enums.SystemExceptionEnum;
import com.dg.mall.system.context.LoginContext;
import com.dg.mall.system.entity.SysRole;
import com.dg.mall.system.entity.SysRoleMenu;
import com.dg.mall.system.entity.SysUserRole;
import com.dg.mall.system.mapper.SysRoleMapper;
import com.dg.mall.system.mapper.SysRoleMenuMapper;
import com.dg.mall.system.mapper.SysUserRoleMapper;
import com.dg.mall.system.service.SysRoleService;
import com.dg.mall.system.vo.req.RoleQueryVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jobob
 * @since 2019-08-01
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;
    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Override
    public IPage<SysRole> getRoleList(PageVO pageVO, RoleQueryVO roleQueryVO) {
        QueryWrapper<SysRole> queryWrapper = new QueryWrapper<>();
        if(roleQueryVO.getRoleId() != null){
            queryWrapper.lambda().eq(roleQueryVO.getRoleId() != null,SysRole::getRoleId, roleQueryVO.getRoleId());
        }else{
            queryWrapper.lambda()
                .ge(roleQueryVO.getStartTime() != null,SysRole::getCreatedTime,roleQueryVO.getStartTime())
                .le(roleQueryVO.getEndTime() != null,SysRole::getCreatedTime,roleQueryVO.getEndTime())
                .and(StringUtils.isNoneBlank(roleQueryVO.getKeyWords()),wrapper ->
                        wrapper.like(SysRole::getRoleName, roleQueryVO.getKeyWords())
                        .or().like(SysRole::getRoleDescription, roleQueryVO.getKeyWords())
                );
        }
        queryWrapper.lambda().eq(StringUtils.isNoneBlank(roleQueryVO.getCreatedUser()),SysRole::getCreatedUser, roleQueryVO.getCreatedUser())
                    .eq(SysRole::getIsDeleted,false)
                    .orderByDesc(SysRole::getUpdatedTime);
        return sysRoleMapper.selectPage(pageVO,queryWrapper);
    }

    @Override
    public void saveRole(SysRole sysRole) {
        QueryWrapper<SysRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(SysRole::getRoleName,sysRole.getRoleName())
                .eq(SysRole::getIsDeleted,false);
        SysRole role = this.checkRoleName(queryWrapper);
        if(role == null){
            sysRoleMapper.insert(sysRole);
        }
    }

    private SysRole checkRoleName(QueryWrapper<SysRole> queryWrapper){
        SysRole role = sysRoleMapper.selectOne(queryWrapper);
        ValidationUtils.checkParam(role == null, SystemExceptionEnum.COMMENT_SERVICE_EXCEPTION.getCode(),"该角色已经存在");
        if(role == null){
            return role;
        }
        return null;
    }

    @Override
    public void updateRole(SysRole sysRole) {
        LoginUser loginUser = LoginContext.me().getLoginUser();
        sysRole.setUpdatedUser(loginUser.getPhone());
        sysRole.setUpdatedTime(new Date());
        QueryWrapper<SysRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(SysRole::getRoleName,sysRole.getRoleName())
                .eq(SysRole::getIsDeleted,false)
                .ne(SysRole::getRoleId,sysRole.getRoleId());
        SysRole role = this.checkRoleName(queryWrapper);
        if(role == null){
            sysRoleMapper.updateById(sysRole);
            //如果不启用，需要将所有属于该角色的用户的启用状态变成未启用
            if(sysRole.getIsUsed() != null && !sysRole.getIsUsed()){
                sysUserRoleMapper.switchIsUsedByRoleId(sysRole);
            }
            //如果未空或启用，不做处理
        }
    }

    @Override
    public void deleteRole(Integer roleId) {
        SysRole sysRole = new SysRole();
        sysRole.setRoleId(roleId);
        sysRole.setIsDeleted(true);
        sysRole.setUpdatedTime(new Date());
        LoginUser loginUser = LoginContext.me().getLoginUser();
        sysRole.setUpdatedUser(loginUser.getPhone());
        sysRoleMapper.updateById(sysRole);
        //删除该角色与用户的所有关联关系
        QueryWrapper<SysUserRole> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(SysUserRole::getRoleId,roleId);
        sysUserRoleMapper.delete(wrapper);
        //删除该角色与菜单的所有关联关系
        QueryWrapper<SysRoleMenu> wp = new QueryWrapper<>();
        wp.lambda().eq(SysRoleMenu::getRoleId,roleId);
        sysRoleMenuMapper.delete(wp);
    }

    @Override
    public SysRole getRoleById(Integer roleId) {
        return sysRoleMapper.getRoleById(roleId);
    }

    @Override
    public List<SysRole> selectRolesByUserId(Integer userId) {
        return sysRoleMapper.selectRolesByUserId(userId);
    }
}
