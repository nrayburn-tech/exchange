package dev.rayburn.exchange.detail;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class DetailCaller {

	private final WebClient currencyWebClient;

	public DetailCaller(WebClient currencyWebClient) {
		this.currencyWebClient = currencyWebClient;
	}

	public Mono<Map<String, Detail>> getDetailList() {
		return currencyWebClient
				.get()
				.uri("/currency-api@{apiVersion}/other/Common-Currency.json", 1)
				.retrieve()
				.bodyToMono(new StringDetailMap())
				.retry(3);
	}

	public Mono<Map<String, String>> getSupportedCurrencyMap() {
		return currencyWebClient
				.get()
				.uri("/currency-api@{apiVersion}/other/currencies.json", 1)
				.retrieve()
				.bodyToMono(new StringStringMap())
				.retry(3);
	}

	private static class StringDetailMap extends ParameterizedTypeReference<Map<String, Detail>> {
	}

	private static class StringStringMap extends ParameterizedTypeReference<Map<String, String>> {
	}

}
