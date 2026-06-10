package com.ruoyi.smoking_identification.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("时间段分布实体")
public class TimeListVo {

    @ApiModelProperty(value = "小时时间段")
    private String hourSlot;

    @ApiModelProperty("条数")
    private Long incidentCount;
}
