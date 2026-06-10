package com.ruoyi.smoking_identification.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("趋势实体")
public class LocationListVo {

    @ApiModelProperty("所处位置")
    private String location;
    @ApiModelProperty("条数")
    private Long incidentCount;

}
