package com.dg.mall.system.api.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @description:
 * @author: wangqiang
 * @create: 2019-08-08 14:30
 **/
@Data
public class ToeknReq {

    @NotBlank(message = "token 不允许为空")
    private String token;
}
