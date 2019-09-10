package com.dg.mall.system.entity;

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
 * 用户表
 * </p>
 *
 * @author 你的名字
 * @since 2019-08-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("dg_sys_user")
public class SysUser implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 主键id
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Integer userId;

    /**
     * 手机号/账号
     */
    private String phone;

    /**
     * 密码
     */
    private String password;

    /**
     * 名称
     */
    private String name;
    
    /**
     * 所属部门
     */
    private Integer department;

    /**
     * 职位名称
     */
    private String jobTitle;

    /**
     * 性别（1：男 0：女）
     */
    private Integer sex;

    /**
     * 电子邮件
     */
    private String email;


    /**
     * 是否普通管理员（1：是，0：否）
     */
    private Boolean isGeneralAdministrator;

    /**
     * 是否具备审核（1：是，0：否）
     */
    private Boolean isToexamine;
    
    /**
     * 审核范围
     */
    private Integer scopeAudit;

    /**
     * 备注
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

    /**
     * 头像照片
     * 
     */
    private String photo;
    
    /**
     * 是否启用(1启用，0未启用)
     */
    private Boolean isUsed;

    /**
     * 是否删除(1已删除，0未删除)
     */
    private Boolean isDeleted;
}
