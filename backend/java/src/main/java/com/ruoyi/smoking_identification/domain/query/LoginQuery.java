package com.ruoyi.smoking_identification.domain.query;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@ApiModel("登录实体")
public class LoginQuery {

    @ApiModelProperty(name = "用户名",required = true)
    @NotBlank(message = "用户名不能为空")
    private String userName;

    @ApiModelProperty(name = "密码",required = true)
    @NotBlank(message = "密码不能为空")
    private String password;


}
