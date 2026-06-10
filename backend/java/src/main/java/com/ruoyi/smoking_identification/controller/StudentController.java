package com.ruoyi.smoking_identification.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ruoyi.smoking_identification.controller.base.BaseController;
import com.ruoyi.smoking_identification.domain.entity.Student;
import com.ruoyi.smoking_identification.domain.query.PageQuery;
import com.ruoyi.smoking_identification.domain.query.StudentCreatQuery;
import com.ruoyi.smoking_identification.domain.vo.StudentListVo;
import com.ruoyi.smoking_identification.mapping.StudentMapping;
import com.ruoyi.smoking_identification.service.IStudentService;
import com.ruoyi.smoking_identification.utils.R;
import com.ruoyi.smoking_identification.utils.TableDataInfo;
import com.ruoyi.smoking_identification.utils.UploadUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/student")
@Api(tags = "学生管理")
public class StudentController extends BaseController {

    @Autowired
    private IStudentService studentService;

    @Autowired
    private UploadUtil uploadUtil;

    @GetMapping
    @ApiOperation("查询学生信息列表")
    public TableDataInfo<List<StudentListVo>> list(PageQuery pageQuery,String banji){
        Page<Object> page = PageHelper.startPage(pageQuery.getPageNum(), pageQuery.getPageSize());

        List<StudentListVo> list = studentService.listByClass(banji);

        return getDataTable(page.getResult(),page.getTotal());
    }

    @GetMapping("/{studentId}")
    @ApiOperation("查询学生详情")
    public R dtl(@PathVariable @Validated Long studentId){
        return R.ok(StudentMapping.INSTANCE.toDtlVo(studentService.getById(studentId)));
    }

    @PostMapping
    @ApiOperation("新增学生")
    public R insert(StudentCreatQuery studentCreatQuery){
        studentService.save(StudentMapping.INSTANCE.toCreat(studentCreatQuery));
        return R.ok();
    }

    @PostMapping("/face")
    @ApiOperation("上传人脸")
    public R face(@RequestParam("image") MultipartFile imageFile
            , @RequestParam("studentId") Long studentId ) throws IOException {
        Student s = studentService.getById(studentId);

        String url = uploadUtil.uploadface(imageFile,s.getName());

        s.setPictureUrl(url);

        studentService.updateById(s);

        return R.ok();
    }

    @DeleteMapping("/{studentId}")
    @ApiOperation("删除学生")
    public R delete(@PathVariable @Validated Long studentId){
        studentService.removeById(studentId);
        return R.ok();
    }
}
