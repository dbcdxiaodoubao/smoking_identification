package com.ruoyi.smoking_identification.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

@Data
@ApiModel("学生列表")
public class StudentListVo {

  @ApiModelProperty("学生id")
  private Long studentId;
  @ApiModelProperty("姓名")
  private String name;
  @ApiModelProperty("班级")
  private String banji;
  @ApiModelProperty("性别（1代表男生，2代表女生）")
  private Long sex;
  @ApiModelProperty("人脸图片路径")
  private String pictureUrl;
  @ApiModelProperty("总事件数")
  private Long incidentCount;

}
