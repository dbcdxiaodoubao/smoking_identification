package com.ruoyi.smoking_identification.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.smoking_identification.domain.entity.SysUser;
import com.ruoyi.smoking_identification.domain.query.LoginQuery;
import com.ruoyi.smoking_identification.domain.query.RegisterQuery;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<SysUser> {

    /**
     * 登录
     * @param loginQuery
     * @return
     */
    SysUser login(LoginQuery loginQuery);

    /**
     * 通过名字查询
     * @param userName
     * @return
     */
    SysUser selectByUserName(String userName);

    /**
     * 注册
     * @param registerQuery
     * @return
     */
    Integer register(RegisterQuery registerQuery);
}
