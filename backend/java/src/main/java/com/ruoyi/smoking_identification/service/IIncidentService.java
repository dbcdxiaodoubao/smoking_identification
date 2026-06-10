package com.ruoyi.smoking_identification.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.smoking_identification.domain.entity.Incident;
import com.ruoyi.smoking_identification.domain.query.IncidentCreateQuery;
import com.ruoyi.smoking_identification.domain.vo.*;
import java.util.List;

public interface IIncidentService extends IService<Incident> {

    /**
     * 新增预警
     * @param incidentCreateQuery
     * @return
     */
    Integer insertIncident(IncidentCreateQuery incidentCreateQuery);

    /**
     * 查询预警事件信息列表
     * @return
     */
    List<IncidentListVo> list(Long status);

    /**
     * 处理事件
     * @param incidentId
     * @return
     */
    Integer dispose(Long incidentId);

    /**
     * 查询趋势
     * @return
     */
    List<TrendListVo> listTrend();

    /**
     * 查询地区分布
     * @return
     */
    List<LocationListVo> listLocation();

    /**
     * 查询时间分布
     * @return
     */
    List<TimeListVo> listTime();

    /**
     * 查询等级分布
     * @return
     */
    List<LevelListVo> listLevel();

    /**
     * 查询班级分布
     * @return
     */
    List<ClassListVo> listClass();
}
