package com.ruoyi.smoking_identification.mapping;

import com.ruoyi.smoking_identification.domain.entity.Camera;
import com.ruoyi.smoking_identification.domain.entity.SysUser;
import com.ruoyi.smoking_identification.domain.query.CameraCreateQuety;
import com.ruoyi.smoking_identification.domain.query.CameraUpdateQuety;
import com.ruoyi.smoking_identification.domain.vo.SysUserDtlVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface SysUserMapping {

    SysUserMapping INSTANCE = Mappers.getMapper(SysUserMapping.class);

    SysUserDtlVo dtl(SysUser sysUser);
}
