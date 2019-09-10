package com.dg.mall.system.controller;


import com.dg.mall.core.reqres.response.ResponseData;
import com.dg.mall.core.util.ValidationUtils;
import com.dg.mall.logger.config.SysLog;
import com.dg.mall.system.api.exception.enums.SystemExceptionEnum;
import com.dg.mall.system.service.SysOperationLogService;
import com.dg.mall.system.service.SysUserService;
import com.dg.mall.system.vo.LogQueryParamVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 操作日志表 前端控制器
 * </p>
 *
 * @author hn
 * @since 2019-08-06
 */
@RestController
@RequestMapping("/sysLog")
public class SysOperationLogController {

    @Autowired
    private SysOperationLogService logService;
    @Autowired
    private SysUserService sysUserService;

    @GetMapping(value = "/page")
    @SysLog(description = "分页查询操作日志")
    public ResponseData getOperationLogList(LogQueryParamVO logQueryParam) {
        ValidationUtils.checkNotNull(logQueryParam.getCurrent(), SystemExceptionEnum.NULL_POINT_EXCEPTION.getCode(),"当前页");
        ValidationUtils.checkNotNull(logQueryParam.getSize(),SystemExceptionEnum.NULL_POINT_EXCEPTION.getCode(),"每页大小");
        return ResponseData.success(logService.getLogList(logQueryParam));
    }


    @GetMapping(value = "/userList")
    public ResponseData getUserList() {
        return ResponseData.success(sysUserService.list());
    }

}

