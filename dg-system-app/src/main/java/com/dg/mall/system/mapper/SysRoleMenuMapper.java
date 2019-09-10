package com.dg.mall.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dg.mall.system.entity.SysRoleMenu;
import feign.Param;

import java.util.List;

/**
 * @author mabo
 * @create 2019/8/5 16:45
 */
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {
    void deleteMenuByRoleIds(@Param("roleIds") List<Integer> roleIds);
}
