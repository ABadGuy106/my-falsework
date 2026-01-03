# My Falsework

一个基于 Spring Boot 3 + MyBatis-Plus + PostgreSQL + Spring Security 的现代化 Java 后端项目模板，集成了 JWT 认证、Knife4j 接口文档、Liquibase 数据库版本管理和 Lombok 代码简化。

## 项目简介

本项目提供了一个开箱即用的 Spring Boot 后端开发模板，整合了企业级开发常用的技术栈和最佳实践，帮助开发者快速搭建规范的后端服务。使用 MyBatis-Plus 可以大幅简化 CRUD 操作，使用 Spring Security + JWT 实现安全的认证授权。

## 技术栈

### 核心框架
- **Spring Boot** 3.5.9 - 基础框架
- **Spring Security** - 安全框架，实现认证和授权
- **Java** 21 - 编程语言
- **Maven** - 项目构建工具

### 数据持久化
- **MyBatis-Plus** 3.5.7 - MyBatis 增强工具，提供通用 CRUD
- **PostgreSQL** - 关系型数据库
- **Liquibase** - 数据库版本控制工具

### 安全认证
- **Spring Security** - 认证和授权框架
- **JWT (JJWT)** 0.12.6 - JSON Web Token 令牌生成和验证
- **BCrypt** - 密码加密

### 接口文档
- **Knife4j** 4.5.0 - Swagger 增强版 API 文档工具（OpenAPI 3）
- **Jakarta Validation** - 参数校验

### 开发工具
- **Lombok** - 简化 Java 代码

## 项目结构

```
my-falsework/
├── src/main/
│   ├── java/net/bbq/falsework/
│   │   ├── config/           # 配置类
│   │   │   ├── OpenApiConfig.java
│   │   │   ├── SecurityConfig.java
│   │   │   └── JwtConfig.java
│   │   ├── controller/       # 控制器层
│   │   │   ├── AuthController.java
│   │   │   └── UserController.java
│   │   ├── dto/              # 数据传输对象
│   │   │   ├── auth/         # 认证相关DTO
│   │   │   │   ├── LoginRequest.java
│   │   │   │   ├── ClientLoginRequest.java
│   │   │   │   ├── RefreshTokenRequest.java
│   │   │   │   └── AuthResponse.java
│   │   │   ├── CommonResult.java
│   │   │   ├── UserCreateRequest.java
│   │   │   └── UserUpdateRequest.java
│   │   ├── entity/           # 实体类
│   │   │   └── User.java
│   │   ├── mapper/           # MyBatis-Plus Mapper 接口
│   │   │   └── UserMapper.java
│   │   ├── security/         # 安全相关
│   │   │   ├── JwtTokenProvider.java
│   │   │   ├── JwtAuthenticationFilter.java
│   │   │   └── CustomUserDetailsService.java
│   │   └── service/          # 业务逻辑层
│   │       ├── AuthService.java
│   │       └── UserService.java
│   └── resources/
│       ├── application.properties      # 应用配置
│       └── db/changelog/               # Liquibase 数据库迁移脚本
│           └── db.changelog-master.yaml
└── pom.xml
```

## 快速开始

### 前置要求

- JDK 21 或更高版本
- Maven 3.6+
- PostgreSQL 12+
- IDE（推荐 IntelliJ IDEA）

### 1. 克隆项目

```bash
git clone <repository-url>
cd my-falsework
```

### 2. 创建数据库

```sql
CREATE DATABASE postgres;
```

### 3. 修改配置

编辑 `src/main/resources/application.properties`，修改数据库连接信息：

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=root
spring.datasource.password=your_password
```

### 4. 启动项目

```bash
mvn spring-boot:run
```

或者直接运行 IDE 中的 `MyFalseworkApplication` 主类。

### 5. 访问应用

- **应用地址**: http://localhost:8080
- **API 文档**: http://localhost:8080/doc.html

## 核心功能

### 1. 用户管理示例

项目提供了完整的用户管理 CRUD 示例，包括：

- 创建用户
- 根据 ID 查询用户
- 根据用户名查询
- 根据邮箱查询
- 获取所有用户
- 更新用户信息
- 删除用户

### 2. MyBatis-Plus 增强

使用 MyBatis-Plus 带来的优势：

- **无需编写 XML** - 内置通用 Mapper，自带 CRUD 方法
- **代码生成器** - 快速生成 Entity、Mapper、Service、Controller
- **条件构造器** - 灵活的条件查询
- **分页插件** - 开箱即用的分页功能
- **性能优化** - 性能分析、SQL 注入等

### 3. 数据库版本控制

使用 Liquibase 管理数据库变更：

- 数据库表结构自动创建
- 版本化的数据库迁移脚本
- 支持回滚

### 4. API 接口文档

集成 Knife4j 自动生成接口文档：

- 在线调试 API
- 请求/响应示例
- 参数说明和校验规则

## 配置说明

### 数据源配置

```properties
# 数据库连接
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=root
spring.datasource.password=1qaz@WSX3edc
spring.datasource.driver-class-name=org.postgresql.Driver
```

### MyBatis-Plus 配置

```properties
# 实体类别名包
mybatis-plus.type-aliases-package=net.bbq.falsework.entity
# 驼峰命名转换
mybatis-plus.configuration.map-underscore-to-camel-case=true
# 日志实现
mybatis-plus.configuration.log-impl=org.apache.ibatis.logging.slf4j.Slf4jImpl
```

### Liquibase 配置

```properties
# 启用 Liquibase
spring.liquibase.enabled=true
# 主 changelog 文件
spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.yaml
# 环境
spring.liquibase.contexts=dev
```

## API 文档

### 用户管理接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/users` | 创建用户 |
| GET | `/api/users/{id}` | 根据 ID 查询用户 |
| GET | `/api/users/username/{username}` | 根据用户名查询 |
| GET | `/api/users/email?email=xxx` | 根据邮箱查询 |
| GET | `/api/users` | 获取所有用户 |
| PUT | `/api/users/{id}` | 更新用户信息 |
| DELETE | `/api/users/{id}` | 删除用户 |

### 请求示例

**创建用户**
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "password123"
  }'
```

**响应示例**
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

详细文档请访问：http://localhost:8080/doc.html

## 开发指南

### 添加新的实体

使用 MyBatis-Plus 快速开发：

1. **创建实体类**
```java
@Data
@TableName("your_table")
public class YourEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("field_name")
    private String fieldName;
}
```

2. **创建 Mapper 接口**
```java
@Mapper
public interface YourMapper extends BaseMapper<YourEntity> {
    // 自定义查询方法可使用注解
    @Select("SELECT * FROM your_table WHERE condition = #{condition}")
    List<YourEntity> findByCondition(@Param("condition") String condition);
}
```

3. **创建 Service 类**
```java
@Service
@RequiredArgsConstructor
public class YourService {
    private final YourMapper yourMapper;

    // 使用 MyBatis-Plus 提供的方法
    public YourEntity getById(Long id) {
        return yourMapper.selectById(id);
    }

    public List<YourEntity> getAll() {
        return yourMapper.selectList(null);
    }
}
```

4. **创建 Controller 类**（添加 Swagger 注解）

5. **创建 Liquibase 变更脚本**

### MyBatis-Plus 常用方法

**BaseMapper 提供：**
- `insert(T entity)` - 插入
- `deleteById(Serializable id)` - 根据ID删除
- `updateById(T entity)` - 根据ID更新
- `selectById(Serializable id)` - 根据ID查询
- `selectList(Wrapper<T>)` - 查询列表
- `selectCount(Wrapper<T>)` - 查询数量
- `insertBatchSomeColumn(List<T>)` - 批量插入

**条件构造器示例：**
```java
// 查询 username = 'john' 且 status = 1 的用户
LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
wrapper.eq(User::getUsername, "john")
       .eq(User::getStatus, 1);
List<User> users = userMapper.selectList(wrapper);
```

### 添加数据库迁移

在 `src/main/resources/db/changelog/db.changelog-master.yaml` 中添加新的 changeSet：

```yaml
databaseChangeLog:
  - changeSet:
      id: 2
      author: your_name
      changes:
        - createTable:
            tableName: your_table
            columns:
              # ... 定义表结构
```

### 代码规范

- 使用 Lombok 简化代码
- Service 使用 `@RequiredArgsConstructor` 注入依赖
- DTO 使用 Jakarta Validation 注解进行参数校验
- 所有接口返回统一的 `CommonResult` 格式
- Swagger 注解要完整、清晰
- 实体类使用 MyBatis-Plus 注解标注

## Lombok 使用

项目集成了 Lombok，常用的注解：

- `@Data` - 自动生成 getter/setter/toString/equals/hashCode
- `@RequiredArgsConstructor` - 为 final 字段生成构造器（依赖注入）
- `@NoArgsConstructor` - 生成无参构造器
- `@AllArgsConstructor` - 生成全参构造器

**示例**：
```java
@Data
@TableName("t_user")
public class User {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("username")
    private String username;
}

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
}
```

## MyBatis-Plus 注解说明

### 实体类注解
- `@TableName` - 指定表名
- `@TableId` - 指定主键字段
- `@TableField` - 指定字段映射
- `@TableLogic` - 逻辑删除字段

### 常见问题

### 1. Liquibase 执行失败

检查数据库连接配置是否正确，确保数据库已创建。

### 2. 表名映射失败

确认实体类的 `@TableName` 注解配置正确，或在配置文件中设置表名前缀。

### 3. 字段映射失败

使用 `@TableField` 注解显式指定数据库字段名，或开启驼峰转换。

### 4. API 文档无法访问

确保项目已正常启动，检查防火墙设置。

### 5. Lombok 不生效

IDEA 需要安装 Lombok 插件：
- Settings → Plugins → 搜索 "Lombok" → 安装
- Settings → Build, Execution, Deployment → Compiler → Annotation Processors → 启用注解处理

## MyBatis-Plus vs MyBatis

### MyBatis-Plus 的优势

1. **无需 XML** - 基础 CRUD 无需编写 XML
2. **代码量少** - 相比 MyBatis 减少 70% 的代码
3. **开发效率高** - 内置通用 Mapper 和 Service
4. **功能强大** - 条件构造器、分页、代码生成等

### 何时需要编写 SQL

- 复杂的多表关联查询
- 需要优化性能的 SQL
- 使用数据库特定函数

可以在 Mapper 接口中使用注解编写 SQL：
```java
@Select("SELECT * FROM t_user WHERE username LIKE CONCAT('%', #{keyword}, '%')")
List<User> searchByUsername(@Param("keyword") String keyword);
```

## 构建与部署

### 打包项目

```bash
mvn clean package
```

### 运行 JAR

```bash
java -jar target/my-falsework-0.0.1-SNAPSHOT.jar
```

## 开发环境

- **IDE**: IntelliJ IDEA（推荐）
- **JDK**: OpenJDK 21
- **构建工具**: Maven
- **数据库**: PostgreSQL
- **API 测试**: Knife4j 在线调试

## 贡献指南

欢迎提交 Issue 和 Pull Request！

## 许可证

本项目采用 Apache 2.0 许可证。

## 联系方式

- **作者**: BBQ
- **邮箱**: admin@example.com

---

**注意**: 本项目为示例模板，生产环境使用前请根据实际情况进行安全加固和性能优化。
修改总结

1. 添加Redisson依赖 (pom.xml)

添加了redisson-spring-boot-starter依赖（版本3.37.0）

2. 配置Redis (application.properties)

- 添加了Redis连接配置
- 添加了token过期时间配置

3. 创建Redis配置类 (RedisConfig.java)

配置了Redisson客户端连接到Redis服务器

4. 创建TokenService (TokenService.java)

提供了完整的token管理功能：
- generateAccessToken(): 生成UUID作为访问令牌， 并将用户信息保存到Redis
- generateRefreshToken(): 生成刷新令牌
- getUserByToken(): 根据token获取用户信息
- validateToken(): 验证token是否有效
- deleteToken(): 删除token（注销）
- refreshToken(): 刷新令牌

5. 创建TokenUser DTO (TokenUser.java)

用于在Redis中存储用户信息（包含userId、username 、role）

6. 修改AuthService (AuthService.java)

- login(): 使用UUID作为token，将用户信息保存到Redis
- register(): 注册成功后返回UUID token
- refreshToken(): 使用新的token刷新机制

主要特性

- Token格式：UUID字符串（如：550e8400-e29b-41d4-a716-446655440000）
- Token存储：Redis中，key格式为 auth:token:{uuid}
- Token过期：默认86400秒（24小时）
- 刷新令牌：7天有效期
- 支持token验证和刷新

所有修改已完成，登录接口现在会返回UUID作为token ，并将对应的用户信息保存在Redis中。
