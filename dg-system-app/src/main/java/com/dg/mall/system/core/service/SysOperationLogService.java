package com.dg.mall.system.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dg.mall.system.entity.SysOperationLog;
import com.dg.mall.system.vo.LogQueryParamVO;


/**
 * @Author hn
 * @Description: 操作日志查询
 * @Date 2019/8/6 15:20
 * @Version V1.0
 **/

public interface SysOperationLogService extends IService<SysOperationLog> {

    IPage<SysOperationLog> getLogList(LogQueryParamVO logQueryParam);

}
