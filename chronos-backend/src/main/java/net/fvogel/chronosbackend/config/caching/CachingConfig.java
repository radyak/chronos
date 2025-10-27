package net.fvogel.chronosbackend.config.caching;

import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
@EnableCaching
public class CachingConfig implements CacheManagerCustomizer<ConcurrentMapCacheManager> {

    public static final String CACHE_NAME_WIKI_ARTICLE = "wiki.article";
    public static final String CACHE_NAME_WIKI_ARTICLES_SEARCH = "wiki.articles.search";

    @Override
    public void customize(ConcurrentMapCacheManager cacheManager) {
        cacheManager.setCacheNames(Arrays.asList(
                CACHE_NAME_WIKI_ARTICLE,
                CACHE_NAME_WIKI_ARTICLES_SEARCH
        ));
    }

}
