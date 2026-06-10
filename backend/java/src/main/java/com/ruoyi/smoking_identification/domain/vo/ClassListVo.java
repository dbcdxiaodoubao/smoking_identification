package com.ruoyi.smoking_identification.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("班级分布实体")
public class ClassListVo {

    @ApiModelProperty("班级名称")
    private String banji;
    @ApiModelProperty("事件数")
    private Long incidentCount;

}
