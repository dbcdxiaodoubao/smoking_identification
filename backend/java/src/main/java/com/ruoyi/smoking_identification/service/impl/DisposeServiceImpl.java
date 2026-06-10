package com.ruoyi.smoking_identification.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.smoking_identification.domain.entity.Dispose;
import com.ruoyi.smoking_identification.domain.query.DisposeCreateQuery;
import com.ruoyi.smoking_identification.domain.vo.DisposeListVo;
import com.ruoyi.smoking_identification.mapper.DisposeMapper;
import com.ruoyi.smoking_identification.service.IDisposeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class DisposeServiceImpl extends ServiceImpl<DisposeMapper, Dispose> implements IDisposeService {

    @Autowired
    private DisposeMapper disposeMapper;

    @Override
    public Integer insert(DisposeCreateQuery disposeCreateQuery) {
        return disposeMapper.insert(disposeCreateQuery);
    }

    @Override
    public Integer deleteByincidentId(Long incidentId) {
        return disposeMapper.deleteByincidentId(incidentId);
    }

    @Override
    public List<DisposeListVo> selectlist() {
        return disposeMapper.list();
    }


}
