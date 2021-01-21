package com.yilers.jwtp.util;

import com.yilers.jwtp.annotation.Ignore;
import com.yilers.jwtp.annotation.Logical;
import com.yilers.jwtp.annotation.RequiresPermissions;
import com.yilers.jwtp.annotation.RequiresRoles;
import com.yilers.jwtp.perm.UrlPerm;
import com.yilers.jwtp.perm.UrlPermResult;
import com.yilers.jwtp.provider.Token;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * CheckPermissionUtil
 * Created by wangfan on 2018-12-27 下午 4:46.
 */
public class CheckPermissionUtil {

    /**
     * 检查是否忽略权限
     */
    public static boolean checkIgnore(Method method) {
        Ignore annotation = method.getAnnotation(Ignore.class);
        if (annotation == null) {  // 方法上没有注解再检查类上面有没有注解
            annotation = method.getDeclaringClass().getAnnotation(Ignore.class);
            if (annotation == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查权限是否符合
     */
    public static boolean checkPermission(Token token, HttpServletRequest request, HttpServletResponse response, Object handler, UrlPerm urlPerm) {
        Method method = ((HandlerMethod) handler).getMethod();
        RequiresPermissions annotation = method.getAnnotation(RequiresPermissions.class);
        if (annotation == null) {  // 方法上没有注解再检查类上面有没有注解
            annotation = method.getDeclaringClass().getAnnotation(RequiresPermissions.class);
        }
        String[] requiresPermissions;
        Logical logical;
        if (annotation != null) {
            requiresPermissions = annotation.value();
            logical = annotation.logical();
        } else if (urlPerm != null) {
            UrlPermResult upr = urlPerm.getPermission(request, response, (HandlerMethod) handler);
            requiresPermissions = upr.getValues();
            logical = upr.getLogical();
        } else {
            return true;
        }
        return SubjectUtil.hasPermission(token, requiresPermissions, logical);
    }

    /**
     * 检查角色是否符合
     */
    public static boolean checkRole(Token token, HttpServletRequest request, HttpServletResponse response, Object handler, UrlPerm urlPerm) {
        Method method = ((HandlerMethod) handler).getMethod();
        RequiresRoles annotation = method.getAnnotation(RequiresRoles.class);
        if (annotation == null) {  // 方法上没有注解再检查类上面有没有注解
            annotation = method.getDeclaringClass().getAnnotation(RequiresRoles.class);
        }
        String[] requiresRoles;
        Logical logical;
        if (annotation != null) {
            requiresRoles = annotation.value();
            logical = annotation.logical();
        } else if (urlPerm != null) {
            UrlPermResult upr = urlPerm.getRoles(request, response, (HandlerMethod) handler);
            requiresRoles = upr.getValues();
            logical = upr.getLogical();
        } else {
            return true;
        }
        return SubjectUtil.hasRole(token, requiresRoles, logical);
    }

    /**
     * 检查是否是没有权限或没有角色
     */
    public static boolean isNoPermission(Token token, HttpServletRequest request, HttpServletResponse response, Object handler, UrlPerm urlPerm) {
        return !checkPermission(token, request, response, handler, urlPerm) || !checkRole(token, request, response, handler, urlPerm);
    }

    /**
     * 放行options请求
     */
    public static void passOptions(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, x-requested-with, X-Custom-Header, Authorization");
    }

    /**
     * 取出前端传递的token
     */
    public static String takeToken(HttpServletRequest request) {
        String access_token = request.getParameter("access_token");
        if (access_token == null || access_token.trim().isEmpty()) {
            access_token = request.getHeader("Authorization");
//            if (access_token != null && access_token.length() >= 7) {
//                access_token = access_token.substring(7);
//            }
        }
        return access_token;
    }

}
