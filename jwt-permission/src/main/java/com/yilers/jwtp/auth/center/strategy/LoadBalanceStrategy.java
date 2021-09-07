package com.yilers.jwtp.auth.center.strategy;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @program: JwtPermission
 * @description: 轮训
 * @author: hui.zhang
 * @date: 2021/9/7 4:27 下午
 **/

public class LoadBalanceStrategy implements AuthCenterStrategy {

    private final AtomicInteger index = new AtomicInteger(0);

    @Override
    public String getUrl(String[] urls) {
        return urls[(Math.abs(index.getAndAdd(1) % urls.length))];
    }
}
