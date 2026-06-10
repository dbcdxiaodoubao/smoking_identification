package com.ruoyi.smoking_identification.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ruoyi.smoking_identification.controller.base.BaseController;
import com.ruoyi.smoking_identification.domain.query.PageQuery;
import com.ruoyi.smoking_identification.domain.vo.DisposeListVo;
import com.ruoyi.smoking_identification.service.IDisposeService;
import com.ruoyi.smoking_identification.utils.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dispose")
@Api(tags = "处理事件管理")
public class DisposeController extends BaseController {

    @Autowired
    private IDisposeService disposeService;

    @GetMapping
    @ApiOperation("查询处理事件列表")
    public TableDataInfo<List<DisposeListVo>> list(PageQuery pageQuery){
        Page<Object> page = PageHelper.startPage(pageQuery.getPageNum(), pageQuery.getPageSize());

        List<DisposeListVo> list = disposeService.selectlist();

        return getDataTable(page.getResult(),page.getTotal());
    }
}
