package com.ruoyi.smoking_identification.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("趋势实体")
public class LevelListVo {

    @ApiModelProperty("预警等级(1-3代表低、中、高危险)")
    private Long level;

    @ApiModelProperty("条数")
    private Long incidentCount;

}
