package com.ruoyi.smoking_identification.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.smoking_identification.utils.AjaxResult;
import com.ruoyi.smoking_identification.utils.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 构造方法注入依赖
    public JwtAuthenticationFilter(JwtTokenUtil jwtTokenUtil, UserDetailsService userDetailsService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
    }

    /**
     * 强制排除登录/注册接口，不经过JWT过滤
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestURI = request.getRequestURI();
        log.debug("当前请求URI：{}", requestURI);

        // 排除登录/注册接口
        boolean isAuthPath = "/user/login".equals(requestURI)
                || "/user/register".equals(requestURI)
                || "/api/user/login".equals(requestURI)
                || "/api/user/register".equals(requestURI);
        // 排除 Knife4j/Swagger 所有相关路径（补充遗漏路径）
        boolean isSwaggerPath = requestURI.startsWith("/swagger-ui/")
                || requestURI.equals("/favicon.ico")// 包含 /swagger-ui/index.html
                || requestURI.equals("/swagger-ui.html") // 旧版Swagger入口
                || requestURI.startsWith("/swagger-resources/")
                || requestURI.equals("/swagger-resources")
                || requestURI.startsWith("/v2/api-docs")
                || requestURI.startsWith("/v3/api-docs/")
                || requestURI.equals("/v3/api-docs")
                || requestURI.startsWith("/api-docs/") // 补充：Knife4j文档数据接口
                || requestURI.startsWith("/webjars/")
                || requestURI.equals("/doc.html"); // Knife4j默认入口

        boolean isImagePath = requestURI.startsWith("/images/")
                ||requestURI.startsWith("/images");

        return isAuthPath || isSwaggerPath || isImagePath;
    }

    /**
     * 核心过滤逻辑（无需 Bearer 前缀）
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 1. 从请求头获取Token（直接取值，无需 Bearer 前缀）
        // 可选：请求头名称也可以改为 "token"，对应 request.getHeader("token")
        String jwtToken = request.getHeader("Authorization");
        String username = null;
        boolean isTokenValid = true;
        String errorMsg = null;

        // 2. 判断Token是否存在
        if (jwtToken == null || jwtToken.trim().isEmpty()) {
            isTokenValid = false;
            errorMsg = "请求头中缺少有效的JWT Token";
        } else {
            try {
                // 3. 直接解析Token获取用户名（无需截取前缀）
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            } catch (Exception e) {
                isTokenValid = false;
                errorMsg = "JWT Token解析失败：" + e.getMessage();
                log.error(errorMsg);
            }

            // 4. 用户名存在且未认证，验证Token有效性
            if (isTokenValid && username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                // 验证Token是否有效（过期、签名错误等）
                if (!jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                    isTokenValid = false;
                    errorMsg = "JWT Token已过期或无效";
                    log.error(errorMsg);
                } else {
                    // Token有效：封装认证信息放入SecurityContext
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }

        // 5. Token无效时，返回JSON提示并终止过滤器链
        if (!isTokenValid) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            AjaxResult<?> errorResult = AjaxResult.unAuth(errorMsg,null);
            try (PrintWriter writer = response.getWriter()) {
                writer.write(objectMapper.writeValueAsString(errorResult));
                writer.flush();
            } catch (Exception e) {
                log.error("返回Token错误响应异常：{}", e.getMessage());
            }
            return;
        }

        // 6. Token有效时，继续执行过滤器链
        filterChain.doFilter(request, response);
    }
}