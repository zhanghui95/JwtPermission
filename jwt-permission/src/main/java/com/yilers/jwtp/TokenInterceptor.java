package com.yilers.jwtp;

import com.yilers.jwtp.exception.ErrorTokenException;
import com.yilers.jwtp.exception.ExpiredTokenException;
import com.yilers.jwtp.exception.UnauthorizedException;
import com.yilers.jwtp.perm.UrlPerm;
import com.yilers.jwtp.provider.Token;
import com.yilers.jwtp.provider.TokenStore;
import com.yilers.jwtp.util.CheckPermissionUtil;
import com.yilers.jwtp.util.SubjectUtil;
import com.yilers.jwtp.util.TokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 拦截器
 * Created by wangfan on 2018-12-27 下午 4:46.
 */
public class TokenInterceptor implements HandlerInterceptor {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private TokenStore tokenStore;
    private UrlPerm urlPerm;

    public TokenInterceptor() {
    }

    public TokenInterceptor(TokenStore tokenStore) {
        setTokenStore(tokenStore);
    }

    public TokenInterceptor(TokenStore tokenStore, UrlPerm urlPerm) {
        setTokenStore(tokenStore);
        setUrlPerm(urlPerm);
    }

    public TokenStore getTokenStore() {
        return tokenStore;
    }

    public void setTokenStore(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    public void setUrlPerm(UrlPerm urlPerm) {
        this.urlPerm = urlPerm;
    }

    public UrlPerm getUrlPerm() {
        return urlPerm;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        /**
         * 放行options请求
         */
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            CheckPermissionUtil.passOptions(response);
            return false;
        }
        Method method = null;
        if (handler instanceof HandlerMethod) {
            method = ((HandlerMethod) handler).getMethod();
        }
        // 检查是否忽略权限验证
        if (method == null || CheckPermissionUtil.checkIgnore(method)) {
            return true;
        }
        // 获取token
        String access_token = CheckPermissionUtil.takeToken(request);
        if (access_token == null || access_token.trim().isEmpty()) {
            throw new ErrorTokenException("Token不能为空");
        }
        String userId;
        try {
            String tokenKey = tokenStore.getTokenKey();
            logger.debug("ACCESS_TOKEN: " + access_token + "   TOKEN_KEY: " + tokenKey);
            userId = TokenUtil.parseToken(access_token, tokenKey);
        } catch (ExpiredJwtException e) {
            logger.debug("ERROR: ExpiredJwtException");
            throw new ExpiredTokenException();
        } catch (Exception e) {
            throw new ErrorTokenException();
        }
        // 检查token是否存在系统中
        Token token = tokenStore.findToken(userId, access_token);
        if (token == null) {
            logger.debug("ERROR: Token Not Found");
            throw new ErrorTokenException();
        }
        // 查询用户的角色和权限
        token.setRoles(tokenStore.findRolesByUserId(userId, token));
        token.setPermissions(tokenStore.findPermissionsByUserId(userId, token));
        // 检查权限
        if (CheckPermissionUtil.isNoPermission(token, request, response, handler, urlPerm)) {
            throw new UnauthorizedException();
        }
        request.setAttribute(SubjectUtil.REQUEST_TOKEN_NAME, token);
        return true;
    }

}
