package com.yilers.jwtp.configuration;

import com.yilers.jwtp.TokenInterceptor;
import com.yilers.jwtp.perm.RestUrlPerm;
import com.yilers.jwtp.perm.SimpleUrlPerm;
import com.yilers.jwtp.perm.UrlPerm;
import com.yilers.jwtp.provider.JdbcTokenStore;
import com.yilers.jwtp.provider.JwtTokenStore;
import com.yilers.jwtp.provider.RedisTokenStore;
import com.yilers.jwtp.provider.TokenStore;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;
import java.util.Collection;

/**
 * JwtPermission自动配置
 * Created by wangfan on 2018-12-29 下午 2:11.
 */
@ComponentScan("com.yilers.jwtp.controller")
@EnableConfigurationProperties(JwtPermissionProperties.class)
public class JwtPermissionConfiguration implements WebMvcConfigurer, ApplicationContextAware {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private ApplicationContext applicationContext;
    @Autowired
    private JwtPermissionProperties properties;

    /**
     * 注入redisTokenStore
     */
    @ConditionalOnProperty(name = "jwtp.store-type", havingValue = "0")
    @Bean
    public TokenStore redisTokenStore() {
        DataSource dataSource = getBean(DataSource.class);
        org.springframework.data.redis.core.StringRedisTemplate stringRedisTemplate = getBean(org.springframework.data.redis.core.StringRedisTemplate.class);
        if (stringRedisTemplate == null) {
            logger.error("JWTP: StringRedisTemplate is null");
        }
        return new RedisTokenStore(stringRedisTemplate, dataSource);
    }

    /**
     * 注入jdbcTokenStore
     */
    @ConditionalOnProperty(name = "jwtp.store-type", havingValue = "1")
    @Bean
    public TokenStore jdbcTokenStore() {
        DataSource dataSource = getBean(DataSource.class);
        if (dataSource == null) {
            logger.error("JWTP: DataSource is null");
        }
        return new JdbcTokenStore(dataSource);
    }

    /**
     * 不存储token
     * jwtTokenStore
     */
    @Bean
    @ConditionalOnProperty(name = "jwtp.store-type", havingValue = "2")
    public TokenStore jwtTokenStore() {
        // token不限制
        properties.setMaxToken(-1);
        return new JwtTokenStore();
    }

    /**
     * 注入simpleUrlPerm
     */
    @ConditionalOnProperty(name = "jwtp.url-perm-type", havingValue = "0")
    @Bean
    public UrlPerm simpleUrlPerm() {
        return new SimpleUrlPerm();
    }

    /**
     * 注入restUrlPerm
     */
    @ConditionalOnProperty(name = "jwtp.url-perm-type", havingValue = "1")
    @Bean
    public UrlPerm restUrlPerm() {
        return new RestUrlPerm();
    }

    /**
     * 添加拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 获取TokenStore
        TokenStore tokenStore = getBean(TokenStore.class);
        // 给TokenStore添加配置参数
        if (tokenStore != null) {
            tokenStore.setMaxToken(properties.getMaxToken());
            tokenStore.setFindRolesSql(properties.getFindRolesSql());
            tokenStore.setFindPermissionsSql(properties.getFindPermissionsSql());
            tokenStore.setMTokenKey(properties.getSecretKey());
        } else {
            logger.error("JWTP: Unknown TokenStore");
        }
        // 获取UrlPerm
        UrlPerm urlPerm = getBean(UrlPerm.class);
        // 获取拦截路径
        String[] path = properties.getPath();
        // 获取排除路径
        String[] excludePath = properties.getExcludePath();
        TokenInterceptor interceptor = new TokenInterceptor(tokenStore, urlPerm);
        registry.addInterceptor(interceptor).addPathPatterns(path).excludePathPatterns(excludePath);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 获取Bean
     */
    private <T> T getBean(Class<T> clazz) {
        T bean = null;
        Collection<T> beans = applicationContext.getBeansOfType(clazz).values();
        while (beans.iterator().hasNext()) {
            bean = beans.iterator().next();
            if (bean != null) {
                break;
            }
        }
        return bean;
    }

}
