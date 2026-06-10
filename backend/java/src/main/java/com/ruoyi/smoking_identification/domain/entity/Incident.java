package com.ruoyi.smoking_identification.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Incident {

  @TableId(type = IdType.AUTO)
  private Long incidentId;
  private String location;
  private String pictureUrl;
  private java.sql.Timestamp createTime;
  private Long status;
  private Long cameraId;
  private Long studentId;
}
