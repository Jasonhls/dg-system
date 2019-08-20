package com.dg.mall.system.core.config;

import com.dg.mall.system.core.constants.RedisKeyConstants;
import com.dg.mall.system.entity.SysRequestConfig;
import com.dg.mall.system.service.SysRequestConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author hn
 * @Description: 初始化请求配置表数据
 * @Date 2019/8/16 10:42
 * @Version V1.0
 **/
@Component
public class InitDataConfig implements CommandLineRunner {

    @Autowired
    private SysRequestConfigService sysRequestConfigService;

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public void run(String... strings) {
        redisTemplate.delete(RedisKeyConstants.REQUEST_CONFIG_DATA);
        List<SysRequestConfig> list = sysRequestConfigService.list();
        redisTemplate.opsForValue().set(RedisKeyConstants.REQUEST_CONFIG_DATA, list);
    }
}
