package com.ruoyi.smoking_identification.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ruoyi.smoking_identification.controller.base.BaseController;
import com.ruoyi.smoking_identification.domain.query.DisposeCreateQuery;
import com.ruoyi.smoking_identification.domain.query.IncidentCreateQuery;
import com.ruoyi.smoking_identification.domain.query.PageQuery;
import com.ruoyi.smoking_identification.domain.vo.IncidentListVo;
import com.ruoyi.smoking_identification.service.IDisposeService;
import com.ruoyi.smoking_identification.service.IIncidentService;
import com.ruoyi.smoking_identification.service.IUserService;
import com.ruoyi.smoking_identification.utils.JwtTokenUtil;
import com.ruoyi.smoking_identification.utils.R;
import com.ruoyi.smoking_identification.utils.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/incident")
@Api(tags = "预警管理")
public class IncidentController extends BaseController {

    @Autowired
    private IIncidentService incidentService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IDisposeService disposeService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping
    @ApiOperation("新增预警")
    public R insert(@RequestBody @Validated IncidentCreateQuery incidentCreateQuery){
        if(incidentService.insertIncident(incidentCreateQuery)==1){
            return R.ok();
        }
        return R.fail();
    }

    @GetMapping()
    @ApiOperation("获取预警事件信息列表")
    public TableDataInfo<List<IncidentListVo>> list(Long status, PageQuery pageQuery){
        Page<IncidentListVo> page = PageHelper.startPage(pageQuery.getPageNum(), pageQuery.getPageSize());

        List<IncidentListVo> list = incidentService.list(status);

        return getDataTable(page.getResult(),page.getTotal());
    }

    @GetMapping("/dtl/{incidentId}")
    @ApiOperation("查询事件详情")
    public R dtl(@Validated @PathVariable Long incidentId){
        return R.ok(incidentService.getById(incidentId));
    }

    @PostMapping("/{incidentId}")
    @ApiOperation("处理事件")
    public R dispose(@PathVariable @Validated Long incidentId ,
                     @RequestHeader(value = "Authorization", required = false) String authorizationHeader){

        String userName = jwtTokenUtil.getUsernameFromToken(authorizationHeader);

        Long userId = userService.selectByUserName(userName).getUserId();

        if(incidentService.dispose(incidentId)==1){
            DisposeCreateQuery disposeCreateQuery = new DisposeCreateQuery();
            disposeCreateQuery.setIncidentId(incidentId);
            disposeCreateQuery.setUserId(userId);
            if(disposeService.insert(disposeCreateQuery) == 1){
                return R.ok();
            }
        }
        return R.fail();
    }

    @DeleteMapping("/{incidentId}")
    @ApiOperation("删除预警事件")
    public R delete(@PathVariable @Validated Long incidentId){
        disposeService.deleteByincidentId(incidentId);
        if(incidentService.removeById(incidentId)){
            return R.ok();
        }
        return R.fail();
    }

    @PutMapping("/{incidentId}/student/{studentId}")
    @ApiOperation("绑定学生到预警事件")
    public R bindStudent(@PathVariable @Validated Long incidentId,
                         @PathVariable @Validated Long studentId){
        com.ruoyi.smoking_identification.domain.entity.Incident incident = new com.ruoyi.smoking_identification.domain.entity.Incident();
        incident.setIncidentId(incidentId);
        incident.setStudentId(studentId);
        if(incidentService.updateById(incident)){
            return R.ok();
        }
        return R.fail();
    }
}
