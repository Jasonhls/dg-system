package com.dg.mall.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dg.mall.system.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author mabo
 * @create 2019/8/2 9:11
 */
@Mapper
public interface SysMenuMapper  extends BaseMapper<SysMenu> {

    List<SysMenu> selectUrlsByRoleId(@Param("roleIds") List<Integer> roleIds);
}
