package net.bbq.falsework.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import net.bbq.falsework.dto.TokenUser;
import net.bbq.falsework.service.TokenService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Component
public class SecurityUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        SecurityUtils.applicationContext = context;
    }

    /**
     * 获取当前登录用户信息
     */
    public static TokenUser getCurrentUser() {
        try {
            TokenService tokenService = getBean(TokenService.class);
            String token = extractTokenFromRequest();
            if (token == null) {
                return null;
            }
            return tokenService.getUserByToken(token);
        } catch (Exception e) {
            log.error("Failed to get current user", e);
            return null;
        }
    }

    /**
     * 获取当前登录用户ID
     */
    public static Long getCurrentUserId() {
        TokenUser user = getCurrentUser();
        return user != null ? user.getUserId() : null;
    }

    /**
     * 获取当前登录用户名
     */
    public static String getCurrentUsername() {
        TokenUser user = getCurrentUser();
        return user != null ? user.getUsername() : null;
    }

    /**
     * 获取当前登录用户角色
     */
    public static String getCurrentUserRole() {
        TokenUser user = getCurrentUser();
        return user != null ? user.getRole() : null;
    }

    /**
     * 检查当前用户是否已登录
     */
    public static boolean isAuthenticated() {
        return getCurrentUser() != null;
    }

    /**
     * 从请求中提取token
     */
    private static String extractTokenFromRequest() {
        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return null;
            }

            HttpServletRequest request = attributes.getRequest();
            String bearerToken = request.getHeader("Authorization");

            if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                return bearerToken.substring(7);
            }

            // 也尝试从请求参数中获取token
            return request.getParameter("token");
        } catch (Exception e) {
            log.error("Failed to extract token from request", e);
            return null;
        }
    }

    /**
     * 从Spring容器中获取Bean
     */
    private static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }
}