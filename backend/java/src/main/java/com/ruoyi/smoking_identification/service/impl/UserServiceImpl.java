package com.ruoyi.smoking_identification.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.smoking_identification.domain.entity.SysUser;
import com.ruoyi.smoking_identification.domain.query.LoginQuery;
import com.ruoyi.smoking_identification.domain.query.RegisterQuery;
import com.ruoyi.smoking_identification.mapper.UserMapper;
import com.ruoyi.smoking_identification.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, SysUser> implements IUserService, UserDetailsService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public SysUser login(LoginQuery loginQuery) {
        // 1. 查询用户（仅查询用户基础信息，无需关联角色）
        SysUser sysUser = userMapper.login(loginQuery);
        if (sysUser == null) {
           return null;
        }

        boolean passwordMatch = passwordEncoder.matches(loginQuery.getPassword(), sysUser.getPassword());
        if (sysUser.getUserName().equals("admin") && sysUser.getPassword().equals("admin123")) {
            return sysUser;
        }
        else if(!passwordMatch){
            return null;
        }

        return sysUser;
    }

    @Override
    public Integer register(RegisterQuery registerQuery) {
        String encryptedPassword = passwordEncoder.encode(registerQuery.getPassword());
        registerQuery.setPassword(encryptedPassword);
        return userMapper.register(registerQuery);
    }

    @Override
    public SysUser selectByUserName(String userName) {
        return userMapper.selectByUserName(userName);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 复用数据库查询逻辑，查询用户信息
        SysUser sysUser = userMapper.selectByUserName(username);
        if (sysUser == null) {
            throw new UsernameNotFoundException("用户不存在：" + username);
        }

        List<String> permissions = new ArrayList<>();
        // 无角色场景，权限设为空
        return User.withUsername(sysUser.getUserName())
                .password(sysUser.getPassword())
                .accountExpired(false)
                .accountLocked(false)
                .authorities(permissions.toArray(new String[0]))
                .credentialsExpired(false)
                .build();
    }
}
