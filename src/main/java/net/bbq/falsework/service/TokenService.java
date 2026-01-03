package net.bbq.falsework.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bbq.falsework.dto.TokenUser;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private final RedissonClient redissonClient;
    private final ObjectMapper objectMapper;

    @Value("${token.expiration:86400}")
    private long tokenExpiration;

    private static final String TOKEN_PREFIX = "auth:token:";
    private static final String REFRESH_TOKEN_PREFIX = "auth:refresh:";

    /**
     * 生成访问令牌并将用户信息保存到Redis
     */
    public String generateAccessToken(TokenUser tokenUser) {
        String token = UUID.randomUUID().toString();
        String key = TOKEN_PREFIX + token;

        try {
            String json = objectMapper.writeValueAsString(tokenUser);
            redissonClient.getBucket(key)
                    .set(json, tokenExpiration, TimeUnit.SECONDS);
            log.info("Generated access token for user: {}", tokenUser.getUsername());
            return token;
        } catch (Exception e) {
            log.error("Failed to save token to Redis", e);
            throw new RuntimeException("Failed to generate token", e);
        }
    }

    /**
     * 生成刷新令牌
     */
    public String generateRefreshToken(Long userId, String username) {
        String refreshToken = UUID.randomUUID().toString();
        String key = REFRESH_TOKEN_PREFIX + refreshToken;

        try {
            TokenUser tokenUser = new TokenUser(userId, username, "USER");
            String json = objectMapper.writeValueAsString(tokenUser);
            // 刷新令牌有效期为7天
            redissonClient.getBucket(key)
                    .set(json, tokenExpiration * 7, TimeUnit.SECONDS);
            log.info("Generated refresh token for user: {}", username);
            return refreshToken;
        } catch (Exception e) {
            log.error("Failed to save refresh token to Redis", e);
            throw new RuntimeException("Failed to generate refresh token", e);
        }
    }

    /**
     * 根据令牌获取用户信息
     */
    public TokenUser getUserByToken(String token) {
        String key = TOKEN_PREFIX + token;

        try {
            String json = (String) redissonClient.getBucket(key).get();
            if (json == null) {
                return null;
            }
            return objectMapper.readValue(json, TokenUser.class);
        } catch (Exception e) {
            log.error("Failed to get user from Redis by token", e);
            return null;
        }
    }

    /**
     * 根据刷新令牌获取用户信息
     */
    public TokenUser getUserByRefreshToken(String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + refreshToken;

        try {
            String json = (String) redissonClient.getBucket(key).get();
            if (json == null) {
                return null;
            }
            return objectMapper.readValue(json, TokenUser.class);
        } catch (Exception e) {
            log.error("Failed to get user from Redis by refresh token", e);
            return null;
        }
    }

    /**
     * 验证令牌是否存在
     */
    public boolean validateToken(String token) {
        String key = TOKEN_PREFIX + token;
        return redissonClient.getBucket(key).isExists();
    }

    /**
     * 验证刷新令牌是否存在
     */
    public boolean validateRefreshToken(String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + refreshToken;
        return redissonClient.getBucket(key).isExists();
    }

    /**
     * 删除令牌（注销）
     */
    public void deleteToken(String token) {
        String key = TOKEN_PREFIX + token;
        redissonClient.getBucket(key).delete();
        log.info("Deleted token: {}", token);
    }

    /**
     * 删除刷新令牌
     */
    public void deleteRefreshToken(String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + refreshToken;
        redissonClient.getBucket(key).delete();
        log.info("Deleted refresh token: {}", refreshToken);
    }

    /**
     * 刷新令牌
     */
    public String refreshToken(String oldRefreshToken) {
        TokenUser tokenUser = getUserByRefreshToken(oldRefreshToken);
        if (tokenUser == null) {
            throw new RuntimeException("Invalid refresh token");
        }

        // 删除旧的刷新令牌
        deleteRefreshToken(oldRefreshToken);

        // 生成新的访问令牌和刷新令牌
        String newAccessToken = generateAccessToken(tokenUser);
        String newRefreshToken = generateRefreshToken(tokenUser.getUserId(), tokenUser.getUsername());

        return newAccessToken;
    }
}