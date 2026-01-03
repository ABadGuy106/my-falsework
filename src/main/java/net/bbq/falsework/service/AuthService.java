package net.bbq.falsework.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bbq.falsework.dto.TokenUser;
import net.bbq.falsework.dto.auth.AuthResponse;
import net.bbq.falsework.dto.auth.ClientLoginRequest;
import net.bbq.falsework.dto.auth.LoginRequest;
import net.bbq.falsework.dto.auth.RegisterRequest;
import net.bbq.falsework.entity.User;
import net.bbq.falsework.mapper.UserMapper;
import net.bbq.falsework.security.CustomUserDetailsService;
import net.bbq.falsework.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Value("${token.expiration:86400}")
    private long tokenExpiration;

    /**
     * 用户名密码登录
     */
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, userDetails.getUsername())
        );

        // 创建TokenUser对象
        TokenUser tokenUser = new TokenUser(user.getId(), user.getUsername(), "USER");

        // 生成UUID作为token，并保存到Redis
        String accessToken = tokenService.generateAccessToken(tokenUser);
        String refreshToken = tokenService.generateRefreshToken(user.getId(), user.getUsername());

        log.info("User {} logged in successfully", user.getUsername());

        return new AuthResponse(
                accessToken,
                "Bearer",
                refreshToken,
                tokenExpiration,
                user.getId(),
                user.getUsername(),
                "USER"
        );
    }

    /**
     * 客户端密钥登录
     */
    public AuthResponse clientLogin(ClientLoginRequest request) {
        UserDetails userDetails = userDetailsService.loadUserByClientSecret(request.getClientSecret());

        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, userDetails.getUsername())
        );

        if (user == null) {
            throw new RuntimeException("Invalid client credentials");
        }

        String accessToken = tokenProvider.generateClientToken(
                request.getClientId(),
                user.getId()
        );

        String refreshToken = tokenProvider.generateRefreshToken(
                user.getId(),
                user.getUsername()
        );

        log.info("Client {} logged in successfully", request.getClientId());

        return new AuthResponse(
                accessToken,
                "Bearer",
                refreshToken,
                tokenProvider.getJwtExpiration() / 1000,
                user.getId(),
                user.getUsername(),
                "CLIENT"
        );
    }

    /**
     * 刷新令牌
     */
    public AuthResponse refreshToken(String refreshToken) {
        // 验证refresh token是否在Redis中存在
        TokenUser tokenUser = tokenService.getUserByRefreshToken(refreshToken);
        if (tokenUser == null) {
            throw new RuntimeException("Invalid refresh token");
        }

        User user = userMapper.selectById(tokenUser.getUserId());
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // 生成新的访问令牌和刷新令牌
        String newAccessToken = tokenService.generateAccessToken(tokenUser);
        String newRefreshToken = tokenService.generateRefreshToken(user.getId(), user.getUsername());

        // 删除旧的刷新令牌
        tokenService.deleteRefreshToken(refreshToken);

        log.info("Token refreshed for user {}", user.getUsername());

        return new AuthResponse(
                newAccessToken,
                "Bearer",
                newRefreshToken,
                tokenExpiration,
                user.getId(),
                user.getUsername(),
                tokenUser.getRole()
        );
    }

    /**
     * 用户注册
     */
    public AuthResponse register(RegisterRequest request) {
        // 验证密码确认
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("两次输入的密码不一致");
        }

        // 检查用户名是否已存在
        User existUser = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, request.getUsername())
        );
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查邮箱是否已存在
        existUser = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getEmail, request.getEmail())
        );
        if (existUser != null) {
            throw new RuntimeException("邮箱已被注册");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setClientSecret(generateClientSecret());
        user.setEnabled(true);
        user.setRoles("USER");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userMapper.insert(user);

        log.info("User {} registered successfully", user.getUsername());

        // 注册成功后自动登录，返回UUID token
        TokenUser tokenUser = new TokenUser(user.getId(), user.getUsername(), "USER");
        String accessToken = tokenService.generateAccessToken(tokenUser);
        String refreshToken = tokenService.generateRefreshToken(user.getId(), user.getUsername());

        return new AuthResponse(
                accessToken,
                "Bearer",
                refreshToken,
                tokenExpiration,
                user.getId(),
                user.getUsername(),
                "USER"
        );
    }

    /**
     * 检查用户名是否已存在
     */
    public boolean checkUsernameExists(String username) {
        return userMapper.selectCount(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, username)
        ) > 0;
    }

    /**
     * 检查邮箱是否已存在
     */
    public boolean checkEmailExists(String email) {
        return userMapper.selectCount(
                new LambdaQueryWrapper<User>()
                        .eq(User::getEmail, email)
        ) > 0;
    }

    /**
     * 生成客户端密钥
     */
    private String generateClientSecret() {
        return UUID.randomUUID().toString().replace("-", "") +
               UUID.randomUUID().toString().replace("-", "");
    }
}
