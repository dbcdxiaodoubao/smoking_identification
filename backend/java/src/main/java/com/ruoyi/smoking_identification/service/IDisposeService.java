package com.ruoyi.smoking_identification.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.smoking_identification.domain.entity.Dispose;
import com.ruoyi.smoking_identification.domain.query.DisposeCreateQuery;
import com.ruoyi.smoking_identification.domain.vo.DisposeListVo;

import java.util.List;

public interface IDisposeService extends IService<Dispose> {

    /**
     * 新增处理
     * @param disposeCreateQuery
     * @return
     */
    Integer insert(DisposeCreateQuery disposeCreateQuery);

    /**
     * 通过incidentId删除处理
     * @param incidentId
     * @return
     */
    Integer deleteByincidentId(Long incidentId);


    /**
     * 查询处理事件信息列表
     * @return
     */
    List<DisposeListVo> selectlist();
}
