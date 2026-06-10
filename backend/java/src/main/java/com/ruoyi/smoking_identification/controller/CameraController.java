package com.ruoyi.smoking_identification.controller;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ruoyi.smoking_identification.controller.base.BaseController;
import com.ruoyi.smoking_identification.domain.entity.Camera;
import com.ruoyi.smoking_identification.domain.query.CameraCreateQuety;
import com.ruoyi.smoking_identification.domain.query.CameraUpdateQuety;
import com.ruoyi.smoking_identification.domain.query.PageQuery;
import com.ruoyi.smoking_identification.domain.vo.CameraListVo;
import com.ruoyi.smoking_identification.mapping.CameraMapping;
import com.ruoyi.smoking_identification.service.ICameraService;
import com.ruoyi.smoking_identification.utils.R;
import com.ruoyi.smoking_identification.utils.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/camera")
@Api(tags = "摄像头管理")
public class CameraController extends BaseController {

    @Autowired
    private ICameraService cameraService;

    @GetMapping
    @ApiOperation("查询摄像头列表")
    public TableDataInfo<List<CameraListVo>> list(PageQuery pageQuery) {
        Page<CameraListVo> page = PageHelper.startPage(pageQuery.getPageNum(), pageQuery.getPageSize());

        List<CameraListVo> list = cameraService.listVo();

        return getDataTable(page.getResult(), page.getTotal());
    }

    @GetMapping("/{cameraId}")
    @ApiOperation("查询摄像头详情")
    public R dtl(@PathVariable @Validated Long cameraId){
        return R.ok(cameraService.dtl(cameraId));
    }

    @PostMapping
    @ApiOperation("新增摄像头")
    public R insert(@RequestBody @Validated CameraCreateQuety cameraCreateQuety){

        if(cameraCreateQuety.getLevel()<1 || cameraCreateQuety.getLevel()>3){
            return R.fail("请输入正确的预警等级");
        }

        if(cameraService.save(CameraMapping.INSTANCE.toCreate(cameraCreateQuety))){
            return R.ok();
        }
        return R.fail();
    }

    @PutMapping
    @ApiOperation("更新摄像头")
    public R update(@RequestBody @Validated CameraUpdateQuety cameraUpdateQuety){
        if(cameraUpdateQuety.getLevel()<1 || cameraUpdateQuety.getLevel()>3){
            return R.fail("请输入正确的预警等级");
        }

        System.out.println(cameraUpdateQuety);

        if(cameraService.updateById(CameraMapping.INSTANCE.toUpdate(cameraUpdateQuety))){
            return R.ok();
        }
        return R.fail();
    }

    @DeleteMapping("/{cameraId}")
    @ApiOperation("删除摄像头")
    public R delete(@PathVariable @Validated Integer cameraId){
        if(cameraService.removeById(cameraId)){
            return R.ok();
        }
        return R.fail();
    }
}
