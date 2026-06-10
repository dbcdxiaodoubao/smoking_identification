package com.ruoyi.smoking_identification.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.smoking_identification.domain.entity.Student;
import com.ruoyi.smoking_identification.domain.vo.StudentListVo;
import com.ruoyi.smoking_identification.mapper.StudentMapper;
import com.ruoyi.smoking_identification.service.IStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements IStudentService {

    @Autowired
    private StudentMapper studentMapper;

    @Override
    public List<StudentListVo> listByClass(String banji) {
        return studentMapper.listByClass(banji);
    }

    @Override
    public Long getIdByName(String name) {
        return studentMapper.getIdByName(name);
    }
}
