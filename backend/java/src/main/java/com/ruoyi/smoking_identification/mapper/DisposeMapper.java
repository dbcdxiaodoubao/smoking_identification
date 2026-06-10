package com.ruoyi.smoking_identification.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.smoking_identification.domain.entity.Dispose;
import com.ruoyi.smoking_identification.domain.query.DisposeCreateQuery;
import com.ruoyi.smoking_identification.domain.vo.DisposeListVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DisposeMapper extends BaseMapper<Dispose> {

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
    List<DisposeListVo> list();
}
