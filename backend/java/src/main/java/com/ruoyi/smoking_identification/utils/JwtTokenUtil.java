package com.ruoyi.smoking_identification.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Component
public class JwtTokenUtil {
    // JWT 密钥（建议配置在application.yml中，长度至少32位）
    @Value("${jwt.secret:your-secret-key-32bit-long-12345678}")
    private String secret;

    // JWT 过期时间（单位：秒，这里配置2小时）
    @Value("${jwt.expiration:100086}")
    private long expiration;

    // 生成密钥（基于配置的secret）
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 从Token中获取用户名
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * 从Token中获取过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * 通用方法：从Token中获取指定信息
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 获取Token中的所有Claims（载荷）
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 验证Token是否过期
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * 生成Token（基于UserDetails）
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>(); // 可自定义添加额外信息（如角色）
        return doGenerateToken(claims, userDetails.getUsername());
    }

    /**
     * 构建Token的核心方法
     */
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject) // 用户名作为Subject
                .setIssuedAt(new Date(System.currentTimeMillis())) // 签发时间
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000)) // 过期时间
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // 签名算法
                .compact();
    }

    /**
     * 验证Token有效性（用户名匹配 + 未过期）
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}