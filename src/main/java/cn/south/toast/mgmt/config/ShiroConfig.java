package cn.south.toast.mgmt.config;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import cn.south.toast.mgmt.shiro.ShiroDBRealm;
import cn.south.toast.mgmt.shiro.URLAuthorizationFilter;
import cn.south.toast.mgmt.shiro.cache.ShiroRedisCacheManager;


/**
 * shiro配置类
 * 
 * @author huangbh
 *
 */
@Configuration
public class ShiroConfig {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Bean
	@DependsOn("redisTemplate")
	public ShiroRedisCacheManager getShiroRedisCacheManager() {
		ShiroRedisCacheManager shiroRedisCacheManager = new ShiroRedisCacheManager();
		RedisCacheManager redisCacheManager = new RedisCacheManager(redisTemplate);
		shiroRedisCacheManager.setRedisCacheManager(redisCacheManager);
		shiroRedisCacheManager.setRedisTemplate(redisTemplate);
		return shiroRedisCacheManager;
	}

	@Bean(name = "realm")
	public Realm getRealm() {
		ShiroDBRealm realm = new ShiroDBRealm();
		realm.setCacheManager(getShiroRedisCacheManager());
		return realm;
	}

	@Bean(name = "securityManager")
	public SecurityManager getSecurityManager() {
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		securityManager.setCacheManager(getShiroRedisCacheManager());
		securityManager.setRealm(getRealm());
		return securityManager;
	}

	@Bean
	public ShiroFilterFactoryBean shiroFilter() {
		ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
		shiroFilterFactoryBean.setSecurityManager(getSecurityManager());
		// 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
		shiroFilterFactoryBean.setLoginUrl("/index_dev.html");
		// 未授权界面;
		shiroFilterFactoryBean.setUnauthorizedUrl("/error/401");
		// 拦截器
		Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
		// 配置不会被拦截的链接 顺序判断
		filterChainDefinitionMap.put("/**/**.*", "anon");
		filterChainDefinitionMap.put("/sail/permission/*", "anon");
		filterChainDefinitionMap.put("/error/*", "anon");
//		filterChainDefinitionMap.put("/api/**", "url");
//		filterChainDefinitionMap.put("/**", "authc");
		shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

//		LinkedHashMap<String, Filter> filtersMap = new LinkedHashMap<String, Filter>();
//		filtersMap.put("url", getURLAuthorizationFilter());
//		shiroFilterFactoryBean.setFilters(filtersMap);

		return shiroFilterFactoryBean;
	}

	public AuthorizationFilter getURLAuthorizationFilter() {
		return new URLAuthorizationFilter();
	}
}
