package com.ruoyi.smoking_identification.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.smoking_identification.domain.entity.Student;
import com.ruoyi.smoking_identification.domain.vo.StudentListVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {

    /**
     * 根据班级查询学生列表
     * @param banji
     * @return
     */
    List<StudentListVo> listByClass(String banji);

    /**
     * 通过姓名查询学生id
     * @param name
     * @return
     */
    Long getIdByName(String name);
}
