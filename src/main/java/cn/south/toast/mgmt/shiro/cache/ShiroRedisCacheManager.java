package cn.south.toast.mgmt.shiro.cache;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 
 * @author huangbh
 *
 */
public class ShiroRedisCacheManager implements CacheManager {
	private RedisCacheManager redisCacheManager;
	private RedisTemplate<String, Object> redisTemplate;

	public void setRedisCacheManager(RedisCacheManager redisCacheManager) {
		this.redisCacheManager = redisCacheManager;
	}

	public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Override
	public <K, V> Cache<K, V> getCache(String name) throws CacheException {
		RedisCache redisCache = (RedisCache) redisCacheManager.getCache(name);
		return new ShiroRedisCache(redisCache, redisTemplate);
	}
}
