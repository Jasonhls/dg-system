package com.dg.mall.system.service.impl;

import com.dg.mall.core.page.PageVO;
import com.dg.mall.model.exception.ServiceException;
import com.dg.mall.system.api.context.LoginUser;
import com.dg.mall.system.api.context.SysRoleDTO;
import com.dg.mall.system.api.exception.enums.SystemExceptionEnum;
import com.dg.mall.system.context.LoginContext;
import com.dg.mall.system.core.constants.SystemConstants;
import com.dg.mall.system.entity.SysRole;
import com.dg.mall.system.entity.SysUser;
import com.dg.mall.system.entity.SysUserRole;
import com.dg.mall.system.mapper.SysRoleMapper;
import com.dg.mall.system.mapper.SysUserMapper;
import com.dg.mall.system.mapper.SysUserRoleMapper;
import com.dg.mall.system.service.SysRoleService;
import com.dg.mall.system.service.SysUserService;
import com.dg.mall.system.vo.SysUserRoleVO;

import io.micrometer.core.instrument.util.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author wlq
 * @since 2019-08-01
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void saveUser(SysUserRoleVO userVO) {
        //校验用户信息
        if (userVO != null && StringUtils.isBlank(userVO.getPhone())) {
            throw new ServiceException(SystemExceptionEnum.USER_PHONE_IS_NOT_NULL);
        }
        if (userVO != null && StringUtils.isBlank(userVO.getEmail())) {
            throw new ServiceException(SystemExceptionEnum.USER_EMAIL_IS_NOT_NULL);
        }
        LoginUser loginUser = LoginContext.me().getLoginUser();
        List<SysRoleDTO> roleLs = loginUser.getRoles();
        this.checkUserInfo(userVO, roleLs);
        SysUser user = new SysUser();
        BeanUtils.copyProperties(userVO, user);
        Date date = new Date();
        //查询是否存在已删除的手机号
        SysUser isUser = this.selectUserExist(userVO);
        if (isUser != null && StringUtils.isNotBlank(isUser.getPhone())) {
            user.setUpdateTime(date);
            user.setUpdateUser(loginUser.getPhone());
            user.setCreateTime(date);
            user.setCreateUser(loginUser.getPhone());
            user.setUserId(isUser.getUserId());
            user.setIsDeleted(false);
            user.setIsUsed(false);
            this.baseMapper.updateById(user);
        } else {

            String passWord = user.getPassword();
            user.setPassword(passWord);
            user.setCreateTime(date);
            user.setUpdateTime(date);
            user.setCreateUser(loginUser.getPhone());
            user.setUpdateUser(loginUser.getPhone());
            //保存用户信息表
            this.baseMapper.insert(user);
        }
        //查询userid
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", userVO.getPhone());
        SysUser oldUser = this.baseMapper.selectOne(queryWrapper);

        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(oldUser.getUserId());
        userRole.setRoleId(userVO.getRoleId());
        userRole.setCreateTime(date);
        userRole.setUpdateTime(date);
        userRole.setCreateUser(loginUser.getPhone());
        userRole.setUpdateUser(loginUser.getPhone());
        //保存用户角色信息表
        sysUserRoleMapper.insert(userRole);
        //超级管理员启用或未启用普通管理员按钮修改的相关数据
        userVO.setUserId(oldUser.getUserId());
        this.updateUserRoleData(userVO, loginUser, roleLs);
    }

    /**
     * 超级管理员启用或未启用普通管理员按钮修改的相关数据
     *
     * @param userVO
     * @param loginUser
     */
    public void updateUserRoleData(SysUserRoleVO userVO, LoginUser loginUser, List<SysRoleDTO> roleLs) {
        //校验是否是超级管理员操作
        if (roleLs != null && roleLs.size() > 0 && SystemConstants.SUPER_MANAGER.equals(roleLs.get(0).getRoleName())) {
            //校验是否有启用普通管理员操作
            if(userVO != null && userVO.getIsGeneralAdministrator() !=  null && userVO.getIsGeneralAdministrator()) {
                //校验该角色其他成员是否有普通管理员
                if (checkAdminByRole(userVO)) {
                    //更新普通管理员为普通成员
                    userVO.setUpdateTime(new Date());
//		    		 userVO.setUpdateUser(loginUser.getPhone());
                    sysUserRoleMapper.updateIsGeneralAdministrator(userVO);
                    //查询角色下是否有二级角色，并更新用户角色关联关系
                    this.checkRoleMenuData(userVO, loginUser);
                } else {
                    //检查该角色下是否有普通成员
                    if (checkGeneralUserByRole(userVO)) {
                        //查询角色下是否有二级角色，并更新用户角色关联关系
                        this.checkRoleMenuData(userVO, loginUser);
                    }
                }
            } else {//如未启用
            	if(userVO != null && userVO.getRoleId() != null) {
            		 //校验是否选择一级角色
                    List<SysRole> role = this.queryFistRole(userVO.getRoleId());
                    //如是
                    if (role != null && role.size() > 0) {
                        //校验该角色其他成员是否有普通管理员
                        if (checkAdminByRole(userVO)) {
                            //查询角色下是否有二级角色，并更新用户角色关联关系
                            this.checkRoleMenuData(userVO, loginUser);
                        }
                    }
            	}
            }
        }
    }

    @Override
    public void updateUser(SysUserRoleVO userVO) {
        SysUser user = new SysUser();
        BeanUtils.copyProperties(userVO, user);
        LoginUser loginUser = LoginContext.me().getLoginUser();
        List<SysRoleDTO> roleLs = loginUser.getRoles();
        //校验
        this.checkUserInfo(userVO, roleLs);
        QueryWrapper<SysUser> queryPhone = new QueryWrapper<>();
        queryPhone.eq("phone", userVO.getPhone());
        queryPhone.ne("user_id", userVO.getUserId());
        SysUser phone = this.baseMapper.selectOne(queryPhone);
        if (phone != null) {
            throw new ServiceException(SystemExceptionEnum.USER_PHONE_IS_EXIST);
        }
        user.setUpdateTime(new Date());
        user.setUpdateUser(loginUser.getPhone());
        this.baseMapper.updateById(user);

        //校验是否有角色变更
        if (userVO != null && userVO.getRoleId() != null) {
            userVO.setUpdateTime(new Date());
            userVO.setUpdateUser(loginUser.getPhone());
            sysUserRoleMapper.updateUserRoleByUserId(userVO);
        }
        this.updateUserRoleData(userVO, loginUser, roleLs);
    }


    @Override
    public void deleteUser(Integer userId) {
        LoginUser loginUser = LoginContext.me().getLoginUser();
        if (userId != null && userId.equals(loginUser.getUserId())) {
            throw new ServiceException(SystemExceptionEnum.USER_NOT_DELETE);
        }
        SysUser sysUser = new SysUser();
        sysUser.setUserId(userId);
        sysUser.setIsDeleted(true);
        sysUser.setUpdateTime(new Date());
        sysUser.setUpdateUser(loginUser.getPhone());
        this.baseMapper.updateById(sysUser);
        //删除用户关联角色
        QueryWrapper<SysUserRole> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        sysUserRoleMapper.delete(wrapper);
        //redisTemplate.delete(RedisKeyConstants.SYSTEM_USER_TOKEN_KEY.concat());
    }


    @Override
    public PageVO<SysUserRoleVO> queryUserList(SysUserRoleVO userVO) {
        PageVO<SysUserRoleVO> page = new PageVO<>(userVO.getCurrent(), userVO.getSize());// 当前页，总条数 构造 page 对象
        return page.setRecords(sysUserRoleMapper.queryUserListPage(page, userVO));
    }


    @Override
    public PageVO<SysUserRoleVO> queryUserByRoleId(SysUserRoleVO userVO) {
        PageVO<SysUserRoleVO> page = new PageVO<>(userVO.getCurrent(), userVO.getSize());// 当前页，总条数 构造 page 对象
        LoginUser loginUser = LoginContext.me().getLoginUser();
        List<SysRoleDTO> roleLs = loginUser.getRoles();
        if (roleLs != null && roleLs.size() > 0 && !SystemConstants.SUPER_MANAGER.equals(roleLs.get(0).getRoleName())) {
            userVO.setConditionRole(roleLs.get(0).getRoleId());//登录用户为普通管理员角色
        }
        userVO.setUserId(loginUser.getUserId());
        List<SysUserRoleVO> ls = sysUserRoleMapper.selectUserPageByRoleId(page, userVO);
        return page.setRecords(ls);

    }


    /**
     * 用户信息的校验
     *
     * @param userVO
     */
    public void checkUserInfo(SysUserRoleVO userVO, List<SysRoleDTO> roleLs) {
        QueryWrapper<SysUser> queryWrapperPhone = new QueryWrapper<>();
        queryWrapperPhone.eq("phone", userVO.getPhone());
        queryWrapperPhone.eq("is_deleted", false);
        QueryWrapper<SysUser> queryWrapperMail = new QueryWrapper<>();
        queryWrapperMail.eq("email", userVO.getEmail());
        queryWrapperMail.eq("is_deleted", false);
        if (userVO != null && userVO.getUserId() != null) {
            queryWrapperPhone.ne("user_id", userVO.getUserId());
            queryWrapperMail.ne("user_id", userVO.getUserId());
        }

        //判断角色id是否必输
        if(userVO != null && userVO.getIsGeneralAdministrator() != null && userVO.getIsGeneralAdministrator()) {
            if (roleLs != null && roleLs.size() > 0 && !SystemConstants.SUPER_MANAGER.equals(roleLs.get(0).getRoleName())) {
                throw new ServiceException(SystemExceptionEnum.USER_ADMINISTRATOR_JURISDICTION);
            }

            if (userVO.getRoleId() == null && userVO.getUserId() != null) {
            	userVO.setRoleId(sysUserRoleMapper.selectRoleByUserId(userVO.getUserId()).getRoleId());
            }
            //判断角色是否变更为一级角色，只有一级角色才拥有修改为普通管理员的权限
            List<SysRole> role = this.queryFistRole(userVO.getRoleId());
            if (role == null || role.size() == 0) {
                throw new ServiceException(SystemExceptionEnum.USER_FIST_ROLE);
            }
        }

        //判断手机号是否重复 
        SysUser oldUserPhone = this.baseMapper.selectOne(queryWrapperPhone);
        if (oldUserPhone != null) {
            throw new ServiceException(SystemExceptionEnum.USER_PHONE_EFFECTIVE_IS_EXIST);
        }

        //判断邮箱是否重复
        SysUser oldUserMail = this.baseMapper.selectOne(queryWrapperMail);
        if (oldUserMail != null) {
            throw new ServiceException(SystemExceptionEnum.USER_EMAIL_IS_EXIST);
        }

    }


    @Override
    public SysRole queryRoleByUserId(Integer userId) {
        return sysUserRoleMapper.selectRoleByUserId(userId);
    }


    @Override
    public List<SysRole> queryRolesByRoleCreateUser(SysRole sysRole) {
        LoginUser loginUser = LoginContext.me().getLoginUser();
        sysRole.setCreatedUser(loginUser.getPhone());
        List<SysRole> lsRole = sysUserRoleMapper.queryRolesByRoleCreateUser(sysRole);
        return lsRole;
    }

    @Override
    public List<Map<String, Object>> selectScopeAudit() {
        //后期从数据字典中读取
        //暂时写死
        Map<Integer, String> map = new HashMap<Integer, String>();
        map.put(0, "特产店商品专员");
        map.put(1, "自营店商品专员");
        map.put(2, "点宝店商品专员");
        map.put(3, "运营总监");
        map.put(4, "会计");
        map.put(5, "财务总监");
        map.put(6, "出纳");
        map.put(7, "总裁");
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(8);
        for (int i = 0; i < 8; i++) {
            Map<String, Object> mp = new HashMap<String, Object>();
            mp.put("code", i);
            mp.put("value", map.get(i));
            list.add(mp);
        }
        return list;
    }

    public static void main(String[] args) {
        Integer aa = 7;
        Integer bb = 7;
        System.out.print(aa.equals(bb));
    }


    @Override
    public SysUserRoleVO queryUserByUserId(Integer userId) {
        return sysUserRoleMapper.queryUserByUserId(userId);
    }


    public SysUser selectUserExist(SysUserRoleVO userVO) {
        QueryWrapper<SysUser> queryPhone = new QueryWrapper<>();
        queryPhone.eq("phone", userVO.getPhone());
        queryPhone.eq("is_deleted", true);
        return this.baseMapper.selectOne(queryPhone);

    }

    /**
     * 查询角色下是否有二级角色，并更新用户角色关联关系
     *
     * @param userVO
     * @param loginUser
     */
    public void checkRoleMenuData(SysUserRoleVO userVO, LoginUser loginUser) {
        //查询角色信息
        SysRole role = sysRoleMapper.getRoleById(userVO.getRoleId());
        //查询该一级角色下是否存在二级角色名称
        QueryWrapper<SysRole> queryWrapperRole = new QueryWrapper<>();
        queryWrapperRole.eq("role_name", role.getRoleName());
        queryWrapperRole.eq("parent_role_id", userVO.getRoleId());
        queryWrapperRole.eq("is_deleted", false);
        SysRole sysRole = sysRoleMapper.selectOne(queryWrapperRole);
        //如不存在， 新增二级角色
        if (sysRole == null) {
            SysRole sysRoles = new SysRole();
            sysRoles.setParentRoleId(role.getRoleId());
            sysRoles.setRoleName(role.getRoleName());
            sysRoles.setRoleDescription(role.getRoleDescription());
            sysRoles.setCreatedUser(loginUser.getPhone());
            sysRoles.setUpdatedUser(loginUser.getPhone());
            sysRoles.setUpdatedTime(new Date());
            sysRoleService.saveRole(sysRoles);
            QueryWrapper<SysRole> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("role_name", role.getRoleName());
            queryWrapper.eq("parent_role_id", role.getRoleId());
            queryWrapper.eq("is_deleted", false);
            sysRole = sysRoleMapper.selectOne(queryWrapper);
        }
        //把该角色下其他成员关联角色更新为最新二级菜单角色
        userVO.setConditionRole(sysRole.getRoleId());
        userVO.setUserId(userVO.getUserId());
        userVO.setRoleId(role.getRoleId());
        userVO.setUpdateUser(loginUser.getPhone());
        userVO.setUpdateTime(new Date());
        sysUserRoleMapper.updateUserRoleById(userVO);
    }

    /**
     * 查询角色下是否有普通管理员（不包括自己）
     *
     * @param userVO
     * @return
     */
    public boolean checkAdminByRole(SysUserRoleVO userVO) {
        //查询该角色下是否已经存在普通管理员
        userVO.setIsGeneralAdministrator(true);
        userVO.setRoleId(userVO.getRoleId());
        Integer countUser = sysUserRoleMapper.queryUserAdministratorsByRoleId(userVO);
        int count = countUser == null ? 0 : countUser.intValue();
        return count >= 1 ? true : false;
    }

    /**
     * 查询角色下是否有普通成员（不包括自己）
     *
     * @param userVO
     * @return
     */
    public boolean checkGeneralUserByRole(SysUserRoleVO userVO) {
        //查询该角色下是否已经存在普通成员
        userVO.setIsGeneralAdministrator(false);
        userVO.setRoleId(userVO.getRoleId());
        Integer countUser = sysUserRoleMapper.queryUserAdministratorsByRoleId(userVO);
        int count = countUser == null ? 0 : countUser.intValue();
        return count >= 1 ? true : false;

    }

    /**
     * 根据角色id查询是否是一级角色
     *
     * @param roleId
     * @return
     */
    public List<SysRole> queryFistRole(Integer roleId) {
        QueryWrapper<SysRole> queryWrapperRole = new QueryWrapper<>();
        queryWrapperRole.eq("role_id", roleId);
        queryWrapperRole.eq("parent_role_id", 0);
        queryWrapperRole.eq("is_deleted", false);
        queryWrapperRole.ne("role_name", SystemConstants.SUPER_MANAGER);
        return sysRoleMapper.selectList(queryWrapperRole);

    }


    public SysRole getParentIdData(Integer roleId) {
        SysRole role = sysRoleService.getRoleById(roleId);
        if (role != null && role.getParentRoleId().equals(0)) {
            return role;
        } else {
            role = sysRoleService.getRoleById(role.getParentRoleId());
            return role;
        }
    }

}
