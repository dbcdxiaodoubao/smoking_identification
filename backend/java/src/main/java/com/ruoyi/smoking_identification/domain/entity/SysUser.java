package com.ruoyi.smoking_identification.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class SysUser {

  @TableId(type = IdType.AUTO)
  private Long userId;
  private String userName;
  private String password;
  private String phone;
  private java.sql.Timestamp createTime;
  private Long status;
  private String email;

}
