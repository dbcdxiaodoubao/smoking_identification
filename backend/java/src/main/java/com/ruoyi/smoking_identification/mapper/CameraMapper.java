package com.ruoyi.smoking_identification.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.smoking_identification.domain.entity.Camera;
import com.ruoyi.smoking_identification.domain.vo.CameraDtlVo;
import com.ruoyi.smoking_identification.domain.vo.CameraListVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CameraMapper extends BaseMapper<Camera> {

    /**
     * 查询摄像头信息列表
     * @return
     */
    List<CameraListVo> list();

    /**
     * 通过id查询摄像头详情
     * @param cameraId
     * @return
     */
    CameraDtlVo dtl(Long cameraId);
}
