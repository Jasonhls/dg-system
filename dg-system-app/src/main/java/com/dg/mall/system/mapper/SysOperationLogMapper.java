package com.dg.mall.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dg.mall.core.page.PageVO;
import com.dg.mall.system.entity.SysOperationLog;
import com.dg.mall.system.vo.LogQueryParamVO;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * @Author hn
 * @Description: Mapper接口
 * @Date 2019/8/2 10:25
 * @Version V1.0
 **/

public interface SysOperationLogMapper extends BaseMapper<SysOperationLog> {

    /**
     * 查询操作日志信息
     * @param page
     * @param logQueryParam
     * @return
     */
    List<SysOperationLog> getOperationLogList(PageVO<SysOperationLog> page ,@Param("logQueryParam") LogQueryParamVO logQueryParam);


}
