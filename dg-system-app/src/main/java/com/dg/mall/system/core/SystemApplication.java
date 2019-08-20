package com.dg.mall.system.core;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@MapperScan(basePackages = {"com.dg.mall.system.mapper"})
public class SystemApplication {


    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class, args);
    }

}
