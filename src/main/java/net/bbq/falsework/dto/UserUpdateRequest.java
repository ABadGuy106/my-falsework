package net.bbq.falsework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "用户更新请求")
@Data
public class UserUpdateRequest {

    @Schema(description = "用户名", example = "john_doe_updated")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    private String username;

    @Schema(description = "邮箱地址", example = "john.updated@example.com")
    @Email(message = "邮箱格式不正确")
    private String email;

    @Schema(description = "密码", example = "newpassword123")
    @Size(min = 6, max = 100, message = "密码长度必须在6-100个字符之间")
    private String password;
}