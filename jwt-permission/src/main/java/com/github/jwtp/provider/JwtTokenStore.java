package com.github.jwtp.provider;

import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @program: JwtPermission
 * @description:
 * @author: hui.zhang
 * @date: 2021/1/14 5:17 下午
 **/
public class JwtTokenStore extends TokenStoreAbstract {

    @Override
    public String getTokenKey() {
        if (!StringUtils.hasText(mTokenKey)) {
            throw new RuntimeException("没有配置密钥");
        }
        return mTokenKey;
    }

    @Override
    public int storeToken(Token token) {
        return 0;
    }

    @Override
    public Token findToken(String userId, String access_token) {
        return null;
    }

    @Override
    public List<Token> findTokensByUserId(String userId) {
        return null;
    }

    @Override
    public Token findRefreshToken(String userId, String refresh_token) {
        return null;
    }

    @Override
    public int removeToken(String userId, String access_token) {
        return 0;
    }

    @Override
    public int removeAllTokensByUserId(String userId) {
        return 0;
    }

    @Override
    public int updateRolesByUserId(String userId, String[] roles) {
        return 0;
    }

    @Override
    public int updatePermissionsByUserId(String userId, String[] permissions) {
        return 0;
    }

    @Override
    public String[] findRolesByUserId(String userId, Token token) {
        return new String[0];
    }

    @Override
    public String[] findPermissionsByUserId(String userId, Token token) {
        return new String[0];
    }
}
