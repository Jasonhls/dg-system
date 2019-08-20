package com.dg.mall.system.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dg.mall.core.page.PageVO;
import com.dg.mall.core.util.ValidationUtils;
import com.dg.mall.model.exception.ServiceException;
import com.dg.mall.system.api.context.LoginUser;
import com.dg.mall.system.api.exception.enums.SystemExceptionEnum;
import com.dg.mall.system.context.LoginContext;
import com.dg.mall.system.entity.SysRole;
import com.dg.mall.system.entity.SysUser;
import com.dg.mall.system.entity.SysUserRole;
import com.dg.mall.system.mapper.SysUserMapper;
import com.dg.mall.system.mapper.SysUserRoleMapper;
import com.dg.mall.system.service.SysUserService;
import com.dg.mall.system.vo.SysUserRoleVO;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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

	@Override
	public void saveUser(SysUserRoleVO userVO) {
		//校验用户信息
	   if(userVO != null && StringUtils.isBlank(userVO.getPhone())) {
	       	 throw new ServiceException(SystemExceptionEnum.USER_PHONE_IS_NOT_NULL);
	   }
	   if(userVO != null && StringUtils.isBlank(userVO.getEmail())) {
	       	 throw new ServiceException(SystemExceptionEnum.USER_EMAIL_IS_NOT_NULL);
	   }
	    //校验
		this.checkUserInfo(userVO);
		SysUser user = new SysUser();
		BeanUtils.copyProperties(userVO,user);
    	Date date = new Date();
		String passWord = user.getPassword();
		user.setPassword(passWord);
	    user.setCreateTime(date);
	    user.setUpdateTime(date);
	    LoginUser loginUser = LoginContext.me().getLoginUser();
	    user.setCreateUser(loginUser.getPhone());
	    user.setUpdateUser(loginUser.getPhone());
	    //保存用户信息表
	    this.baseMapper.insert(user);
	    //查询userid
	    QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
	    queryWrapper.eq("phone",userVO.getPhone());
	    queryWrapper.eq("is_deleted", false);
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
	}


	@Override
	public void updateUser(SysUserRoleVO userVO) {
		SysUser user = new SysUser();
		BeanUtils.copyProperties(userVO,user);
		//校验
		this.checkUserInfo(userVO);
    	user.setUpdateTime(new Date());
        LoginUser loginUser = LoginContext.me().getLoginUser();
 	    user.setUpdateUser(loginUser.getPhone());
 		this.baseMapper.updateById(user);
 		//校验是否有角色变更
 		if(userVO!= null && userVO.getRoleId()!=null) {
 	 		userVO.setUpdateTime(new Date());
 	 		userVO.setUpdateUser(loginUser.getPhone());
 	 		sysUserRoleMapper.updateUserRoleByUserId(userVO);
 		}

	}

	
	@Override
	public void deleteUser(Integer userId) {
		LoginUser loginUser = LoginContext.me().getLoginUser();
		if(userId != null && userId.equals(loginUser.getUserId())) {
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
	}

	
	@Override
	public PageVO<SysUserRoleVO> queryUserList(SysUserRoleVO userVO) {
		PageVO<SysUserRoleVO> page = new PageVO<>(userVO.getCurrent(), userVO.getSize());// 当前页，总条数 构造 page 对象
		return page.setRecords(sysUserRoleMapper.queryUserListPage(page, userVO));
	}


	@Override
	public PageVO<SysUserRoleVO> queryUserByRoleId(SysUserRoleVO userVO) {
		PageVO<SysUserRoleVO> page = new PageVO<>(userVO.getCurrent(), userVO.getSize());// 当前页，总条数 构造 page 对象
		
		List<SysUserRoleVO> ls =sysUserRoleMapper.selectUserPageByRoleId(page, userVO);
		return page.setRecords(ls);
		
	}
	
	
	/**
	 * 用户信息的校验
	 * @param userVO
	 */
	public void checkUserInfo(SysUserRoleVO userVO) {
        QueryWrapper<SysUser> queryWrapperPhone = new QueryWrapper<>();
        queryWrapperPhone.eq("phone",userVO.getPhone());
        queryWrapperPhone.eq("is_deleted",false);
        QueryWrapper<SysUser> queryPhone = new QueryWrapper<>();
        queryPhone.eq("phone",userVO.getPhone());
        QueryWrapper<SysUser> queryWrapperMail = new QueryWrapper<>();
	    queryWrapperMail.eq("email",userVO.getEmail());
	    queryWrapperMail.eq("is_deleted", false);
        if(userVO != null && userVO.getUserId()!= null) {
        	queryWrapperPhone.ne("user_id", userVO.getUserId());
        	queryPhone.ne("user_id", userVO.getUserId());
        	queryWrapperMail.ne("user_id", userVO.getUserId());
        }
        //判断手机号是否重复 
        SysUser oldUserPhone = this.baseMapper.selectOne(queryWrapperPhone);
        if(oldUserPhone != null) {
        	 throw new ServiceException(SystemExceptionEnum.USER_PHONE_EFFECTIVE_IS_EXIST);
        }

        SysUser phone = this.baseMapper.selectOne(queryPhone);
        if(phone != null) {
       	 throw new ServiceException(SystemExceptionEnum.USER_PHONE_IS_EXIST);
       }

		//判断邮箱是否重复
        SysUser oldUserMail = this.baseMapper.selectOne(queryWrapperMail);
        if(oldUserMail != null) {
          	 throw new ServiceException(SystemExceptionEnum.USER_EMAIL_IS_EXIST);
         }

        if(userVO!=null && userVO.getRoleId()!= null) {
        	//判断该角色下是否已存在普通管理员用户
    		List<SysUserRoleVO> ls =sysUserRoleMapper.queryUserListByRoleId(userVO);
    	     if(ls != null || ls.size() > 0) {
              	 throw new ServiceException(SystemExceptionEnum.USER_ROLE_GENERAL_ADMINISTRATOR);
             }
        }

	}


	@Override
	public SysRole queryRoleByUserId(Integer userId) {
		return sysUserRoleMapper.selectRoleByUserId(userId);
	}


	@Override
	public List<SysRole> queryRolesByRoleCreateUser(SysRole sysRole) {
		if(sysRole != null && StringUtils.isNotBlank(sysRole.getCreatedUser())) {
			LoginUser loginUser = LoginContext.me().getLoginUser();
			sysRole.setCreatedUser(loginUser.getPhone());
		}
		List<SysRole> lsRole = sysUserRoleMapper.queryRolesByRoleCreateUser(sysRole);
		return lsRole;
	}

	@Override
	public List<Map<String, Object>> selectScopeAudit() {
		//后期从数据字典中读取
		//暂时写死
		Map<Integer, String>  map =new HashMap<Integer, String>();
		map.put(0, "特产店商品专员");
		map.put(1,"自营店商品专员");
		map.put(2,"点宝店商品专员");
		map.put(3,"运营总监");
		map.put(4,"会计");
		map.put(5,"财务总监");
		map.put(6,"出纳");
		map.put(7,"总裁");
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(8);  
		for (int i = 0; i < 8; i++) {
			Map<String, Object>  mp =new HashMap<String, Object>();
			mp.put("code", i);
			mp.put("value", map.get(i));
			list.add(mp);
		}
		return list;
	}
	public static void main(String[] args) {
		Integer aa=7;
		Integer bb=7;
		System.out.print(aa.equals(bb));
	}


	@Override
	public SysUserRoleVO queryUserByUserId(Integer userId) {
		return sysUserRoleMapper.queryUserByUserId(userId);
	}

    @Override
    public SysUser queryUserByUserId(String userId) {
        SysUser sysUser = sysUserMapper.selectOne(new QueryWrapper<SysUser>().lambda().eq(SysUser::getUserId, Integer.valueOf(userId)).eq(SysUser::getIsDeleted, false));
        ValidationUtils.checkParam(sysUser != null, SystemExceptionEnum.USER_NOT_FOUND.getCode());
        return sysUser;
    }
}
