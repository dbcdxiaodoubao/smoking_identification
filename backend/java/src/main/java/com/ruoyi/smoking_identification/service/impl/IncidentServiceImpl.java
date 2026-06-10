package com.ruoyi.smoking_identification.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.smoking_identification.domain.entity.Incident;
import com.ruoyi.smoking_identification.domain.query.IncidentCreateQuery;
import com.ruoyi.smoking_identification.domain.vo.*;
import com.ruoyi.smoking_identification.mapper.IncidentMapper;
import com.ruoyi.smoking_identification.service.IIncidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class IncidentServiceImpl extends ServiceImpl<IncidentMapper, Incident> implements IIncidentService {

    @Autowired
    private IncidentMapper incidentMapper;

    @Override
    public Integer insertIncident(IncidentCreateQuery incidentCreateQuery) {
        return incidentMapper.insertIncident(incidentCreateQuery);
    }

    @Override
    public List<IncidentListVo> list(Long status) {
        return incidentMapper.list(status);
    }

    @Override
    public Integer dispose(Long incidentId) {
        return incidentMapper.dispose(incidentId);
    }

    @Override
    public List<TrendListVo> listTrend() {
        return incidentMapper.listTrend();
    }

    @Override
    public List<LocationListVo> listLocation() {
        return incidentMapper.listLocation();
    }

    @Override
    public List<TimeListVo> listTime() {
        return incidentMapper.listTime();
    }

    @Override
    public List<LevelListVo> listLevel() {
        return incidentMapper.listLevel();
    }

    @Override
    public List<ClassListVo> listClass() {
        return incidentMapper.listClass();
    }
}
