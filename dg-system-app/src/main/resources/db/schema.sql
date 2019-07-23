DROP TABLE IF EXISTS sys_user;

CREATE TABLE `sys_user` ( 
      `USER_ID` bigint(20) NOT NULL COMMENT '主键id',
      `AVATAR` varchar(255) DEFAULT NULL COMMENT '头像',
      `ACCOUNT` varchar(45) DEFAULT NULL COMMENT '账号',
      `PASSWORD` varchar(45) DEFAULT NULL COMMENT '密码',
      `SALT` varchar(45) DEFAULT NULL COMMENT 'md5密码盐',
      `NAME` varchar(45) DEFAULT NULL COMMENT '名字',
      `BIRTHDAY` datetime DEFAULT NULL COMMENT '生日',
      `SEX` char(1) DEFAULT NULL COMMENT '性别（M：男 F：女）',
      `EMAIL` varchar(45) DEFAULT NULL COMMENT '电子邮件',
      `PHONE` varchar(45) DEFAULT NULL COMMENT '电话',
      `STATUS` int(11) DEFAULT NULL COMMENT '状态(1：启用  2：冻结  3：删除）',
      `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
      `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
      PRIMARY KEY (`USER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表';