package com.yilers.jwtp.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 配置属性
 * Created by wangfan on 2018-12-29 下午 2:13.
 */
@ConfigurationProperties(prefix = "jwtp")
public class JwtPermissionProperties {
    /**
     * token存储方式，0 redis存储，1 数据库存储，2 不存储
     * 不存储的时候token数量是不限制的
     */
    private Integer storeType = 0;

    /**
     * url自动对应权限方式，0 简易模式，1 RESTful模式，2 不校验接口请求
     */
    private Integer urlPermType;

    /**
     * 拦截路径，多个路径用逗号分隔
     */
    private String[] path = new String[]{"/**"};

    /**
     * 排除拦截路径，多个路径用逗号分隔
     */
    private String[] excludePath = new String[]{};

    /**
     * 单个用户最大的token数量
     */
    private Integer maxToken = -1;

    /**
     * 自定义查询用户角色的sql
     */
    private String findRolesSql;

    /**
     * 自定义查询用户权限的sql
     */
    private String findPermissionsSql;

    /**
     * 统一认证中心地址，多个地址用逗号','分隔 可配置请求策略 随机/轮询
     */
    private String authCenterUrl;

    /**
     * 生成token的密钥，有则使用 无则自动生成
     */
    private String secretKey;

    /**
     * 0-轮询 1-随机
     * 统一认证中心地址请求策略 默认轮询
     */
    private Integer authCenterStrategy = 0;

    public Integer getStoreType() {
        return storeType;
    }

    public void setStoreType(Integer storeType) {
        this.storeType = storeType;
    }

    public Integer getUrlPermType() {
        return urlPermType;
    }

    public void setUrlPermType(Integer urlPermType) {
        this.urlPermType = urlPermType;
    }

    public String[] getPath() {
        return path;
    }

    public void setPath(String[] path) {
        this.path = path;
    }

    public String[] getExcludePath() {
        return excludePath;
    }

    public void setExcludePath(String[] excludePath) {
        this.excludePath = excludePath;
    }

    public Integer getMaxToken() {
        return maxToken;
    }

    public void setMaxToken(Integer maxToken) {
        this.maxToken = maxToken;
    }

    public String getFindRolesSql() {
        return findRolesSql;
    }

    public void setFindRolesSql(String findRolesSql) {
        this.findRolesSql = findRolesSql;
    }

    public String getFindPermissionsSql() {
        return findPermissionsSql;
    }

    public void setFindPermissionsSql(String findPermissionsSql) {
        this.findPermissionsSql = findPermissionsSql;
    }

    public String getAuthCenterUrl() {
        return authCenterUrl;
    }

    public void setAuthCenterUrl(String authCenterUrl) {
        this.authCenterUrl = authCenterUrl;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public Integer getAuthCenterStrategy() {
        return authCenterStrategy;
    }

    public void setAuthCenterStrategy(Integer strategyType) {
        this.authCenterStrategy = strategyType;
    }
}
