package net.bbq.falsework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.bbq.falsework.dto.auth.AuthResponse;
import net.bbq.falsework.dto.auth.ClientLoginRequest;
import net.bbq.falsework.dto.auth.LoginRequest;
import net.bbq.falsework.dto.auth.RefreshTokenRequest;
import net.bbq.falsework.dto.auth.RegisterRequest;
import net.bbq.falsework.service.AuthService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "认证接口", description = "用户认证相关接口")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "用户注册", description = "注册新用户，注册成功后自动登录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "注册成功并返回令牌"),
            @ApiResponse(responseCode = "400", description = "参数校验失败或用户已存在")
    })
    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @Operation(summary = "用户密码登录", description = "使用用户名和密码进行登录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "登录成功"),
            @ApiResponse(responseCode = "401", description = "认证失败")
    })
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @Operation(summary = "客户端密钥登录", description = "使用客户端ID和密钥进行登录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "登录成功"),
            @ApiResponse(responseCode = "401", description = "认证失败")
    })
    @PostMapping("/client/login")
    public AuthResponse clientLogin(@Valid @RequestBody ClientLoginRequest request) {
        return authService.clientLogin(request);
    }

    @Operation(summary = "刷新令牌", description = "使用刷新令牌获取新的访问令牌")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "刷新成功"),
            @ApiResponse(responseCode = "401", description = "令牌无效")
    })
    @PostMapping("/refresh")
    public AuthResponse refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return authService.refreshToken(request.getRefreshToken());
    }

    @Operation(summary = "检查用户名是否存在", description = "检查用户名是否已被注册")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "检查成功")
    })
    @GetMapping("/check-username")
    public Map<String, Object> checkUsername(
            @Parameter(description = "用户名", required = true)
            @RequestParam("username") String username) {
        boolean exists = authService.checkUsernameExists(username);
        Map<String, Object> result = new HashMap<>();
        result.put("exists", exists);
        result.put("message", exists ? "用户名已存在" : "用户名可用");
        return result;
    }

    @Operation(summary = "检查邮箱是否存在", description = "检查邮箱是否已被注册")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "检查成功")
    })
    @GetMapping("/check-email")
    public Map<String, Object> checkEmail(
            @Parameter(description = "邮箱地址", required = true)
            @RequestParam("email") String email) {
        boolean exists = authService.checkEmailExists(email);
        Map<String, Object> result = new HashMap<>();
        result.put("exists", exists);
        result.put("message", exists ? "邮箱已被注册" : "邮箱可用");
        return result;
    }
}
