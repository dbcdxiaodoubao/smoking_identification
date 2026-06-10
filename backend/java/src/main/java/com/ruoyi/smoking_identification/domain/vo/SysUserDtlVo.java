package com.ruoyi.smoking_identification.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

@Data
@ApiModel("用户详情实体")
public class SysUserDtlVo {

  @ApiModelProperty("用户id")
  private Long userId;
  @ApiModelProperty("用户名")
  private String userName;
  @ApiModelProperty("密码")
  private String password;
  @ApiModelProperty("电话")
  private String phone;
  @ApiModelProperty("邮箱")
  private String email;

}
