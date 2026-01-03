package net.bbq.falsework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.bbq.falsework.dto.CommonResult;
import net.bbq.falsework.dto.UserCreateRequest;
import net.bbq.falsework.dto.UserUpdateRequest;
import net.bbq.falsework.entity.User;
import net.bbq.falsework.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "用户管理", description = "用户相关接口")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "创建用户", description = "创建新用户，需要提供用户名、邮箱和密码")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "创建成功",
                    content = @Content(schema = @Schema(implementation = CommonResult.class))),
            @ApiResponse(responseCode = "400", description = "参数校验失败"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping
    public CommonResult<User> createUser(@Valid @RequestBody UserCreateRequest request) {
        User user = userService.createUser(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        );
        return CommonResult.success("用户创建成功", user);
    }

    @Operation(summary = "根据ID查询用户", description = "通过用户ID获取用户详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @GetMapping("/{id}")
    public CommonResult<User> getUserById(
            @Parameter(description = "用户ID", example = "1", required = true)
            @PathVariable("id") Long id) {
        return userService.getUserById(id)
                .map(user -> CommonResult.success("查询成功", user))
                .orElseGet(() -> CommonResult.error(404, "用户不存在"));
    }

    @Operation(summary = "根据用户名查询", description = "通过用户名获取用户信息")
    @GetMapping("/username/{username}")
    public CommonResult<User> getUserByUsername(
            @Parameter(description = "用户名", example = "john_doe", required = true)
            @PathVariable("username") String username) {
        return userService.getUserByUsername(username)
                .map(user -> CommonResult.success("查询成功", user))
                .orElseGet(() -> CommonResult.error(404, "用户不存在"));
    }

    @Operation(summary = "根据邮箱查询", description = "通过邮箱获取用户信息")
    @GetMapping("/email")
    public CommonResult<User> getUserByEmail(
            @Parameter(description = "邮箱地址", example = "john@example.com", required = true)
            @RequestParam("email") String email) {
        return userService.getUserByEmail(email)
                .map(user -> CommonResult.success("查询成功", user))
                .orElseGet(() -> CommonResult.error(404, "用户不存在"));
    }

    @Operation(summary = "获取所有用户", description = "获取系统中所有用户的列表")
    @GetMapping
    public CommonResult<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return CommonResult.success("查询成功", users);
    }

    @Operation(summary = "更新用户信息", description = "根据用户ID更新用户信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "400", description = "参数校验失败"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @PutMapping("/{id}")
    public CommonResult<User> updateUser(
            @Parameter(description = "用户ID", example = "1", required = true)
            @PathVariable("id") Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        try {
            User user = userService.updateUser(
                    id,
                    request.getUsername(),
                    request.getEmail(),
                    request.getPassword()
            );
            return CommonResult.success("用户更新成功", user);
        } catch (RuntimeException e) {
            return CommonResult.error(404, e.getMessage());
        }
    }

    @Operation(summary = "删除用户", description = "根据用户ID删除用户")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @DeleteMapping("/{id}")
    public CommonResult<Void> deleteUser(
            @Parameter(description = "用户ID", example = "1", required = true)
            @PathVariable("id") Long id) {
        userService.deleteUser(id);
        return CommonResult.success("用户删除成功", null);
    }
}