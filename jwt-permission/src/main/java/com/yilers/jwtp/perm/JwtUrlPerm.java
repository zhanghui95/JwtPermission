package com.yilers.jwtp.perm;

import com.yilers.jwtp.annotation.Logical;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** 
 *
 * @author hui.zhang
 * @date 2021/1/29 4:02 下午
 */ 
public class JwtUrlPerm implements UrlPerm {
    protected final Log logger = LogFactory.getLog(this.getClass());

    @Override
    public UrlPermResult getPermission(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) {
        String perm = request.getRequestURI();
        logger.debug("Generate Permissions: " + perm);
        return new UrlPermResult(new String[]{perm}, Logical.OR);
    }

    @Override
    public UrlPermResult getRoles(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) {
        return new UrlPermResult(new String[0], Logical.OR);
    }

}
