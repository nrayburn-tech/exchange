package dev.rayburn.exchange.rate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class RateCaller {
	private final WebClient currencyWebClient;
	private final ObjectMapper objectMapper;

	public RateCaller(WebClient currencyWebClient, ObjectMapper objectMapper) {
		this.currencyWebClient = currencyWebClient;
		this.objectMapper = objectMapper;
	}

	/**
	 * @param currency - The currency code to use.
	 * @param folder   - A string with the format 'yyyy-MM-dd' or 'latest'.
	 * @return - A JsonNode with the format of {date: string, [currency]: Map<string, number>}
	 */
	public Mono<JsonNode> getCurrency(final String currency, final String folder) {
		return currencyWebClient
				.get()
				.uri(
						"/currency-api@{apiVersion}/{folder}/currencies/{currency}.min.json",
						1, folder, currency
				)
				.retrieve()
				.bodyToMono(String.class)
				.map(body -> {
					try {
						return objectMapper.readTree(body);
					} catch (JsonProcessingException e) {
						throw new RuntimeException(e);
					}
				});
	}
}
