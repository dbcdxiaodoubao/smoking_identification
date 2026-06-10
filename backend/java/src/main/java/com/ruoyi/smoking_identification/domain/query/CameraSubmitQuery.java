package com.ruoyi.smoking_identification.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ApiModel("摄像头识别实体")
@Data
public class CameraSubmitQuery {

    @ApiModelProperty("摄像头id")
    @NotNull(message = "摄像头id不能为空")
    private Long CameraId;

    @ApiModelProperty(name = "所处位置",required = true)
    @NotBlank(message = "所在位置不能为空")
    private String location;

    @ApiModelProperty(name = "预警等级",required = true)
    @NotNull(message = "预警等级不能为空")
    private Long level;

    @ApiModelProperty(name = "摄像头名称",required = true)
    @NotBlank(message = "摄像头名称不能为空")
    private String cameraName;
}
