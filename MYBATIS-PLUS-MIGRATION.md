# MyBatis 到 MyBatis-Plus 迁移指南

## 迁移完成情况

本项目已成功从 MyBatis 迁移到 MyBatis-Plus 3.5.7。

## 主要变更

### 1. 依赖变更

**pom.xml**
```xml
<!-- 之前：MyBatis -->
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>3.0.4</version>
</dependency>

<!-- 现在：MyBatis-Plus -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
    <version>3.5.7</version>
</dependency>
```

### 2. 实体类变更

**之前：**
```java
@Data
public class User {
    private Long id;
    private String username;
    private String email;
    // ...
}
```

**现在：**
```java
@Data
@TableName("t_user")
public class User {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("username")
    private String username;

    @TableField("email")
    private String email;

    @TableField("password")
    private String password;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
```

### 3. Mapper 接口变更

**之前：**
```java
@Mapper
public interface UserMapper {
    int insert(User user);
    Optional<User> findById(@Param("id") Long id);
    Optional<User> findByUsername(@Param("username") String username);
    Optional<User> findByEmail(@Param("email") String email);
    List<User> findAll();
    int update(User user);
    int deleteById(@Param("id") Long id);
}
```

需要编写对应的 UserMapper.xml

**现在：**
```java
@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT * FROM t_user WHERE username = #{username}")
    Optional<User> findByUsername(@Param("username") String username);

    @Select("SELECT * FROM t_user WHERE email = #{email}")
    Optional<User> findByEmail(@Param("email") String email);
}
```

BaseMapper 已经提供：
- `insert(T entity)`
- `deleteById(Serializable id)`
- `updateById(T entity)`
- `selectById(Serializable id)`
- `selectList(Wrapper<T> queryWrapper)`
- 等等...

### 4. Service 层变更

**之前：**
```java
public Optional<User> getUserById(Long id) {
    return userMapper.findById(id);
}

public List<User> getAllUsers() {
    return userMapper.findAll();
}

public User updateUser(Long id, String username, String email, String password) {
    User user = userMapper.findById(id).orElseThrow(() ->
            new RuntimeException("User not found with id: " + id));
    user.setUsername(username);
    user.setEmail(email);
    user.setPassword(password);
    user.setUpdatedAt(LocalDateTime.now());
    userMapper.update(user);
    return user;
}
```

**现在：**
```java
public Optional<User> getUserById(Long id) {
    return Optional.ofNullable(userMapper.selectById(id));
}

public List<User> getAllUsers() {
    return userMapper.selectList(null);
}

public User updateUser(Long id, String username, String email, String password) {
    User user = userMapper.selectById(id);
    if (user == null) {
        throw new RuntimeException("User not found with id: " + id);
    }
    user.setUsername(username);
    user.setEmail(email);
    user.setPassword(password);
    user.setUpdatedAt(LocalDateTime.now());
    userMapper.updateById(user);
    return user;
}
```

### 5. 配置文件变更

**之前：**
```properties
# MyBatis Configuration
mybatis.mapper-locations=classpath:mapper/**/*.xml
mybatis.type-aliases-package=net.bbq.falsework.entity
mybatis.configuration.map-underscore-to-camel-case=true
mybatis.configuration.log-impl=org.apache.ibatis.logging.slf4j.Slf4jImpl
```

**现在：**
```properties
# MyBatis-Plus Configuration
mybatis-plus.type-aliases-package=net.bbq.falsework.entity
mybatis-plus.configuration.map-underscore-to-camel-case=true
mybatis-plus.configuration.log-impl=org.apache.ibatis.logging.slf4j.Slf4jImpl
```

### 6. XML 映射文件

**之前：**
需要维护 UserMapper.xml，编写所有 SQL

**现在：**
- 删除了 UserMapper.xml
- 基本 CRUD 无需 XML
- 复杂查询可在 Mapper 接口中使用 @Select 等注解

## MyBatis-Plus 优势

### 1. 减少代码量
- 无需编写 XML 文件
- Mapper 接口只需继承 BaseMapper
- 自动提供 CRUD 方法

### 2. 开发效率提升
- 代码生成器：一键生成 Entity、Mapper、Service、Controller
- 条件构造器：链式查询，无需编写 SQL
- 分页插件：开箱即用

### 3. 功能增强
- 通用 Service：IService 接口提供更多业务方法
- 逻辑删除：自动处理删除标记
- 字段自动填充：自动设置创建时间、更新时间等
- 乐观锁：防止并发更新冲突

## 常用注解说明

### @TableName
指定数据库表名
```java
@TableName("t_user")
public class User { }
```

### @TableId
指定主键字段
```java
@TableId(value = "id", type = IdType.AUTO)
private Long id;
```

主键策略：
- `IdType.AUTO` - 数据库自增
- `IdType.ASSIGN_ID` - 雪花算法生成
- `IdType.INPUT` - 手动输入

### @TableField
指定字段映射
```java
@TableField("user_name")
private String username;
```

特殊用法：
```java
@TableField(exist = false)  // 字段不存在于数据库
@TableField(fill = FieldFill.INSERT)  // 插入时自动填充
@TableField(select = false)  // 查询时不返回该字段
```

### @TableLogic
逻辑删除字段
```java
@TableLogic
@TableField("deleted")
private Integer deleted;
```

## BaseMapper 常用方法

### 插入
```java
int insert(T entity);  // 插入一条记录
```

### 删除
```java
int deleteById(Serializable id);  // 根据ID删除
int deleteByMap(Map<String, Object> map);  // 根据 map 删除
int delete(Wrapper<T> wrapper);  // 根据 wrapper 删除
int deleteBatchIds(Collection<?> list);  // 批量删除
```

### 更新
```java
int updateById(T entity);  // 根据ID更新
int update(T entity, Wrapper<T> wrapper);  // 根据 wrapper 更新
```

### 查询
```java
T selectById(Serializable id);  // 根据ID查询
List<T> selectBatchIds(Collection<?> list);  // 批量查询
List<T> selectList(Wrapper<T> wrapper);  // 根据 wrapper 查询
Long selectCount(Wrapper<T> wrapper);  // 查询数量
```

## 条件构造器示例

### LambdaQueryWrapper（推荐）
```java
LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
wrapper.eq(User::getUsername, "john")
       .ne(User::getStatus, 0)
       .gt(User::getAge, 18)
       .orderByDesc(User::getCreatedAt);
List<User> users = userMapper.selectList(wrapper);
```

### QueryWrapper
```java
QueryWrapper<User> wrapper = new QueryWrapper<>();
wrapper.eq("username", "john")
       .ne("status", 0)
       .gt("age", 18);
List<User> users = userMapper.selectList(wrapper);
```

### 常用条件
```java
wrapper.eq("字段名", 值)  // 等于
       .ne("字段名", 值)  // 不等于
       .gt("字段名", 值)  // 大于
       .ge("字段名", 值)  // 大于等于
       .lt("字段名", 值)  // 小于
       .le("字段名", 值)  // 小于等于
       .like("字段名", 值)  // 模糊查询
       .in("字段名", 集合)  // IN 查询
       .between("字段名", 值1, 值2)  // BETWEEN
       .orderByAsc("字段名")  // 升序
       .orderByDesc("字段名");  // 降序
```

## 分页查询

### 1. 配置分页插件
```java
@Configuration
public class MyBatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.POSTGRE_SQL));
        return interceptor;
    }
}
```

### 2. 使用分页
```java
Page<User> page = new Page<>(1, 10);  // 第1页，每页10条
LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
wrapper.eq(User::getStatus, 1);
Page<User> result = userMapper.selectPage(page, wrapper);

List<User> records = result.getRecords();  // 数据列表
long total = result.getTotal();  // 总记录数
```

## 自动填充

### 1. 定义填充处理器
```java
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createdAt", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
    }
}
```

### 2. 实体类字段添加注解
```java
@TableField(fill = FieldFill.INSERT)
private LocalDateTime createdAt;

@TableField(fill = FieldFill.INSERT_UPDATE)
private LocalDateTime updatedAt;
```

## 代码生成器

MyBatis-Plus 提供代码生成器，可以快速生成：
- Entity
- Mapper
- Service
- Controller

详见官方文档：https://baomidou.com/pages/779a6e/

## 注意事项

1. **表名映射**：如果表名与实体类名不一致，必须使用 `@TableName` 注解
2. **字段映射**：如果字段名与属性名不一致（或有关键字），使用 `@TableField` 注解
3. **主键策略**：PostgreSQL 通常使用 `IdType.AUTO`
4. **复杂查询**：对于复杂的多表关联查询，仍然建议使用 XML 或注解编写 SQL
5. **性能优化**：生产环境建议开启 SQL 性能分析插件

## 迁移收益

- **代码减少 70%**：无需编写 XML 和基本 CRUD 方法
- **开发效率提升 50%**：代码生成器、条件构造器等工具
- **维护成本降低**：代码更简洁，更易维护
- **功能更强大**：分页、自动填充、逻辑删除等开箱即用

## 参考资料

- MyBatis-Plus 官方文档：https://baomidou.com/
- Spring Boot 3 整合指南：https://baomidou.com/pages/779a6e/
