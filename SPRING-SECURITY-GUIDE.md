# Spring Security + JWT 认证指南

## 概述

本项目集成了 Spring Security 和 JWT (JSON Web Token) 实现用户认证和授权。支持多种登录方式：
- 用户名密码登录
- 客户端密钥登录

## 核心组件

### 1. JWT Token Provider

`JwtTokenProvider.java` - JWT 令牌生成和验证

**功能：**
- 生成访问令牌
- 生成刷新令牌
- 生成客户端令牌
- 验证令牌
- 解析令牌信息

### 2. Security 配置

`SecurityConfig.java` - Spring Security 配置

**配置内容：**
- CSRF 禁用（使用 JWT）
- 会话管理设置为无状态
- 公开接口配置（登录、刷新令牌、API 文档）
- JWT 认证过滤器
- 密码编码器（BCrypt）

### 3. JWT 认证过滤器

`JwtAuthenticationFilter.java` - 请求拦截和令牌验证

**工作流程：**
1. 从请求头中提取 JWT 令牌
2. 验证令牌有效性
3. 解析用户信息
4. 设置 Security 上下文

### 4. UserDetailsService 实现

`CustomUserDetailsService.java` - 用户加载服务

**方法：**
- `loadUserByUsername()` - 根据用户名加载
- `loadUserById()` - 根据 ID 加载
- `loadUserByClientSecret()` - 根据客户端密钥加载

### 5. 认证服务

`AuthService.java` - 认证业务逻辑

**功能：**
- 用户名密码登录
- 客户端密钥登录
- 刷新令牌

### 6. 认证控制器

`AuthController.java` - 认证相关接口

## API 接口

### 1. 用户名密码登录

**接口：** `POST /api/auth/login`

**请求体：**
```json
{
  "username": "john_doe",
  "password": "password123"
}
```

**响应：**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 86400,
  "userId": 1,
  "username": "john_doe",
  "role": "USER"
}
```

### 2. 客户端密钥登录

**接口：** `POST /api/auth/client/login`

**请求体：**
```json
{
  "clientId": "client_app_001",
  "clientSecret": "secret_key_abc123"
}
```

**响应：**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 86400,
  "userId": 1,
  "username": "john_doe",
  "role": "CLIENT"
}
```

### 3. 刷新令牌

**接口：** `POST /api/auth/refresh`

**请求体：**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**响应：**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 86400,
  "userId": 1,
  "username": "john_doe",
  "role": "USER"
}
```

## 使用 JWT 令牌

### 请求头格式

```
Authorization: Bearer {accessToken}
```

### 示例

```bash
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

## 保护接口

### 方法1：使用 Security 配置

在 `SecurityConfig.java` 中配置：

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/public/**").permitAll()
    .requestMatchers("/api/admin/**").hasRole("ADMIN")
    .anyRequest().authenticated()
)
```

### 方法2：使用方法级注解

```java
@PreAuthorize("hasRole('ADMIN')")
public adminMethod() { }

@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public userMethod() { }

@PreAuthorize("#userId == authentication.principal")
public getUserById(Long userId) { }
```

## 数据库结构

### t_user 表字段

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| username | VARCHAR(50) | 用户名 |
| email | VARCHAR(100) | 邮箱 |
| password | VARCHAR(255) | 密码（BCrypt加密）|
| client_secret | VARCHAR(255) | 客户端密钥 |
| enabled | BOOLEAN | 是否启用 |
| roles | VARCHAR(50) | 角色 |
| created_at | TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 更新时间 |

## 配置说明

### JWT 配置

```properties
# JWT 密钥（生产环境请使用更复杂的密钥）
jwt.secret=mySecretKeyForJWTTokenGenerationMustBeLongEnoughForHS256Algorithm

# 访问令牌有效期（毫秒）- 默认 24 小时
jwt.expiration=86400000

# 刷新令牌有效期（毫秒）- 默认 7 天
jwt.refresh-expiration=604800000
```

## 工作流程

### 1. 用户名密码登录流程

```
用户            客户端            服务端
 │                │                 │
 │  1.输入用户名密码 │                 │
 │───────────────>│                 │
 │                │ 2.POST /api/auth/login
 │                │────────────────>│
 │                │                 │ 3.验证用户名密码
 │                │                 │ 4.生成JWT令牌
 │                │ 5.返回accessToken
 │                │<────────────────│
 │ 6.保存令牌      │                 │
 │<───────────────│                 │
 │                │                 │
 │ 7.访问受保护接口  │                 │
 │───────────────>│                 │
 │                │ 8.携带令牌访问
 │                │────────────────>│
 │                │                 │ 9.验证令牌
 │                │ 10.返回数据
 │                │<────────────────│
 │ 11.显示数据     │                 │
 │<───────────────│                 │
```

### 2. 客户端密钥登录流程

```
客户端           服务端
 │                │
 │ 1.使用客户端密钥 │
 │                │
 │ 2.POST /api/auth/client/login
 │────────────────>│
 │                │ 3.验证客户端密钥
 │                │ 4.生成JWT令牌
 │ 5.返回令牌（CLIENT角色）
 │<────────────────│
 │                │
 │ 6.使用令牌访问
 │────────────────>│
 │                │ 7.验证令牌和权限
 │ 8.返回数据
 │<────────────────│
```

## 令牌管理

### 访问令牌（Access Token）
- 有效期：24小时（可配置）
- 用于访问受保护的API接口
- 过期后需要使用刷新令牌获取新的访问令牌

### 刷新令牌（Refresh Token）
- 有效期：7天（可配置）
- 用于获取新的访问令牌
- 可以设置更长的有效期

## 安全建议

### 1. 生产环境配置

```properties
# 使用强密钥（至少256位）
jwt.secret=${JWT_SECRET}

# 根据业务需求调整令牌有效期
jwt.expiration=3600000  # 1小时
jwt.refresh-expiration=2592000000  # 30天
```

### 2. 密码存储
- 使用 BCrypt 加密存储
- 永远不要明文存储密码

### 3. 客户端密钥
- 生成足够长的随机密钥
- 定期轮换密钥
- 为不同的客户端使用不同的密钥

### 4. 令牌传输
- 始终使用 HTTPS
- 在 Authorization 头中传输
- 不要在 URL 中传递令牌

### 5. 令牌存储
- 客户端应安全存储令牌（如使用 HttpOnly Cookie）
- 考虑实现令牌黑名单机制

## 测试

### 1. 使用 Knife4j 测试

1. 访问 http://localhost:8080/doc.html
2. 找到认证接口
3. 点击"调试"
4. 输入用户名密码进行登录
5. 复制返回的 accessToken
6. 在其他接口的请求头中添加：`Authorization: Bearer {token}`

### 2. 使用 curl 测试

```bash
# 登录
TOKEN=$(curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john_doe","password":"password123"}' \
  | jq -r '.accessToken')

# 使用令牌访问接口
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer $TOKEN"
```

### 3. 创建测试用户

```java
// 在启动时创建测试用户
@Component
public class DataInitializer implements ApplicationRunner {
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        User user = new User();
        user.setUsername("test_user");
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setClientSecret("client_secret_abc123");
        userService.createUser(user.getUsername(), user.getEmail(), user.getPassword());
    }
}
```

## 常见问题

### 1. 令牌过期
**原因：** 访问令牌有效期已过

**解决：** 使用刷新令牌获取新的访问令牌

### 2. 令牌无效
**原因：** 令牌被篡改或格式错误

**解决：** 重新登录获取新令牌

### 3. 401 Unauthorized
**原因：** 未携带令牌或令牌无效

**解决：** 确保请求头包含有效的 Authorization

### 4. 403 Forbidden
**原因：** 权限不足

**解决：** 确认用户角色有权限访问该资源

## 扩展功能

### 1. 添加更多认证方式

可以扩展支持：
- 手机号验证码登录
- 第三方登录（OAuth2）
- 双因素认证（2FA）

### 2. 令牌黑名单

实现 Redis 令牌黑名单，用于：
- 用户登出时令牌失效
- 强制下线
- 修改密码后使原令牌失效

### 3. 限流和防暴力破解

添加登录限流和账户锁定机制

### 4. 审计日志

记录所有认证活动：
- 登录时间
- IP地址
- 设备信息
- 操作日志

## 参考资料

- [Spring Security 官方文档](https://docs.spring.io/spring-security/reference/)
- [JWT 官方网站](https://jwt.io/)
- [JJWT 库文档](https://github.com/jwtk/jjwt)

问题原因

你的系统中有两种 token 机制：
- 普通用户登录/注册：使用 TokenService 生成 UUID 格式的 token（存储在 Redis）
- 客户端密钥登录：使用 JwtTokenProvider 生成 JWT 格式的 token（格式：xxx.yyy.zzz）

但原来的 JwtAuthenticationFilter 只能处理 JWT 格式的 token，当收到 UUID token 时会尝试用 JWT 方式 解析，导致报错"Found: 0"（UUID 字符串中没有点号） 。

修复方案

修改了 JwtAuthenticationFilter（src/main/java/net/bbq/falsework/security/JwtAuthenticationFilter.java:36-77），现在它会：

1. 优先使用 TokenService 验证 UUID token（普通用户登录）
2. 如果 UUID token 验证失败，再使用 JwtTokenProvider 验证 JWT token（客户端登录）

这样两种 token 格式都能正常工作了，并且不会出现 解析错误。


