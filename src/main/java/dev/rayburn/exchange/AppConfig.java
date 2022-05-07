package dev.rayburn.exchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.rayburn.exchange.logger.LoggingClientConnector;
import io.netty.channel.ChannelOption;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

@Configuration
public class AppConfig {

	@Bean
	public DateTimeFormatter apiDateTimeFormatter() {
		return DateTimeFormatter.ofPattern("yyyy-MM-dd");
	}

	/**
	 * Creates a WebClient to use for calling the currency rates api.
	 * It is pre-configured for responses that are JSON/UTF-8 only.
	 * It also has a timeout of 10 seconds.
	 */
	@Bean
	public WebClient currencyWebClient() {
		final int connectTimeout = 2000;
		final int readWriteTimeout = 10000;
		final HttpClient httpClient = HttpClient
				.create()
				.headers((httpHeaders) -> {
					httpHeaders.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
					httpHeaders.set(HttpHeaders.ACCEPT_CHARSET, StandardCharsets.UTF_8);
					httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
				})
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
				.responseTimeout(Duration.ofMillis(readWriteTimeout));

		return WebClient
				.builder()
				.clientConnector(new LoggingClientConnector(httpClient))
				.baseUrl("https://cdn.jsdelivr.net/gh/fawazahmed0")
				.build();
	}

	@Bean
	public ObjectMapper objectMapper() {
		final ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.findAndRegisterModules();

		return objectMapper;
	}
}
