package com.dg.mall.system.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Auther: chase
 * @Date: 2019/4/10 16:09
 * @Description: 拦截器
 */
@Configuration
public class WebMvcConfigure implements WebMvcConfigurer {

    @Bean
    public DgRequestInterceptor loginInterceptor() {
        return new DgRequestInterceptor();
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new DgRequestInterceptor())
                .addPathPatterns("/**");
    }

}
