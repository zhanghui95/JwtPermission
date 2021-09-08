# JwtPermission

## 1. 项目介绍
项目基于 https://github.com/whvcse/JwtPermission
> 基于token验证的Java Web权限控制框架，使用jjwt，支持redis和db多种存储方式，可用于前后端分离项目，功能完善、使用简单、易于扩展。

曾经发现原作者这个项目感觉很轻量 简单 实用，但是可能因为别的原因很长一段时间没有更新，主页提的issues也没回复，因为自己也有需求想要加入就自己基于源码添加实现并发布中央仓库。

## 2. 使用

### 2.1 SpringBoot项目引入依赖
```xml
<dependency>
    <groupId>com.yilers</groupId>
    <artifactId>jwtp-spring-boot-starter</artifactId>
    <version>1.5.3</version>
</dependency>
```
### 2.2 加注解
在启动类上面加入`@EnableJwtPermission`注解

### 2.3 添加配置
```properties
## 0-redisTokenStore 1-jdbcTokenStore 2-jwtTokenStore 默认是0
jwtp.store-type=0

## 生成token的密钥 redis和jdbc类型可以不指定 jwt模式下必须指定
jwtp.secret-key=123456

## 拦截路径，默认是/**
jwtp.path=/**

## 排除拦截路径，默认无
jwtp.exclude-path=/swagger-resources/**,/v2/**

## 单个用户最大token数，默认-1不限制
jwtp.max-token=10

## url自动对应权限方式，0 简易模式，1 RESTful模式，2 接口处根据注解校验
jwtp.url-perm-type=2

## 统一认证中心地址
jwtp.auth-center-url=http://localhost:8082,http://localhost:8083

## 统一认证多个地址请求策略 1.5.3添加 0-轮询 1-随机
jwtp.auth-center-strategy = 0

# 自定义查询用户权限的sql
jwtp.find-permissions-sql=select permission_code from t_permission where id in (select permission_id from t_role_permission where role_id in (select role_id from t_user_role where user_id = ?))

# 自定义查询用户角色的sql
jwtp.find-roles-sql=select role_code from t_role where id in (select role_id from t_user_role where user_id = ?)
```

### 2.4 登陆生成token
```java
@RestController
public class LoginController {
    @Autowired
    private TokenStore tokenStore;
    
    @PostMapping("/token")
    public Result<Token> token(String account, String password) {
        // 你的验证逻辑
        // ......
        // 签发token 有多种重载形式
        Token token = tokenStore.createNewToken(userId);
        Result.ok(token);
    }
}
```
> 更多使用参考原作者详细文档 https://gitee.com/whvse/JwtPermission/wikis/pages

### 2.5 其他
```
1. 接口权限校验
// 需要有system权限才能访问
@RequiresPermissions("system")

// 需要有system和front权限才能访问,logical可以不写,默认是AND
@RequiresPermissions(value={"system","front"}, logical=Logical.AND)

// 需要有system或front权限才能访问
@RequiresPermissions(value={"system","front"}, logical=Logical.OR)

// 需要有admin或user角色才能访问
@RequiresRoles(value={"admin","user"}, logical=Logical.OR)

2. 接口忽略鉴权
可以在配置文件中配置排除拦截路径 或 使用注解@Ignore忽略验证

3. 自定义查询用户权限 角色sql
配置上可自动查询用户角色与权限

4. 前后端规约
前端需要在请求头添加 "Authorization":'Bearer '+ token，注意Bearer后有一个空格

5. 统一认证
当A服务提供token颁发 鉴权，B服务作为资源服务受A服务统一鉴权保护
5.1 B服务配置 jwtp.auth-center-url 用于服务统一认证调用
5.2 B服务启动类添加 @EnableJwtPermissionClient 注解

6. jwtp.store-type说明
当采用redis存储时自行配置redis集成用于token存储
当采用db存储时，需要导入提供的脚本
当采用jwt无存储时，token过期时间不易设置太长

7. 获取当前用户信息
// 正常可以这样获取
Token token = SubjectUtil.getToken(request);

// 对于排除拦截的接口可以这样获取
Token token = SubjectUtil.parseToken(request);
说明：在我使用时发现对于添加@Ignore忽略鉴权的接口 SubjectUtil.parseToken(request)是有问题的，目前没有很好的办法，我是这样处理的
String accessToken = CheckPermissionUtil.takeToken(request);
String tokenKey = tokenStore.getTokenKey();
String userId = TokenUtil.parseToken(accessToken, tokenKey);

```

### 2.6 变更点
```
1. 升级底层依赖包版本
2. 更换过时API
3. 增加jwtTokenStore无存储token方式
4. 增加自定义密钥配置
5. 统一认证地址可配置多个
6. 代码注释等优化
7. jwtp.url-perm-type=2 不自动校验url，只有接口添加鉴权注解才去校验

说明：
1. 在jwtp.store-type=2选择jwt方式，是不存储token的 所以jwtp.max-token是不限制的
2. 在jwtp.store-type=2选择jwt方式，必须配置jwtp.secret-key自定义密钥 不然启动失败
3. 在jwtp.store-type=2选择jwt方式，是没有刷新token的
4. 统一认证中心地址 多个用逗号分隔 jwtp.auth-center-url=http://localhost:8082,http://localhost:8083
```

### 2.7 更新记录
```
2021.01.15 1.0版本 存在bug不能使用
1. 升级依赖版本
2. 添加无存储token方式
3. 自定义jwt生成密钥
4. 修改包名前缀为com.yilers
5. 抛出异常代替代码中控制台输出

2021.01.19 1.1.0版本 存在bug不能使用
1. jwt方式token生成bug修复
2. 过时方法修改
3. 统一认证请求jwt方式修改

2021.01.21 1.2.0版本 存在bug不能使用
1. 处理统一认证
2. 已知问题修复

2021.01.22 1.3.0版本 存在bug不能使用
1. 处理统一认证返回数据格式解析错误
2. 优化统一认证截取token

2021.01.23 1.4.0版本 存在bug不能使用
1. 处理统一认证jwt方式解析错误

2021.01.25 1.5.0版本
1. 处理没有指定密钥加密错误
2. jwt方式不生成刷新token

2021.01.29 1.5.1版本
1. 加入jwtp.url-perm-type=2 不自动校验，只有接口添加鉴权注解才去校验
2. 判断权限NPE处理

2021.01.30 1.5.2版本
1. Token实体时间格式化配置东八区

2021.09.08 1.5.3版本
1. 统一认证中心地址配置多个可配置请求策略(轮询/随机)
```