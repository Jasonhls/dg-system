package com.dg.mall.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dg.mall.system.entity.SysRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author jobob
 * @since 2019-08-01
 */
public interface SysRoleMapper extends BaseMapper<SysRole> {

    List<SysRole> selectRolesByUserId(@Param("userId") Integer userId);

    SysRole getRoleById(@Param("roleId") Integer roleId);

    void deleteRoleByParentId(SysRole sysRole);
}
