package com.careeros.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Service for Redis caching operations
 */
@Service
public class CacheService {
    
    private static final Logger logger = LoggerFactory.getLogger(CacheService.class);
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    /**
     * Cache a value with expiration
     */
    public void cacheValue(String key, Object value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
            logger.debug("Cached value for key: {}", key);
        } catch (Exception e) {
            logger.error("Error caching value for key: {}", key, e);
        }
    }
    
    /**
     * Get cached value
     */
    public Object getCachedValue(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            logger.error("Error retrieving cached value for key: {}", key, e);
            return null;
        }
    }
    
    /**
     * Remove cached value
     */
    public void evictCache(String key) {
        try {
            redisTemplate.delete(key);
            logger.debug("Evicted cache for key: {}", key);
        } catch (Exception e) {
            logger.error("Error evicting cache for key: {}", key, e);
        }
    }
    
    /**
     * Check if key exists in cache
     */
    public boolean hasKey(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            logger.error("Error checking cache key existence: {}", key, e);
            return false;
        }
    }
}
