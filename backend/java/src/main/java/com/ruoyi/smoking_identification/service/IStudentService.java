package com.ruoyi.smoking_identification.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.smoking_identification.domain.entity.Student;
import com.ruoyi.smoking_identification.domain.vo.StudentListVo;

import java.util.List;


public interface IStudentService extends IService<Student> {

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
