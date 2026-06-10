package com.ruoyi.smoking_identification.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Student {

  @TableId(type = IdType.AUTO)
  private Long studentId;
  private String name;
  private String banji;
  private Long sex;
  private String pictureUrl;

}
