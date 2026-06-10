package com.ruoyi.smoking_identification.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("摄像头详情实体")
public class CameraDtlVo {

  @ApiModelProperty("摄像头id")
  private Long CameraId;

  @ApiModelProperty("所在位置")
  private String location;

  @ApiModelProperty("RTSP地址")
  private String rtsp;

  @ApiModelProperty("详情")
  private String dtl;

  @ApiModelProperty("预警等级(1-3代表低、中、高危险)")
  private Long level;

  @ApiModelProperty("摄像头名称")
  private String cameraName;
}
