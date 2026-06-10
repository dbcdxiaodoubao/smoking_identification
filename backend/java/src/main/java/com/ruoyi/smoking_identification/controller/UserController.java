package com.ruoyi.smoking_identification.controller;

import com.ruoyi.smoking_identification.controller.base.BaseController;
import com.ruoyi.smoking_identification.domain.entity.SysUser;
import com.ruoyi.smoking_identification.domain.query.LoginQuery;
import com.ruoyi.smoking_identification.domain.query.RegisterQuery;
import com.ruoyi.smoking_identification.domain.vo.SysUserDtlVo;
import com.ruoyi.smoking_identification.mapping.SysUserMapping;
import com.ruoyi.smoking_identification.service.IUserService;
import com.ruoyi.smoking_identification.utils.JwtTokenUtil;
import com.ruoyi.smoking_identification.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/user")
@Api(tags = "用户管理接口")
public class UserController extends BaseController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private IUserService userService;

    @PostMapping("/login")
    @ApiOperation("登录")
    public R login(@RequestBody @Validated LoginQuery loginQuery) {
        SysUser sysUser = userService.login(loginQuery);

        if (sysUser == null) {
            return R.fail("账号或密码错误");
        }

        // 2. 构建UserDetails（无角色，仅需基础用户信息，权限设为空即可）
        UserDetails userDetails = User.withUsername(sysUser.getUserName())
                .password(sysUser.getPassword()) // 仅用于Token生成，无需明文
                .authorities(Collections.emptyList())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .build();

        // 3. 手动构建已认证对象，存入安全上下文
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities() // 空权限
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 4. 生成JWT Token
        String token = jwtTokenUtil.generateToken(userDetails);


        return R.ok(token);
    }

    @GetMapping("/info")
    @ApiOperation("获取用户信息")
    public R info(@RequestHeader(value = "Authorization", required = false) String authorizationHeader){

        String userName = jwtTokenUtil.getUsernameFromToken(authorizationHeader);

        SysUser sysUser = userService.selectByUserName(userName);

        SysUserDtlVo sysUserDtlVo = new SysUserDtlVo();

        sysUserDtlVo.setUserName(sysUser.getUserName());
        sysUserDtlVo.setPassword(sysUser.getPassword());
        sysUserDtlVo.setEmail(sysUser.getEmail());
        sysUserDtlVo.setPhone(sysUser.getPhone());
        sysUserDtlVo.setUserId(sysUser.getUserId());

        return R.ok(sysUserDtlVo);
    }

    @PostMapping("/register")
    @ApiOperation("注册")
    public R register(@RequestBody @Validated RegisterQuery registerQuery) {

        if(userService.selectByUserName(registerQuery.getUserName()) != null){
            return R.fail("该用户名已经存在");
        }

        if(userService.register(registerQuery) != 1){
            return R.fail("注册失败");
        }
        return R.ok();
    }
}
