package com.dg.mall.system.core.controller;


import com.dg.mall.core.reqres.response.ResponseData;
import com.dg.mall.system.service.SysOperationLogService;
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

    @GetMapping(value = "/page")
    public ResponseData getOperationLogList(LogQueryParamVO logQueryParam) {
        return ResponseData.success(logService.getLogList(logQueryParam));
    }


}

