package com.ruoyi.smoking_identification.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

@Data
@ApiModel("事件详情实体")
public class IncidentDtlVo {

  @ApiModelProperty("事件id")
  private Long incidentId;
  @ApiModelProperty("所在位置")
  private String location;
  @ApiModelProperty("图片地址")
  private String pictureUrl;
  @ApiModelProperty("时间")
  private java.sql.Timestamp createTime;
  @ApiModelProperty("状态")
  private Long status;
  @ApiModelProperty("摄像头id")
  private Long cameraId;
}
