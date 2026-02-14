package net.fvogel.chronos.schema.config.caching;

import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Configuration;

@EnableCaching
@Configuration
public class CachingConfig implements CacheManagerCustomizer<ConcurrentMapCacheManager> {

    @Override
    public void customize(ConcurrentMapCacheManager cacheManager) {
    }

    public static class CacheNames {
        public static final String SCHEMA_CACHE = "cache.schema";
        public static final String ENTITY_CACHE = "cache.entity";
    }

}
