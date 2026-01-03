package net.bbq.falsework.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "客户端密钥登录请求")
@Data
public class ClientLoginRequest {

    @Schema(description = "客户端ID", example = "client_app_001", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "客户端ID不能为空")
    private String clientId;

    @Schema(description = "客户端密钥", example = "secret_key_abc123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "客户端密钥不能为空")
    private String clientSecret;
}
