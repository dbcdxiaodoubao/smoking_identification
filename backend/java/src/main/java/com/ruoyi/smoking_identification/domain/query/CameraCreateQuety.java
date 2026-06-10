package com.ruoyi.smoking_identification.domain.query;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("摄像头新增实体")
public class CameraCreateQuety {

  @ApiModelProperty(name = "所处位置",required = true)
  @NotBlank(message = "所在位置不能为空")
  private String location;

  @ApiModelProperty(name = "RTSP地址",required = true)
  @NotBlank(message = "RTSP地址不能为空")
  private String rtsp;

  @ApiModelProperty(name = "详情")
  private String dtl;

  @ApiModelProperty(name = "预警等级",required = true)
  @NotNull(message = "预警等级不能为空")
  private Long level;

  @ApiModelProperty(name = "摄像头名称",required = true)
  @NotBlank(message = "摄像头名称不能为空")
  private String cameraName;
}
