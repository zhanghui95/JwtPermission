package com.yilers.jwtp.provider;

import com.yilers.jwtp.util.JacksonUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * @program: JwtPermission
 * @description:
 * @author: hui.zhang
 * @date: 2021/1/14 5:17 下午
 **/
public class JwtTokenStore extends TokenStoreAbstract {
    private final JdbcTemplate jdbcTemplate;

    public JwtTokenStore(DataSource dataSource) {
        Assert.notNull(dataSource, "DataSource required");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public String getTokenKey() {
        if (!StringUtils.hasText(mTokenKey)) {
            throw new RuntimeException("没有配置密钥");
        }
        return mTokenKey;
    }

    @Override
    public int storeToken(Token token) {
        return 1;
    }

    @Override
    public Token findToken(String userId, String access_token) {
        Token token = new Token();
        token.setUserId(userId);
        token.setAccessToken(access_token);
        Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(mTokenKey).build().parseClaimsJws(access_token);
        Date expireTime = claimsJws.getBody().getExpiration();
        token.setExpireTime(expireTime);
        return token;
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
        // 判断是否自定义查询
        if (getFindRolesSql() == null || getFindRolesSql().trim().isEmpty()) {
            return token.getRoles();
        }
        try {
            List<String> roleList = jdbcTemplate.query(getFindRolesSql(), new RowMapper<String>() {
                @Override
                public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return rs.getString(1);
                }
            }, userId);
            return JacksonUtil.stringListToArray(roleList);
        } catch (EmptyResultDataAccessException e) {
        }
        return null;
    }

    @Override
    public String[] findPermissionsByUserId(String userId, Token token) {
        // 判断是否自定义查询
        if (getFindPermissionsSql() == null || getFindPermissionsSql().trim().isEmpty()) {
            return token.getPermissions();
        }
        try {
            List<String> permList = jdbcTemplate.query(getFindPermissionsSql(), new RowMapper<String>() {
                @Override
                public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return rs.getString(1);
                }
            }, userId);
            return JacksonUtil.stringListToArray(permList);
        } catch (EmptyResultDataAccessException e) {
        }
        return null;
    }
}
