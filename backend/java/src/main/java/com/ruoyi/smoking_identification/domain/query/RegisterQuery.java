package com.ruoyi.smoking_identification.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel("注册实体")
public class RegisterQuery {

    @ApiModelProperty(name = "用户名",required = true)
    @NotBlank(message = "用户名不能为空")
    private String userName;

    @ApiModelProperty(name = "密码",required = true)
    @NotBlank(message = "密码不能为空")
    private String password;

    @ApiModelProperty(name = "电话号码",required = true)
    @NotBlank(message = "手机号不能为空")
    private String phone;

    @ApiModelProperty(name = "邮箱",required = true)
    @NotBlank(message = "邮箱不能为空")
    private String email;
}
