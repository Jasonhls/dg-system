package com.dg.mall.system.core.consumer;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dg.mall.logger.constants.KafkaConstants;
import com.dg.mall.logger.entity.SysOperationLogDTO;
import com.dg.mall.system.core.constants.RedisKeyConstants;
import com.dg.mall.system.entity.SysOperationLog;
import com.dg.mall.system.entity.SysRequestConfig;
import com.dg.mall.system.service.SysOperationLogService;
import com.dg.mall.system.service.SysRequestConfigService;
import com.dg.mall.system.service.SysUserService;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author hn
 * @Description: 接收kafka发送的操作日志消息，保存到数据库表中
 * @Date 2019/8/8 17:02
 * @Version V1.0
 **/

@Component
public class SysLogConsumer {

    private static final Logger logger = LogManager.getLogger(SysLogConsumer.class);

    @Autowired
    private SysOperationLogService logService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysRequestConfigService sysRequestConfigService;

    @Autowired
    private RedisTemplate redisTemplate;

    @KafkaListener(topics = KafkaConstants.SYS_LOG_TOPIC)
    public void logConsumer(List<ConsumerRecord<String, String>> records) {
        if (CollectionUtil.isNotEmpty(records)) {
            for (ConsumerRecord<String, String> record : records) {
                logger.info("消费消息：" + record.value());
                SysOperationLogDTO sysOperationLog = JSONObject.parseObject(record.value(), SysOperationLogDTO.class);
                //用户名称
                String userName = sysUserService.queryUserByUserId(sysOperationLog.getUserId()).getName();
                //操作日志记录实体类
                SysOperationLog log = new SysOperationLog(userName, sysOperationLog);
                //操作记录(后期从缓存中读取数据)
                List<SysRequestConfig> list = (List<SysRequestConfig>)redisTemplate.opsForValue().get(RedisKeyConstants.REQUEST_CONFIG_DATA);
                if(CollectionUtils.isEmpty(list)){
                    list = sysRequestConfigService.list();
                }
                Map<String, SysRequestConfig> urlMap = new HashMap(16);
                for (SysRequestConfig config : list) {
                    urlMap.put(config.getRequestUrl() + config.getRequestType(), config);
                }
                String key = sysOperationLog.getPath() + sysOperationLog.getRequestType();
                if (urlMap.containsKey(key)) {
                    String remark = urlMap.get(key).getUrlDescription();
                    StringBuilder sb = new StringBuilder(remark);
                    String params = urlMap.get(key).getRequestParams();
                    JSONArray json = JSON.parseArray(sysOperationLog.getRequestParams());
                    if (StringUtils.isNotBlank(params)) {
                        //path与url一样
                        if (sysOperationLog.getPath().equals(sysOperationLog.getRequestUrl())) {
                            remark = sb.append(",").append(String.format(urlMap.get(key).getParamsDescription(), json.getJSONObject(0).get(params))).toString();
                        } else {
                            remark = sb.append(",").append(String.format(urlMap.get(key).getParamsDescription(), json.get(0))).toString();
                        }
                    }
                    log.setRemark(remark);
                }
                logger.info("消息入库：{}", log);
                logService.save(log);
            }
        }
    }

}
