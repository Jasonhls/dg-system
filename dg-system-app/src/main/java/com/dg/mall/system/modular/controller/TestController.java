package com.dg.mall.system.modular.controller;

import com.dg.mall.logger.util.LogUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 测试
 * @author: chase
 * @create: 2019-07-24 15:16
 **/
@RestController
public class TestController {

    @GetMapping("info")
    public String getInfo(){
        LogUtil.info("test kafka");
        return "up";
    }
}
