package com.dg.mall.system.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dg.mall.core.page.PageVO;
import com.dg.mall.system.entity.SysOperationLog;
import com.dg.mall.system.mapper.SysOperationLogMapper;
import com.dg.mall.system.service.SysOperationLogService;
import com.dg.mall.system.vo.LogQueryParamVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * @Author hn
 * @Description: 操作日志查询接口实现类
 * @Date 2019/8/6 16:25
 * @Version V1.0
 **/

@Service
public class SysOperationLogServiceImpl extends ServiceImpl<SysOperationLogMapper, SysOperationLog> implements SysOperationLogService {


    @Autowired
    private SysOperationLogMapper sysOperationLogMapper;

    @Override
    public PageVO<SysOperationLog> getLogList(LogQueryParamVO logQueryParam) {
        PageVO<SysOperationLog> page = new PageVO<>(logQueryParam.getCurrent(), logQueryParam.getSize());
        List<SysOperationLog> rnList = sysOperationLogMapper.getOperationLogList(page, logQueryParam);
        return page.setRecords(rnList);
    }

}
