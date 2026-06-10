package com.ruoyi.smoking_identification.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.smoking_identification.domain.entity.SysUser;
import com.ruoyi.smoking_identification.domain.query.LoginQuery;
import com.ruoyi.smoking_identification.domain.query.RegisterQuery;


public interface IUserService extends IService<SysUser> {

    /**
     * 登录
     * @param loginQuery
     * @return
     */
    SysUser login(LoginQuery loginQuery);

    /**
     * 注册
     * @param registerQuery
     * @return
     */
    Integer register(RegisterQuery registerQuery);

    /**
     * 通过名字查询
     * @param userName
     * @return
     */
    SysUser selectByUserName(String userName);
}
