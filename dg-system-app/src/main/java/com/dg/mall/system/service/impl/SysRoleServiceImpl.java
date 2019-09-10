package com.dg.mall.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dg.mall.core.page.PageVO;
import com.dg.mall.core.util.ValidationUtils;
import com.dg.mall.system.api.context.LoginUser;
import com.dg.mall.system.api.context.SysRoleDTO;
import com.dg.mall.system.api.exception.enums.SystemExceptionEnum;
import com.dg.mall.system.context.LoginContext;
import com.dg.mall.system.core.constants.SystemConstants;
import com.dg.mall.system.entity.SysRole;
import com.dg.mall.system.mapper.SysRoleMapper;
import com.dg.mall.system.mapper.SysRoleMenuMapper;
import com.dg.mall.system.mapper.SysUserRoleMapper;
import com.dg.mall.system.service.SysRoleService;
import com.dg.mall.system.vo.req.RoleQueryVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
        LoginUser loginUser = LoginContext.me().getLoginUser();
        ValidationUtils.checkParam(loginUser.getRoles() != null && loginUser.getRoles().size() > 0 &&
                loginUser.getRoles().get(0) != null, SystemExceptionEnum.COMMENT_SERVICE_EXCEPTION.getCode(),"用户角色不能为空");
        //只有超级管理员可以查看所有角色，其他人只能查看那些父角色为当前登录人所属角色的角色
        SysRoleDTO sr = loginUser.getRoles().get(0);
        if(!SystemConstants.SUPER_MANAGER.equals(sr.getRoleName())){
            queryWrapper.lambda().eq(SysRole::getParentRoleId,sr.getRoleId());
        }
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
        queryWrapper.lambda().eq(SysRole::getIsDeleted,false).orderByDesc(SysRole::getUpdatedTime);
        return sysRoleMapper.selectPage(pageVO,queryWrapper);
    }

    @Override
    public void saveRole(SysRole sysRole) {
        LoginUser loginUser = LoginContext.me().getLoginUser();
        QueryWrapper<SysRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(SysRole::getRoleName,sysRole.getRoleName())
                .eq(SysRole::getParentRoleId,sysRole.getParentRoleId())
                .eq(SysRole::getIsDeleted,false);
        sysRole = checkRole(sysRole,queryWrapper,loginUser);
        sysRole.setCreatedUser(loginUser.getPhone());
        sysRoleMapper.insert(sysRole);
    }

    private SysRole checkRole(SysRole sysRole,QueryWrapper<SysRole> queryWrapper,LoginUser loginUser){
        SysRoleDTO sr = loginUser.getRoles().get(0);
        //只有超级管理员或普通管理员才能操作角色
        ValidationUtils.checkParam(SystemConstants.SUPER_MANAGER.equals(sr.getRoleName()) || loginUser.getIsGeneralAdministrator(),
                SystemExceptionEnum.COMMENT_SERVICE_EXCEPTION.getCode(),"只有超级管理员或普通管理员才能创建或更新角色");
        String roleName;
        if(sysRole.getParentRoleId() != 0){
            SysRole parentRole = sysRoleMapper.getRoleById(sysRole.getParentRoleId());
            ValidationUtils.checkNotNull(parentRole,SystemExceptionEnum.COMMENT_SERVICE_EXCEPTION.getCode(),"该父角色不存在");
            roleName = parentRole.getRoleName();
        }else {
            roleName = SystemConstants.SUPER_MANAGER;
        }

        //超级管理员直系的角色名不能重复，普通管理员下面的角色名也不能重复，但是他们相互之间，创建的角色名可以相同
        List<SysRole> sysRoles = sysRoleMapper.selectList(queryWrapper);
        ValidationUtils.checkParam(sysRoles == null || sysRoles.size() < 1,
                SystemExceptionEnum.COMMENT_SERVICE_EXCEPTION.getCode(),roleName + "下面已经存在同名的角色");

        sysRole.setUpdatedUser(loginUser.getPhone());
        sysRole.setUpdatedTime(new Date());
        return sysRole;
    }

    @Override
    public void updateRole(SysRole sysRole) {
        LoginUser loginUser = LoginContext.me().getLoginUser();
        SysRoleDTO sr = loginUser.getRoles().get(0);
        if((!SystemConstants.SUPER_MANAGER.equals(sr.getRoleName())) && loginUser.getIsGeneralAdministrator()){
            ValidationUtils.checkParam(sysRole.getParentRoleId().equals(sr.getRoleId()),
                    SystemExceptionEnum.COMMENT_SERVICE_EXCEPTION.getCode(),"普通管理员只能修改该普通管理员下面的角色");
        }
        QueryWrapper<SysRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(SysRole::getRoleName,sysRole.getRoleName())
                .eq(SysRole::getParentRoleId,sysRole.getParentRoleId())
                .eq(SysRole::getIsDeleted,false)
                .ne(SysRole::getRoleId,sysRole.getRoleId());
        sysRole = this.checkRole(sysRole,queryWrapper,loginUser);
        sysRoleMapper.updateById(sysRole);
        //如果不启用，需要将所有属于该角色的用户的启用状态变成未启用
        if(sysRole.getIsUsed() != null && !sysRole.getIsUsed()){
            sysUserRoleMapper.switchIsUsedByRoleId(sysRole);
        }
    }

    @Override
    public void deleteRole(Integer roleId) {
        LoginUser loginUser = LoginContext.me().getLoginUser();
        SysRoleDTO srd = loginUser.getRoles().get(0);
        SysRole sr = sysRoleMapper.getRoleById(roleId);
        ValidationUtils.checkNotNull(sr,SystemExceptionEnum.COMMENT_SERVICE_EXCEPTION.getCode(),"该角色不存在");
        ValidationUtils.checkParam(SystemConstants.SUPER_MANAGER.endsWith(srd.getRoleName()) || sr.getParentRoleId().equals(srd.getRoleId()),
                SystemExceptionEnum.COMMENT_SERVICE_EXCEPTION.getCode(),"只有超级管理员或普通管理员才能删除该角色");
        SysRole sysRole = new SysRole();
        sysRole.setRoleId(roleId);
        sysRole.setIsDeleted(true);
        sysRole.setUpdatedTime(new Date());
        sysRole.setUpdatedUser(loginUser.getPhone());
        //删除该角色
        sysRoleMapper.updateById(sysRole);
        //删除该角色的所有子角色
        SysRole sysRoleChildren = new SysRole();
        sysRoleChildren.setUpdatedUser(loginUser.getPhone());
        sysRoleMapper.deleteRoleByParentId(sysRoleChildren);
        //获取所有子角色集合
        RoleQueryVO rv = new RoleQueryVO();
        rv.setParentRoleId(roleId);
        List<SysRoleDTO> childrenRoles = getRoleTreee(rv);
        List<Integer> collect = childrenRoles.stream().map(SysRoleDTO::getRoleId).collect(Collectors.toList());
        collect.add(roleId);
        //删除该角色及其子角色与用户的所有关联关系
        sysUserRoleMapper.deleteUserByRoleIds(collect);
        //删除该角色及其子角色与菜单的所有关联关系
        sysRoleMenuMapper.deleteMenuByRoleIds(collect);
    }

    @Override
    public SysRole getRoleById(Integer roleId) {
        return sysRoleMapper.getRoleById(roleId);
    }

    @Override
    public List<SysRole> selectRolesByUserId(Integer userId) {
        return sysRoleMapper.selectRolesByUserId(userId);
    }

    @Override
    public List<SysRoleDTO> getRoleTreee(RoleQueryVO roleQueryVO) {
        QueryWrapper<SysRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(roleQueryVO.getParentRoleId() != null,SysRole::getParentRoleId,roleQueryVO.getParentRoleId())
                .eq(SysRole::getIsDeleted,false);
        List<SysRole> sysRoles = sysRoleMapper.selectList(queryWrapper);
        List<SysRoleDTO> list = new ArrayList<>(sysRoles.size());
        for (int i = 0;i < sysRoles.size();i++){
            SysRoleDTO sysRoleDTO = new SysRoleDTO();
            BeanUtils.copyProperties(sysRoles.get(i),sysRoleDTO);
            list.add(sysRoleDTO);
        }
        SysRoleDTO srd = new SysRoleDTO(roleQueryVO.getParentRoleId() == null ? 0 : roleQueryVO.getParentRoleId(),
                "","", 0);
        tranferListToTree(list,srd);
        return srd.getChildren();
    }

    private void tranferListToTree(List<SysRoleDTO> list,SysRoleDTO sysRoleDTO){
        if(list == null || list.size() < 1){return;}
        List<SysRoleDTO> newList = new ArrayList<>();
        List<SysRoleDTO> children = new ArrayList<>();
        for (int i = 0;i < list.size();i++){
            SysRoleDTO srd = list.get(i);
            if(srd.getParentRoleId().equals(sysRoleDTO.getRoleId())){
                children.add(srd);
            }else{
                newList.add(srd);
            }
        }
        if(children.size() > 0){
            sysRoleDTO.setChildren(children);
        }else{
            return;
        }
        for (int j = 0;j < children.size();j++){
            tranferListToTree(newList,children.get(j));
        }
    }
}
