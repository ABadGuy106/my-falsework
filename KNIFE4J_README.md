# Knife4j 接口文档集成说明

## 已完成的工作

### 1. 添加依赖
- **knife4j-openapi3-jakarta-spring-boot-starter** (4.5.0) - 适用于Spring Boot 3.x
- **spring-boot-starter-validation** - 用于参数校验

### 2. 创建配置类
- `OpenApiConfig.java` - OpenAPI配置，包含API文档基本信息

### 3. 创建DTO类
- `UserCreateRequest.java` - 用户创建请求DTO，包含完整的校验注解和文档注解
- `UserUpdateRequest.java` - 用户更新请求DTO
- `CommonResult.java` - 通用响应结果封装类

### 4. 创建Controller
- `UserController.java` - 用户管理接口，包含完整的Swagger注解

## 访问Knife4j文档

启动项目后，访问以下地址：

```
http://localhost:8080/doc.html
```

## 主要功能特性

### 1. 接口分类
使用 `@Tag` 注解将接口分组，便于管理和查找

### 2. 接口描述
- `@Operation` - 描述接口功能
- `@ApiResponses` - 描述响应状态码
- `@Parameter` - 描述参数信息

### 3. 模型文档
- `@Schema` - 描述实体类的字段信息
- 包含字段类型、示例值、是否必填等

### 4. 参数校验
在DTO中使用Jakarta Validation注解：
- `@NotBlank` - 不能为空
- `@Email` - 邮箱格式校验
- `@Size` - 长度限制

## API接口列表

### 用户管理接口 (`/api/users`)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/users` | 创建用户 |
| GET | `/api/users/{id}` | 根据ID查询用户 |
| GET | `/api/users/username/{username}` | 根据用户名查询 |
| GET | `/api/users/email?email=xxx` | 根据邮箱查询 |
| GET | `/api/users` | 获取所有用户 |
| PUT | `/api/users/{id}` | 更新用户信息 |
| DELETE | `/api/users/{id}` | 删除用户 |

## 使用示例

### 在线测试
1. 打开 `http://localhost:8080/doc.html`
2. 找到对应的接口
3. 点击"调试"按钮
4. 填写参数
5. 发送请求

### 示例请求：创建用户
```json
POST /api/users
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123"
}
```

### 示例响应
```json
{
  "code": 200,
  "message": "用户创建成功",
  "data": {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "password": "password123",
    "createdAt": "2025-01-01T12:00:00",
    "updatedAt": "2025-01-01T12:00:00"
  }
}
```

## 添加新接口的步骤

1. 创建DTO类，添加 `@Schema` 注解
2. 创建Controller方法，添加 `@Operation`、`@ApiResponses` 等注解
3. 使用 `@Parameter` 注解描述路径参数和查询参数
4. 在DTO中添加校验注解（如需要）
5. 重启应用，Knife4j会自动更新文档

## 注解说明

### Controller相关
- `@Tag(name="分组名", description="描述")` - 接口分组
- `@Operation(summary="简要说明", description="详细说明")` - 接口说明
- `@ApiResponses` - 定义可能的响应
- `@Parameter` - 参数说明

### Model相关
- `@Schema(description="说明", example="示例值", requiredMode=REQUIRED/NOT_REQUIRED)` - 字段说明

### 校验注解
- `@NotNull` - 不能为null
- `@NotBlank` - 字符串不能为空
- `@Email` - 邮箱格式
- `@Size(min=, max=)` - 长度限制
- `@Min` / `@Max` - 数值范围
