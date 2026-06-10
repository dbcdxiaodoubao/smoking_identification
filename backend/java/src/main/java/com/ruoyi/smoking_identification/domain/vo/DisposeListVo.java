package com.ruoyi.smoking_identification.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("处理事件信息列表")
public class DisposeListVo {

    @ApiModelProperty("处理事件id")
    private Long disposeId;
    @ApiModelProperty("处理人id")
    private Long userId;
    @ApiModelProperty("预警事件id")
    private Long incidentId;
    @ApiModelProperty("处理时间")
    private java.sql.Timestamp createTime;
}
