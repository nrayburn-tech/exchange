package dev.rayburn.exchange.detail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Locale;
import java.util.Map;

@Service
public class DetailService {

	@Autowired
	private Cache detailCache;
	@Autowired
	private DetailCaller detailCaller;

	// TODO: Really just being lazy and not making another Cache for this...
	private Map<String, String> supportedCurrencyMapCache = null;

	public void clearCache() {
		detailCache.invalidate();
	}

	public Mono<Detail> getDetail(final String currencyCode) {

		final Detail detail = detailCache.get(currencyCode.toUpperCase(Locale.ROOT), Detail.class);
		if (detail != null) {
			return Mono.justOrEmpty(detail);
		}

		return detailCaller
				.getDetailList()
				.doOnSuccess(detailMap -> detailMap.forEach((key, value) -> detailCache.put(key.toUpperCase(Locale.ROOT), value)))
				.map(detailMap -> detailMap.get(currencyCode.toUpperCase(Locale.ROOT)));
	}

	public Mono<Map<String, String>> getSupportedCurrencyMap() {
		if (supportedCurrencyMapCache != null) {
			return Mono.justOrEmpty(supportedCurrencyMapCache);
		}
		return detailCaller
				.getSupportedCurrencyMap()
				.doOnSuccess(stringStringMap -> supportedCurrencyMapCache = stringStringMap);
	}
}
