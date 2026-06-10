package com.ruoyi.smoking_identification.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.smoking_identification.domain.entity.Camera;
import com.ruoyi.smoking_identification.domain.vo.CameraDtlVo;
import com.ruoyi.smoking_identification.domain.vo.CameraListVo;

import java.util.List;

public interface ICameraService extends IService<Camera> {

    /**
     * 查询摄像头信息列表
     * @return
     */
    List<CameraListVo> listVo();

    /**
     * 通过id查询摄像头详情
     * @param cameraId
     * @return
     */
    CameraDtlVo dtl(Long cameraId);
}
