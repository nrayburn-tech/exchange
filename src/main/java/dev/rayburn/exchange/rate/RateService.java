package dev.rayburn.exchange.rate;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class RateService {

	@Autowired
	private DateTimeFormatter apiDateTimeFormatter;
	@Autowired
	private RateCaller rateCaller;
	@Autowired
	private Cache ratesCache;

	public void clearCache() {
		ratesCache.invalidate();
	}

	public Mono<JsonNode> getRate(final String currency, final RateRequest rateRequest) {
		if (!StringUtils.hasText(currency)) {
			throw new IllegalArgumentException("Currency must not be empty.");
		}
		String folder = rateRequest.getFolder();
		final String timeZone = rateRequest.getTimeZone();
		final ZonedDateTime zonedDateTime = LocalDate.parse(folder, apiDateTimeFormatter).atStartOfDay().atZone(ZoneId.of(timeZone));
		folder = apiDateTimeFormatter.format(zonedDateTime);

		// TODO: There is likely a better way of creating the cache key.
		//  Maybe move currency back into RateRequest.
		final JsonNode value = ratesCache.get(currency + " - " + rateRequest, JsonNode.class);
		if (value != null) {
			return Mono.justOrEmpty(value);
		}

		return rateCaller
				.getCurrency(currency.toLowerCase(Locale.ROOT), folder)
				.map(node -> {
					if (!node.get("date").asText().equals(rateRequest.getFolder())) {
						throw new RuntimeException("Data is not available for requested date " + rateRequest.getFolder());
					}
					return node;
				})
				// TODO: If the map throws, does this still run if the http status is 2xx?
				.doOnSuccess(body -> ratesCache.put(currency + " - " + rateRequest, body));
	}

}
