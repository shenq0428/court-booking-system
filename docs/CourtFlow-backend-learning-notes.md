# CourtFlow 后端学习笔记

> 目标：用 Spring Boot、PostgreSQL 和 React 完成一个可以用于申请 Java Backend Internship 的球场预约系统。  
> 学习方式：每次只完成一个小功能，同时理解它的职责、调用关系、输入、输出和删除后的影响。

## 1. 技术栈之间的关系

| 名称 | 在项目中的作用 |
|---|---|
| Java | 编写后端程序和业务逻辑的语言 |
| Spring Framework | 创建并管理 Java 对象，提供依赖注入、Web、数据库等能力 |
| Spring Boot | 自动配置 Spring、启动内置 Web Server，让项目更快运行 |
| Maven | 下载依赖、编译、测试和打包 Java 项目 |
| PostgreSQL | 长期保存用户、场馆、球场和预约资料 |
| JPA | Java 对象与关系型数据库之间的映射标准 |
| Hibernate | Spring Boot 默认常用的 JPA 实现，负责产生和执行 SQL |
| React | 用户看见和操作的前端页面 |

一次请求的主要流程：

```text
React
  -> Controller
  -> Service
  -> Repository
  -> PostgreSQL
  -> Response DTO
  -> React
```

## 2. 注册功能为什么要拆开完成

注册不是一个文件，而是一条由多个职责组成的流程：

```text
RegisterRequest
  -> AuthController
  -> AuthService
  -> AppUserRepository
  -> PostgreSQL
```

如果一次把 Entity、Repository、DTO、Service、Controller、Security 和 JWT 全写完，发生错误时很难判断是哪一层的问题。因此我们按照可以单独验证的顺序开发。

---

## 3. 注册功能的十个步骤

### 第 1 步：建立 `AppUser`

`AppUser` 是代表系统账户的 Java class。

选择 `AppUser` 而不是直接叫 `User`，可以减少它和 Spring Security 内置 `User` class 之间的命名混淆。数据库表可以命名为 `app_users`。

可能包含的资料：

```text
id
name
email
passwordHash
role
createdAt
```

这里先学习：

- Java class
- field
- object
- getter/setter 或其他存取方式

完成标准：项目能够编译，`AppUser` 能表达一个账户应有的资料。

复盘问题：

1. `AppUser` 代表什么？
2. 为什么不用一个 `String` 代表整个用户？
3. 哪些 field 不应该返回给 React？

### 第 2 步：理解 Entity 和数据库表

Entity 是需要持久化到数据库的 Java class。

对应关系：

| Java | PostgreSQL |
|---|---|
| `AppUser` class | `app_users` table |
| 一个 `AppUser` object | 表中的一行 row |
| `email` field | `email` column |
| `id` field | primary key column |

常见 Annotation：

```java
@Entity
@Table(name = "app_users")
@Id
@GeneratedValue
@Column
```

这里先学习：

- Annotation 的基本作用
- Primary Key
- column
- JPA/ORM 的基本概念

完成标准：启动应用后，JPA 能识别这个 Entity，并能映射到数据库表。

复盘问题：

1. 一个 object 和一行数据库资料有什么关系？
2. `@Entity` 删除后会怎样？
3. 为什么 email 应该设置 unique constraint？

### 第 3 步：建立 Repository

Repository 是 Java 后端访问数据库的入口。

它负责：

- 保存用户
- 根据 ID 查找用户
- 根据 Email 查找用户
- 检查 Email 是否存在

概念示例：

```java
public interface AppUserRepository
        extends JpaRepository<AppUser, Long> {
}
```

这里先学习：

- interface
- generic，例如 `<AppUser, Long>`
- Spring Data JPA
- CRUD

完成标准：可以调用 `save()` 保存用户，也可以查询刚才保存的用户。

复盘问题：

1. Repository 负责业务规则吗？
2. `AppUser` 和 `Long` 分别代表什么？
3. 谁会调用 Repository？

### 第 4 步：测试能不能保存用户

在建立 HTTP API 前，先测试 Entity 和 Repository 是否真的能和数据库配合。

测试流程：

```text
建立 AppUser object
  -> repository.save(user)
  -> 再从数据库查询
  -> assert 查询结果正确
```

这里先学习：

- 测试的 Arrange / Act / Assert
- JPA test
- assertion
- 测试数据库或测试环境

完成标准：测试可以自动证明用户能被保存和读取。

复盘问题：

1. 这个测试证明了什么？
2. 它有没有测试注册 API？
3. 如果 unique email 不生效，应该增加什么测试？

### 第 5 步：建立 Register DTO

DTO 是不同层之间传递资料的对象。注册 Request DTO 只接收注册需要的资料：

```json
{
  "name": "Zhang Shen",
  "email": "zhang@example.com",
  "password": "Password123!"
}
```

Register DTO 不应该接收：

```text
id
passwordHash
ADMIN role
createdAt
```

这里先学习：

- DTO 和 Entity 的区别
- Java record 或普通 class
- API 输入边界

完成标准：Controller 可以把 JSON 转换成一个类型明确的 `RegisterRequest`。

复盘问题：

1. 为什么不直接让 Controller 接收 Entity？
2. 为什么客户端不能提交 ADMIN role？
3. Request DTO 是否会直接保存到数据库？

### 第 6 步：学习 Validation

Validation 负责拒绝格式不正确的输入。

注册时通常检查：

- name 不能为空
- email 不能为空并且格式正确
- password 必须达到最低长度

常见 Annotation：

```java
@NotBlank
@Email
@Size(min = 8, max = 72)
```

这里先学习：

- 输入验证
- `@Valid`
- 验证错误
- 为什么不能相信前端输入

完成标准：错误 Email 或太短的密码会得到清楚的 `400 Bad Request`。

复盘问题：

1. React 已经验证过，后端为什么还要验证？
2. Validation 和“Email 是否已注册”是不是同一种检查？
3. 哪些问题应该返回 400？

### 第 7 步：建立 AuthService

Service 负责注册的业务规则，而不是处理 HTML 或直接决定页面样式。

注册业务流程：

```text
标准化 Email
  -> 检查 Email 是否已存在
  -> 加密密码
  -> 建立 AppUser
  -> 保存用户
  -> 返回安全的结果
```

这里先学习：

- Service layer
- constructor injection
- 业务规则
- transaction 基础

完成标准：Service 能完成注册，而且重复 Email 会被拒绝。

复盘问题：

1. 为什么 Email 检查不直接写在 Controller？
2. AuthService 需要哪些 dependency？
3. Repository 保存失败时会怎样？

### 第 8 步：学习 BCrypt

密码绝对不能以明文保存。

错误：

```text
password = Password123!
```

正确方向：

```text
passwordHash = BCrypt 产生的 Hash
```

BCrypt 是 password hashing algorithm。注册时使用 `encode()`，登录时使用 `matches()` 比较密码。

这里先学习：

- Hash 不是 encryption
- PasswordEncoder
- salt
- 为什么不能解密密码 Hash

完成标准：数据库永远看不到用户的原始密码，但正确密码仍然可以通过登录检查。

复盘问题：

1. 为什么不能保存明文密码？
2. 登录时为什么不是把 Hash 解密？
3. 为什么不能用普通字符串比较密码和 Hash？

### 第 9 步：建立 Controller

Controller 是 HTTP 请求进入后端的入口。

它主要负责：

- 接收 JSON
- 触发 Validation
- 调用 AuthService
- 决定 HTTP status
- 返回 Response DTO

概念示例：

```text
POST /api/auth/register
```

这里先学习：

- REST API
- HTTP method
- status code
- `@RestController`
- `@PostMapping`
- `@RequestBody`

完成标准：外部客户端可以通过 HTTP 注册用户，并收到安全、清楚的响应。

复盘问题：

1. Controller 会自己执行 SQL 吗？
2. Controller 调用了谁？
3. 成功注册应该返回什么状态码？

### 第 10 步：用 Postman 测试

Postman 让我们在 React 尚未接入时直接测试 API。

至少测试：

1. 正常注册
2. Email 格式错误
3. 密码太短
4. name 为空
5. Email 重复
6. 响应中没有 password/passwordHash

这里先学习：

- Request URL
- Header
- JSON body
- HTTP response
- status code

完成标准：注册 API 的成功和失败路径都有可重复验证的结果。

---

## 4. 当前需要的 Java 基础

### 4.1 变量和类型

变量保存资料，类型限制资料的种类。

```java
String email = "zhang@example.com";
int courtNumber = 3;
boolean active = true;
```

### 4.2 Class 和 Object

Class 是设计，Object 是根据设计建立出来的实例。

```java
public class AppUser {
    private String email;
}
```

`AppUser` 是 class；具体注册出来的 Zhang Shen 账户是一个 object。

### 4.3 Field 和 Method

Field 保存对象的状态：

```java
private String email;
```

Method 表示对象或服务能够执行的行为：

```java
public void changeEmail(String newEmail) {
    this.email = newEmail;
}
```

### 4.4 Constructor

Constructor 是建立 object 时使用的方法。

```java
public AppUser(String name, String email) {
    this.name = name;
    this.email = email;
}
```

Spring 也经常通过 constructor 把 dependency 注入 Service。

### 4.5 `public` 和 `private`

```java
private String passwordHash;
public String getName() { ... }
```

- `private`：只允许这个 class 内部直接访问
- `public`：其他 class 可以访问

不要把 `private` 简单理解成安全加密；它只是 Java 的访问范围控制。

### 4.6 Interface

Interface 规定“需要提供哪些行为”，但通常不负责保存具体状态。

```java
public interface AppUserRepository {
    Optional<AppUser> findByEmail(String email);
}
```

Spring Data JPA 可以在运行时提供 Repository interface 的实现。

### 4.7 `List`

`List` 表示有顺序的一组资料：

```java
List<Venue> venues;
```

它表示一个 Venue 集合，而不是一个 Venue。

### 4.8 `Optional`

`Optional<AppUser>` 表示查询结果可能有用户，也可能没有。

```java
Optional<AppUser> user = repository.findByEmail(email);
```

它让“找不到资料”成为明确情况，减少随意处理 `null`。

### 4.9 Enum

Enum 表示一组固定选项：

```java
public enum Role {
    CUSTOMER,
    ADMIN
}
```

注册接口默认创建 `CUSTOMER`，不能信任客户端传来的管理员角色。

### 4.10 Exception

Exception 表示程序无法正常完成某个操作。

例如 Email 已存在：

```java
throw new EmailAlreadyExistsException(email);
```

后面由统一异常处理把它转换成清楚的 HTTP response。

### 4.11 Annotation

Annotation 是附加在 class、field 或 method 上的说明信息：

```java
@Entity
@Service
@RestController
@NotBlank
```

Spring/JPA/Validation 会读取这些 Annotation，然后执行对应行为。

---

## 5. 五个固定复盘问题

每完成一个 class，都填写下面的模板：

```text
Class 名称：

1. 这个 class 负责什么？
2. 谁会调用它？
3. 它需要什么资料或 dependency？
4. 它返回什么或产生什么结果？
5. 删除它以后，哪一部分会坏？
```

例子：

```text
Class 名称：AuthService

1. 负责什么？
   负责注册和登录的业务规则。

2. 谁会调用它？
   AuthController。

3. 需要什么？
   AppUserRepository 和 PasswordEncoder。

4. 返回什么？
   安全的用户资料或登录结果。

5. 删除后哪里会坏？
   Controller 没有地方执行注册和登录逻辑。
```

---

## 6. 推荐的每次学习循环

每个小功能都走一遍：

1. 用一句话说明今天要解决的问题。
2. 只学习完成它所需的一两个概念。
3. 自己输入代码，不盲目复制整套功能。
4. 编译并运行测试。
5. 故意制造一个小错误，观察报错。
6. 修复错误。
7. 用自己的话回答五个复盘问题。
8. 做一个小的 Git commit。

建议一次学习控制在 60～90 分钟：

```text
10 分钟：复习上次内容
20 分钟：理解新概念
35 分钟：在 CourtFlow 实现
15 分钟：测试与排错
10 分钟：写总结和 commit
```

## 7. 注册完成的停止标准

当下面项目全部完成，就停止继续扩张注册功能：

- 正常资料可以注册
- 密码只保存 BCrypt Hash
- 重复 Email 被拒绝
- 无效输入返回 400
- 数据可以保存到 PostgreSQL
- Response 不包含密码或 Hash
- Repository 和 Service 的关键规则有测试
- Postman 可以重复演示成功及失败流程

之后才进入登录和 JWT，不同时加入 Google/Facebook OAuth、2FA 或复杂权限系统。

## 8. 当前进度

- [x] Java 21 正常
- [x] Maven Wrapper 正常
- [x] Spring Boot Context 测试通过
- [ ] 确认并清理旧示例代码
- [ ] 建立 `AppUser` Entity
- [ ] 建立 `AppUserRepository`
- [ ] Repository 保存测试
- [ ] Register DTO 与 Validation
- [ ] AuthService 与 BCrypt
- [ ] AuthController
- [ ] Postman 测试注册
- [ ] 登录与 JWT
- [ ] React 接入真实 API

