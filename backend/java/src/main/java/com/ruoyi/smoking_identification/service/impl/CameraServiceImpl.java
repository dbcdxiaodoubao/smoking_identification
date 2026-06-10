package com.ruoyi.smoking_identification.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.smoking_identification.domain.entity.Camera;
import com.ruoyi.smoking_identification.domain.vo.CameraDtlVo;
import com.ruoyi.smoking_identification.domain.vo.CameraListVo;
import com.ruoyi.smoking_identification.mapper.CameraMapper;
import com.ruoyi.smoking_identification.service.ICameraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CameraServiceImpl extends ServiceImpl<CameraMapper,Camera> implements ICameraService {

    @Autowired
    private CameraMapper cameraMapper;

    public List<CameraListVo> listVo(){
        return cameraMapper.list();
    }

    @Override
    public CameraDtlVo dtl(Long cameraId) {
        return cameraMapper.dtl(cameraId);
    }
}
