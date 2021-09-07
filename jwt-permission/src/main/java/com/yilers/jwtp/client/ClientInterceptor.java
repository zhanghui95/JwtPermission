package com.yilers.jwtp.client;

import com.yilers.jwtp.auth.center.strategy.AuthCenterStrategy;
import com.yilers.jwtp.auth.center.strategy.LoadBalanceStrategy;
import com.yilers.jwtp.auth.center.strategy.RandomStrategy;
import com.yilers.jwtp.exception.ErrorTokenException;
import com.yilers.jwtp.exception.ExpiredTokenException;
import com.yilers.jwtp.exception.UnauthorizedException;
import com.yilers.jwtp.perm.UrlPerm;
import com.yilers.jwtp.util.CheckPermissionUtil;
import com.yilers.jwtp.util.SubjectUtil;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 拦截器
 * Created by wangfan on 2018-12-27 下午 4:46.
 */
public class ClientInterceptor implements HandlerInterceptor {
    private UrlPerm urlPerm;
    private String authCenterUrl;
    private static final String COMMA = ",";
    private Integer authCenterStrategyType;

    public Integer getAuthCenterStrategyType() {
        return authCenterStrategyType;
    }

    public void setAuthCenterStrategyType(Integer authCenterStrategy) {
        this.authCenterStrategyType = authCenterStrategy;
    }

    public ClientInterceptor() {
    }

    public ClientInterceptor(UrlPerm urlPerm) {
        setUrlPerm(urlPerm);
    }

    public ClientInterceptor(String authCenterUrl, UrlPerm urlPerm, Integer authCenterStrategy) {
        setAuthCenterUrl(authCenterUrl);
        setUrlPerm(urlPerm);
        setAuthCenterStrategyType(authCenterStrategy);
    }

    public void setUrlPerm(UrlPerm urlPerm) {
        this.urlPerm = urlPerm;
    }

    public UrlPerm getUrlPerm() {
        return urlPerm;
    }

    public String getAuthCenterUrl() {
        return authCenterUrl;
    }

    public void setAuthCenterUrl(String authCenterUrl) {
        this.authCenterUrl = authCenterUrl;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行options请求
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
        if (authCenterUrl == null) {
            throw new RuntimeException("请配置authCenterUrl");
        }
        // 多个地址 再判断请求策略
        String centerUrl = authCenterUrl;
        if (authCenterUrl.contains(COMMA)) {
            String[] urls = authCenterUrl.split(COMMA);
            AuthCenterStrategy strategy = null;
            if (0 == authCenterStrategyType) {
                // 轮询
                strategy = new LoadBalanceStrategy();
            } else if (1 == authCenterStrategyType) {
                strategy = new RandomStrategy();
            }
            centerUrl = strategy.getUrl(urls);
        }
        String url = centerUrl + "/authentication?access_token=" + access_token;
        AuthResult authResult = new RestTemplate().getForObject(url, AuthResult.class);
        if (authResult == null) {
            throw new RuntimeException("'" + authCenterUrl + "/authentication' return null");
        } else if (AuthResult.CODE_EXPIRED == authResult.getCode()) {
            throw new ExpiredTokenException();
        } else if (AuthResult.CODE_OK != authResult.getCode()) {
            throw new ErrorTokenException();
        }
        // 检查权限
        if (CheckPermissionUtil.isNoPermission(authResult.getToken(), request, response, handler, urlPerm)) {
            throw new UnauthorizedException();
        }
        request.setAttribute(SubjectUtil.REQUEST_TOKEN_NAME, authResult.getToken());
        return true;
    }

}
