package com.yilers.jwtp.client;

import com.yilers.jwtp.exception.ErrorTokenException;
import com.yilers.jwtp.exception.ExpiredTokenException;
import com.yilers.jwtp.exception.UnauthorizedException;
import com.yilers.jwtp.perm.UrlPerm;
import com.yilers.jwtp.util.CheckPermissionUtil;
import com.yilers.jwtp.util.SubjectUtil;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Random;

/**
 * 拦截器
 * Created by wangfan on 2018-12-27 下午 4:46.
 */
public class ClientInterceptor implements HandlerInterceptor {
    private UrlPerm urlPerm;
    private String authCenterUrl;
    private static final String COMMA = ",";

    public ClientInterceptor() {
    }

    public ClientInterceptor(UrlPerm urlPerm) {
        setUrlPerm(urlPerm);
    }

    public ClientInterceptor(String authCenterUrl, UrlPerm urlPerm) {
        setAuthCenterUrl(authCenterUrl);
        setUrlPerm(urlPerm);
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
        if (request.getMethod().toUpperCase().equals("OPTIONS")) {
            CheckPermissionUtil.passOptions(response);
            return false;
        }
        Method method = null;
        if (handler instanceof HandlerMethod) {
            method = ((HandlerMethod) handler).getMethod();
        }
        // 检查是否忽略权限验证
        if (method == null || CheckPermissionUtil.checkIgnore(method)) {
//            return super.preHandle(request, response, handler);
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
        // 多个地址 任意取一个
        String centerUrl = authCenterUrl;
        if (authCenterUrl.contains(COMMA)) {
            String[] split = authCenterUrl.split(COMMA);
            Random random = new Random();
            int round = random.nextInt(split.length);
            centerUrl = split[round];
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
//        return super.preHandle(request, response, handler);
        return true;
    }

}
