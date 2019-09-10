package com.dg.mall.system.api.resp;

import com.dg.mall.system.api.context.SysMenuDTO;
import lombok.Data;
import java.util.List;

/**
 * @Author hn
 * @Description: 接口返回值
 * @Date 2019/8/9 17:49
 * @Version V1.0
 **/

@Data
public class UserMenuResp {

    /*
     用户名称
     */
    String userName;

    /*
     用户权限菜单列表
     */
    List<SysMenuDTO> list;
}
