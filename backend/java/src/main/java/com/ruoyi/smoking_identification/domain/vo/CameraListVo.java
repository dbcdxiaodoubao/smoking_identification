package com.ruoyi.smoking_identification.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("摄像头信息列表")
public class CameraListVo {

  @ApiModelProperty("摄像头id")
  private Long CameraId;

  @ApiModelProperty("所在位置")
  private String location;

  @ApiModelProperty("预警等级(1-3代表低、中、高危险)")
  private Long level;

  @ApiModelProperty("摄像头名称")
  private String cameraName;
}
