package com.dg.mall.system.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 系统请求配置表
 * </p>
 *
 * @author hn
 * @since 2019-08-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("dg_sys_request_config")
public class SysRequestConfig implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 请求路径
     */
    private String requestUrl;

    /**
     * 请求类型 GET/POST/PUT/DELETE
     */
    private String requestType;

    /**
     * 请求描述
     */
    private String urlDescription;

    /**
     * 请求参数
     */
    private String requestParams;

    /**
     * 参数描述
     */
    private String paramsDescription;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 更新人
     */
    private String updateUser;


}
