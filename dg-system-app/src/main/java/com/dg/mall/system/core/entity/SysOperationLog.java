package com.dg.mall.system.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dg.mall.logger.entity.SysOperationLogDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 操作日志表
 * </p>
 *
 * @author hn
 * @since 2019-08-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("dg_sys_operation_log")
public class SysOperationLog implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 操作人
     */
    private String userName;

    /**
     * 客户端ip地址
     */
    private String ipAddress;

    /**
     * 请求路径
     */
    private String requestUrl;

    /**
     * 请求参数
     */
    private String requestParams;

    /**
     * 请求类型 GET/POST
     */
    private String requestType;

    /**
     * 操作记录
     */
    private String remark;

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

    public SysOperationLog() {
    }

    public SysOperationLog(String userName, SysOperationLogDTO logDTO) {
        this.userName = userName;
        this.ipAddress = logDTO.getIpAddress();
        this.requestUrl = logDTO.getRequestUrl();
        this.requestParams = logDTO.getRequestParams();
        this.requestType = logDTO.getRequestType();
        this.createTime = logDTO.getCreateTime();
        this.createUser = userName;
    }
}
