package com.ruoyi.smoking_identification.mapping;

import com.ruoyi.smoking_identification.domain.entity.Student;
import com.ruoyi.smoking_identification.domain.entity.SysUser;
import com.ruoyi.smoking_identification.domain.query.StudentCreatQuery;
import com.ruoyi.smoking_identification.domain.vo.StudentDtlVo;
import com.ruoyi.smoking_identification.domain.vo.SysUserDtlVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface StudentMapping {

    StudentMapping INSTANCE = Mappers.getMapper(StudentMapping.class);

    Student toCreat(StudentCreatQuery studentCreatQuery);

    StudentDtlVo toDtlVo(Student student);
}
