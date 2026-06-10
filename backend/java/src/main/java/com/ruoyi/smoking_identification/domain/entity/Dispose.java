package com.ruoyi.smoking_identification.domain.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Dispose {

  @TableId(type = IdType.AUTO)
  private Long disposeId;
  private Long userId;
  private Long incidentId;
  private java.sql.Timestamp createTime;
}
