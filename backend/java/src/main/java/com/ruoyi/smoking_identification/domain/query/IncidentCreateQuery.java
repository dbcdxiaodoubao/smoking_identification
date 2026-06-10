package com.ruoyi.smoking_identification.domain.query;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("预警新增实体")
public class IncidentCreateQuery {

  @ApiModelProperty(name = "所处位置",required = true)
  @NotBlank(message = "所处位置不能为空")
  private String location;

  @ApiModelProperty(name = "图片路径",required = true)
  @NotBlank(message = "图片路径不能为空")
  private String pictureUrl;

  @ApiModelProperty(name = "摄像头id",required = true)
  @NotNull(message = "摄像头id不呢为空")
  private Long cameraId;

  @ApiModelProperty(value = "学生id",required = true)
  @NotNull(message = "学生id不能为空")
  private Long studentId;
}
