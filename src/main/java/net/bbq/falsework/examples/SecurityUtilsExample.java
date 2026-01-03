package net.bbq.falsework.examples;

import lombok.extern.slf4j.Slf4j;
import net.bbq.falsework.dto.TokenUser;
import net.bbq.falsework.util.SecurityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/example")
public class SecurityUtilsExample {

    /**
     * 示例：获取当前用户信息
     */
    @GetMapping("/current-user")
    public Map<String, Object> getCurrentUser() {
        TokenUser user = SecurityUtils.getCurrentUser();

        Map<String, Object> result = new HashMap<>();
        if (user != null) {
            result.put("userId", user.getUserId());
            result.put("username", user.getUsername());
            result.put("role", user.getRole());
        } else {
            result.put("message", "No user logged in");
        }
        return result;
    }

    /**
     * 示例：获取当前用户ID
     */
    @GetMapping("/current-user-id")
    public Map<String, Object> getCurrentUserId() {
        Long userId = SecurityUtils.getCurrentUserId();

        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        return result;
    }

    /**
     * 示例：获取当前用户名
     */
    @GetMapping("/current-username")
    public Map<String, Object> getCurrentUsername() {
        String username = SecurityUtils.getCurrentUsername();

        Map<String, Object> result = new HashMap<>();
        result.put("username", username);
        return result;
    }

    /**
     * 示例：检查用户是否已登录
     */
    @GetMapping("/check-auth")
    public Map<String, Object> checkAuthentication() {
        boolean authenticated = SecurityUtils.isAuthenticated();

        Map<String, Object> result = new HashMap<>();
        result.put("authenticated", authenticated);

        if (authenticated) {
            result.put("userId", SecurityUtils.getCurrentUserId());
            result.put("username", SecurityUtils.getCurrentUsername());
            result.put("role", SecurityUtils.getCurrentUserRole());
        }

        return result;
    }

    /**
     * 示例：在Service层使用
     * 注意：这个方法需要在有HTTP请求上下文的环境中调用
     */
    @GetMapping("/service-example")
    public Map<String, Object> serviceExample() {
        Long userId = SecurityUtils.getCurrentUserId();
        String username = SecurityUtils.getCurrentUsername();

        // 模拟业务逻辑
        Map<String, Object> result = new HashMap<>();
        result.put("message", "User performing action");
        result.put("userId", userId);
        result.put("username", username);

        // 示例：根据当前用户ID查询数据
        // List<Order> orders = orderService.findByUserId(userId);

        return result;
    }
}
