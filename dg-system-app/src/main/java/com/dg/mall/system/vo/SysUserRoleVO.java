package com.dg.mall.system.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dg.mall.core.page.PageVO;
import com.dg.mall.system.entity.SysRole;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.io.Serializable;
import java.sql.Timestamp;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author wlq
 * @since 2019-08-02
 */
@Data
public class SysUserRoleVO extends PageVO<SysUserRoleVO> {


	/**
	 * 用户id
	 */
    private Integer userId;

    /**
	 * 手机号
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
	 * 部门
	 */
    private Integer department;

    /**
	 * 职位名称
	 */
    private String jobTitle;

    /**
	 * 性别
	 */
    private Integer sex;

    /**
	 * 邮箱
	 */
    private String email;

    /**
	 * 是否普通管理员
	 */
    private Boolean isGeneralAdministrator;

    /**
         * 是否具备审核
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
	 * 开始时间
	 */
    private String startTime;
    
    /**
	 * 结束时间
	 */
    private String endTime;
    
    /**
	 * 角色id
	 */
    private Integer roleId;
    
    /**
     * 角色名称
     */
    private String roleName;
    /**
     * 角色创建人
     */
    private String roleCreateUser;
    
    /**
	 * 角色列表
	 */
    private List<SysRole> sysRole;
    
    /**
     * 是否启用(1启用，0未启用)
     */
    private Boolean isUsed;

    /**
     * 是否删除(1已删除，0未删除)
     */
    private Boolean isDeleted;
    
    /**
     * 模糊查询
     */
    private String keyWords;
    
    private Integer conditionRole;
    
    private Integer parentRoleId;
    
}
