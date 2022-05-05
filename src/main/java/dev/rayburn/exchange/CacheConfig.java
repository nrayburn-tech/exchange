package dev.rayburn.exchange;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport {

	private static final String detailsCache = "details";
	private static final String ratesCache = "rates";

	@Bean
	public CacheManager cacheManager(Caffeine caffeine) {
		CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager(detailsCache, ratesCache);
		caffeineCacheManager.setCaffeine(caffeine);

		return caffeineCacheManager;
	}

	@Bean
	public Caffeine caffeineConfig() {
		return Caffeine
				.newBuilder()
				.expireAfterWrite(Duration.ofMinutes(5))
				.maximumSize(250)
				.recordStats();
	}

	@Bean
	public Cache detailCache(CacheManager cacheManager) {
		return cacheManager.getCache(detailsCache);
	}

	@Bean
	public Cache ratesCache(CacheManager cacheManager) {
		return cacheManager.getCache(ratesCache);
	}
}
