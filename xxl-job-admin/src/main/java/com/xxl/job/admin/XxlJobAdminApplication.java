package com.xxl.job.admin;

import com.xxl.job.core.config.XxlJobConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 应用入口 XxlJobAdminApplication
 */
@SpringBootApplication(exclude = XxlJobConfig.class)
public class XxlJobAdminApplication {

	public static void main(String[] args) {
        SpringApplication.run(XxlJobAdminApplication.class, args);
	}

}