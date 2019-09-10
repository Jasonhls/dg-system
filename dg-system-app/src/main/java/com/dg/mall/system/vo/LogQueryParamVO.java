package com.dg.mall.system.vo;

import lombok.Data;

/**
 * @Author hn
 * @Description: 操作日志查询 参数
 * @Date 2019/8/6 15:20
 * @Version V1.0
 **/


@Data
public class LogQueryParamVO {

    /**
     *  用户id  用于准确查询
     */
    private Integer userId;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 当前页
     */
    private Long current;

    /**
     * 每页大小
     */
    private Long size;

    /**
     *  模糊查询关键字
     */
    private String keyWords;

}
