package com.yilers.jwtp.auth.center.strategy;

/**
 * 统一认证请求策略
 * @author hui.zhang
 * @date 2021/9/7 4:22 下午
 */

public interface AuthCenterStrategy {

    String getUrl(String[] urls);
}
