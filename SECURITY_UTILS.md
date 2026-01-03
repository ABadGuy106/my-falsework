# SecurityUtils 使用说明

## 概述
`SecurityUtils` 是一个工具类，提供静态方法获取当前登录用户的信息。

## 功能方法

### 1. `getCurrentUser()`
获取完整的当前用户信息对象。

```java
TokenUser user = SecurityUtils.getCurrentUser();
if (user != null) {
    Long userId = user.getUserId();
    String username = user.getUsername();
    String role = user.getRole();
}
```

### 2. `getCurrentUserId()`
获取当前登录用户的ID。

```java
Long userId = SecurityUtils.getCurrentUserId();
```

### 3. `getCurrentUsername()`
获取当前登录用户的用户名。

```java
String username = SecurityUtils.getCurrentUsername();
```

### 4. `getCurrentUserRole()`
获取当前登录用户的角色。

```java
String role = SecurityUtils.getCurrentUserRole();
```

### 5. `isAuthenticated()`
检查当前用户是否已登录。

```java
if (SecurityUtils.isAuthenticated()) {
    // 用户已登录
} else {
    // 用户未登录
}
```

## 使用场景

### 在Controller中使用

```java
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @GetMapping
    public Result<List<Order>> getMyOrders() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<Order> orders = orderService.findByUserId(userId);
        return Result.success(orders);
    }

    @PostMapping
    public Result<Order> createOrder(@RequestBody CreateOrderRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        Order order = orderService.create(userId, request);
        return Result.success(order);
    }
}
```

### 在Service中使用

```java
@Service
public class OrderService {

    public Order create(CreateOrderRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();

        Order order = new Order();
        order.setUserId(userId);
        // ... 其他逻辑

        return orderRepository.save(order);
    }
}
```

### 在权限验证中使用

```java
@GetMapping("/admin-only")
public Result<String> adminOnlyEndpoint() {
    String role = SecurityUtils.getCurrentUserRole();

    if (!"ADMIN".equals(role)) {
        throw new AccessDeniedException("Only admin can access");
    }

    return Result.success("Welcome admin");
}
```

### 在条件判断中使用

```java
public void processData() {
    if (SecurityUtils.isAuthenticated()) {
        Long userId = SecurityUtils.getCurrentUserId();
        // 处理已登录用户的逻辑
        log.info("Processing for user: {}", userId);
    } else {
        // 处理匿名用户的逻辑
        log.info("Processing for anonymous user");
    }
}
```

## 注意事项

1. **HTTP请求上下文**：这些方法必须在HTTP请求上下文中调用，即在Controller、Service等由Spring管理的Bean中被调用时才能正常工作。

2. **返回值可能为null**：除了`isAuthenticated()`返回boolean外，其他方法在用户未登录时可能返回`null`，使用时需要进行空值检查。

3. **Token格式**：工具类自动从请求头的`Authorization`字段中提取Bearer token，或从请求参数`token`中获取。

4. **线程安全**：每个请求线程都有独立的SecurityContext，因此是线程安全的。

## 示例API

测试这些方法：

```bash
# 获取当前用户信息
curl -H "Authorization: Bearer YOUR_TOKEN" http://localhost:8080/api/example/current-user

# 获取当前用户ID
curl -H "Authorization: Bearer YOUR_TOKEN" http://localhost:8080/api/example/current-user-id

# 获取当前用户名
curl -H "Authorization: Bearer YOUR_TOKEN" http://localhost:8080/api/example/current-username

# 检查认证状态
curl -H "Authorization: Bearer YOUR_TOKEN" http://localhost:8080/api/example/check-auth
```

## 完整示例

```java
@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @GetMapping
    public Result<Profile> getProfile() {
        // 获取当前用户
        Long userId = SecurityUtils.getCurrentUserId();

        if (userId == null) {
            return Result.error("User not authenticated");
        }

        // 查询用户资料
        Profile profile = profileService.findByUserId(userId);

        return Result.success(profile);
    }

    @PutMapping
    public Result<Profile> updateProfile(@RequestBody UpdateProfileRequest request) {
        // 获取当前用户ID
        Long userId = SecurityUtils.getCurrentUserId();
        String username = SecurityUtils.getCurrentUsername();

        log.info("User {} updating profile", username);

        // 更新资料
        Profile profile = profileService.update(userId, request);

        return Result.success(profile);
    }

    @DeleteMapping("/account")
    public Result<Void> deleteAccount() {
        // 验证用户是否已登录
        if (!SecurityUtils.isAuthenticated()) {
            throw new UnauthorizedException("Please login first");
        }

        Long userId = SecurityUtils.getCurrentUserId();

        // 删除账户
        userService.deleteAccount(userId);

        return Result.success();
    }
}
```