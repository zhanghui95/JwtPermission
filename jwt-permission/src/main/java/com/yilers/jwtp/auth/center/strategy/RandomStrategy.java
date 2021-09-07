package com.yilers.jwtp.auth.center.strategy;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @program: JwtPermission
 * @description: 随机策略
 * @author: hui.zhang
 * @date: 2021/9/7 4:24 下午
 **/

public class RandomStrategy implements AuthCenterStrategy {

    @Override
    public String getUrl(String[] urls) {
        return urls[(ThreadLocalRandom.current().nextInt(urls.length))];
    }
}
