package com.ruoyi.smoking_identification.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("预警事件信息列表")
public class IncidentListVo {

  @ApiModelProperty("预警事件id")
  private Long incidentId;

  @ApiModelProperty("所在位置")
  private String location;

  @ApiModelProperty("图片路径")
  private String pictureUrl;

  @ApiModelProperty("发生时间")
  private java.sql.Timestamp createTime;

  @ApiModelProperty("状态")
  private Long status;

  @ApiModelProperty("学生姓名")
  private String name;

  @ApiModelProperty("摄像头id")
  private Long cameraId;

  @ApiModelProperty("预警等级(1-3代表低、中、高危险)")
  private Long level;

  @ApiModelProperty("学生id")
  private Long studentId;
}
