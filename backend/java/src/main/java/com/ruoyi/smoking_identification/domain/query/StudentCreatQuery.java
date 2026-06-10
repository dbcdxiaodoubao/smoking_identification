package com.ruoyi.smoking_identification.domain.query;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("学生创建实体")
public class StudentCreatQuery {

  @ApiModelProperty(value = "姓名",required = true)
  @NotBlank(message = "学生姓名不能为空")
  private String name;
  @ApiModelProperty(value = "班级",required = true)
  @NotBlank(message = "班级不能为空")
  private String banji;
  @ApiModelProperty(value = "性别",required = true)
  @NotNull(message = "性别")
  private Long sex;

}
