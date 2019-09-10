package com.dg.mall.system.consumer;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dg.mall.logger.constants.KafkaConstants;
import com.dg.mall.logger.entity.SysOperationLogDTO;
import com.dg.mall.system.entity.SysOperationLog;
import com.dg.mall.system.entity.SysUser;
import com.dg.mall.system.service.SysOperationLogService;
import com.dg.mall.system.service.SysUserService;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * @Author hn
 * @Description: 接收kafka发送的操作日志消息，保存到数据库表中
 * @Date 2019/8/8 17:02
 * @Version V1.0
 **/

@Component
public class SysLogConsumer {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SysOperationLogService logService;

    @Autowired
    private SysUserService sysUserService;

    @KafkaListener(topics = KafkaConstants.SYS_LOG_TOPIC)
    public void logConsumer(List<ConsumerRecord<String, String>> records) {
        if (CollectionUtil.isNotEmpty(records)) {
            for (ConsumerRecord<String, String> record : records) {
                logger.info("消费消息：" + record.value());
                SysOperationLogDTO sysOperationLog = JSON.parseObject(record.value(), SysOperationLogDTO.class);
                if (sysOperationLog == null) {
                    continue;
                }
                SysUser sysUser = sysUserService.getOne(new QueryWrapper<SysUser>().lambda().eq(SysUser::getUserId, Integer.valueOf(sysOperationLog.getUserId())));
                if (sysUser == null) {
                    continue;
                }
                //操作日志实体类
                SysOperationLog log = new SysOperationLog(sysUser, sysOperationLog);
                JSONArray json = JSON.parseArray(sysOperationLog.getRequestParams());
                StringBuilder sb = new StringBuilder(sysOperationLog.getRemark());
                if (StringUtils.isNotBlank(sysOperationLog.getRemark()) && StringUtils.isNotBlank(sysOperationLog.getKeyParam())) {
                    for (int i = 0; i < json.size(); i++) {
                        if (json.get(i) instanceof JSONObject) {
                            Object object = json.getJSONObject(i).get(sysOperationLog.getKeyParam());
                            if (object != null) {
                                sb.append(",").append(sysOperationLog.getParamDesc()).append(object.toString());
                                break;
                            }
                        } else {
                            sb.append(",").append(sysOperationLog.getParamDesc()).append(json.get(i));
                            break;
                        }
                    }
                }
                log.setRemark(sb.toString());
                logger.info("消息入库：{}", log);
                logService.save(log);
            }
        }
    }
}
