package com.ruoyi.smoking_identification.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ruoyi.smoking_identification.controller.base.BaseController;
import com.ruoyi.smoking_identification.domain.vo.ClassListVo;
import com.ruoyi.smoking_identification.domain.vo.LevelListVo;
import com.ruoyi.smoking_identification.domain.vo.LocationListVo;
import com.ruoyi.smoking_identification.domain.vo.TimeListVo;
import com.ruoyi.smoking_identification.domain.vo.TrendListVo;
import com.ruoyi.smoking_identification.service.IDisposeService;
import com.ruoyi.smoking_identification.service.IIncidentService;
import com.ruoyi.smoking_identification.service.IUserService;
import com.ruoyi.smoking_identification.utils.R;
import com.ruoyi.smoking_identification.utils.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/echart")
@Api(tags = "可视化接口")
public class EChartController extends BaseController {

    @Autowired
    private IIncidentService incidentService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IDisposeService disposeService;

    @GetMapping("/trend")
    @ApiOperation("查询趋势")
    public R<List<TrendListVo>> trend(){
        return R.ok(incidentService.listTrend());
    }

    @GetMapping("/overview")
    @ApiOperation(value = "吸烟行为检测次数", notes = "查询系统吸烟行为检测总数量")
    public R overview(){
        return R.ok(incidentService.count());
    }

    @GetMapping("/location")
    @ApiOperation("查询地区分布")
    public R<List<LocationListVo>> location(){
        return R.ok(incidentService.listLocation());
    }

    @GetMapping("/time")
    @ApiOperation("查询时间分布")
    public R<List<TimeListVo>> time(){
        return R.ok(incidentService.listTime());
    }

    @GetMapping("/level")
    @ApiOperation("查询等级分布")
    public R<List<LevelListVo>> level(){
        return R.ok(incidentService.listLevel());
    }

    @GetMapping("/class")
    @ApiOperation("查询班级分布")
    public R<List<ClassListVo>> clazz(){
        return R.ok(incidentService.listClass());
    }
}
