package com.ruoyi.smoking_identification.mapping;

import com.ruoyi.smoking_identification.domain.entity.Camera;
import com.ruoyi.smoking_identification.domain.query.CameraCreateQuety;
import com.ruoyi.smoking_identification.domain.query.CameraUpdateQuety;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CameraMapping {

    CameraMapping INSTANCE = Mappers.getMapper(CameraMapping.class);

    Camera toCreate(CameraCreateQuety cameraCreateQuety);

    Camera toUpdate(CameraUpdateQuety cameraUpdateQuety);
}
