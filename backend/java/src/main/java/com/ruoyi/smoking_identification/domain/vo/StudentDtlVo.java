package com.ruoyi.smoking_identification.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("学生详情实体")
public class StudentDtlVo {

  @ApiModelProperty("学生id")
  private Long studentId;
  @ApiModelProperty("学生姓名")
  private String name;
  @ApiModelProperty("班级")
  private String banji;
  @ApiModelProperty("性别")
  private Long sex;
  @ApiModelProperty("人脸路径")
  private String pictureUrl;

}
