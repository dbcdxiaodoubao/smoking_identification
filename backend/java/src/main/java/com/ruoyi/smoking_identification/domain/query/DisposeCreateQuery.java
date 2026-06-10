package com.ruoyi.smoking_identification.domain.query;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("新增处理实体")
public class DisposeCreateQuery {

  @ApiModelProperty(name = "处理人id",required = true)
  private Long userId;

  @ApiModelProperty(name = "事件id",required = true)
  private Long incidentId;

}
