package com.ruoyi.smoking_identification.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Camera {

  @TableId(type = IdType.AUTO)
  private Long cameraId;
  private String location;
  private String rtsp;
  private String dtl;
  private Long level;
  private String cameraName;
}
