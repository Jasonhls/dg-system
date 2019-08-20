package com.dg.mall.system.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dg.mall.core.page.PageVO;
import com.dg.mall.system.core.constants.SystemConstants;
import com.dg.mall.system.entity.SysOperationLog;
import com.dg.mall.system.mapper.SysOperationLogMapper;
import com.dg.mall.system.service.SysOperationLogService;
import com.dg.mall.system.vo.LogQueryParamVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public IPage<SysOperationLog> getLogList(LogQueryParamVO logQueryParam) {
        if (logQueryParam.getCurrent() == null) {
            logQueryParam.setCurrent(SystemConstants.DEFAULT_CURRENT_PAGE);
        }
        if (logQueryParam.getSize() == null) {
            logQueryParam.setSize(SystemConstants.DEFAULT_PAGE_SIZE);
        }
        PageVO page = new PageVO(logQueryParam.getCurrent(), logQueryParam.getSize());
        QueryWrapper<SysOperationLog> queryWrapper = new QueryWrapper();
        queryWrapper.lambda()
                .like(StringUtils.isNoneBlank(logQueryParam.getUserName()), SysOperationLog::getUserName, logQueryParam.getUserName())
                .ge(logQueryParam.getStartTime() != null, SysOperationLog::getCreateTime, logQueryParam.getStartTime())
                .le(logQueryParam.getEndTime() != null, SysOperationLog::getCreateTime, logQueryParam.getEndTime())
                .orderByDesc(SysOperationLog::getCreateTime);

        return sysOperationLogMapper.selectPage(page, queryWrapper);
    }
}
