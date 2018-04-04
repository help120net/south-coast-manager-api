package cn.south.toast.mgmt.shiro.cache;

import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;

/**
 * 
 * @author huangbh
 *
 */
public class ShiroRedisCache implements Cache {
	private static Logger logger = LoggerFactory.getLogger(ShiroRedisCache.class);
	private RedisTemplate<String, Object> redisTemplate;
	private RedisCache redisCache;

	public ShiroRedisCache(RedisCache redisCache, RedisTemplate<String, Object> redisTemplate) {
		this.redisCache = redisCache;
		this.redisTemplate = redisTemplate;
	}

	protected String getCachePrefix() {
		return redisCache.getName() + "_";
	}

	protected String getCacheName(Object key) {
		return getCachePrefix() + key;
	}

	@Override
	public Object get(Object key) throws CacheException {
		String cacheName = getCacheName(key);
		try {
			return redisTemplate.opsForValue().get(cacheName);
		} catch (Exception e) {
			logger.error("获取缓存" + cacheName + "失败", e);
			throw e;
		}
	}

	@Override
	public Object put(Object key, Object value) throws CacheException {
		String cacheName = getCacheName(key);
		try {
			redisTemplate.opsForValue().set(cacheName, value);
			return value;
		} catch (Exception e) {
			logger.error("设置缓存" + cacheName + "失败", e);
			throw e;
		}
	}

	@Override
	public Object remove(Object key) throws CacheException {
		String cacheName = getCacheName(key);
		try {
			List<String> list = new ArrayList<String>();
			list.add(cacheName);
			redisTemplate.delete(list);
			return null;
		} catch (Exception e) {
			logger.error("删除缓存" + cacheName + "失败", e);
			throw e;
		}
	}

	@Override
	public void clear() throws CacheException {
		try {
			redisTemplate.delete(this.keys());
		} catch (Exception e) {
			String CachePrefix = getCachePrefix();
			logger.error("清空缓存" + CachePrefix + "失败", e);
			throw e;
		}
	}

	@Override
	public int size() {
		int size = 0;
		try {
			size = this.keys().size();
		} catch (Exception e) {
			String CachePrefix = getCachePrefix();
			logger.error("获取缓存" + CachePrefix + "大小失败", e);
		}
		return size;
	}

	@Override
	public Set<String> keys() {
		String CachePrefix = getCachePrefix();
		Set<String> keysSet = new HashSet<String>();
		try {
			Set<String> resultSet = (Set<String>) redisTemplate.keys(CachePrefix + "*");
			if (!CollectionUtils.isEmpty(resultSet)) {
				keysSet = resultSet;
			}
			return keysSet;
		} catch (Exception e) {
			logger.error("获取缓存" + CachePrefix + "所有的键失败", e);
			throw e;
		}
	}

	@Override
	public Collection<Object> values() {
		Set<String> keysSet = this.keys();
		Collection<Object> values = new ArrayList<Object>();
		try {
			for (Object key : keysSet) {
				values.add(redisTemplate.opsForValue().get(key));
			}
			return values;
		} catch (Exception e) {
			String CachePrefix = getCachePrefix();
			logger.error("获取缓存" + CachePrefix + "所有的值失败", e);
			throw e;
		}
	}

}