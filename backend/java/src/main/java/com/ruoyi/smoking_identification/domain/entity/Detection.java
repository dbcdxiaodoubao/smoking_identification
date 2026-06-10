package com.ruoyi.smoking_identification.domain.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("检查结果")
public class Detection {
    @ApiModelProperty("检查结果")
    public String class_name;
    @ApiModelProperty("置信值")
    public double confidence;
    @ApiModelProperty("框的坐标")
    public double x, y, w, h;
}
