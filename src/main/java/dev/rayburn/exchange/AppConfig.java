package dev.rayburn.exchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.rayburn.exchange.logger.LoggingClientConnector;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

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
		final int timeout = 10000;
		final HttpClient httpClient = HttpClient
				.create()
				.headers((httpHeaders) -> {
					httpHeaders.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
					httpHeaders.set(HttpHeaders.ACCEPT_CHARSET, StandardCharsets.UTF_8);
					httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
				})
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout)
				.responseTimeout(Duration.ofMillis(timeout))
				.doOnConnected(connection ->
						connection.addHandlerLast(new ReadTimeoutHandler(timeout, TimeUnit.MILLISECONDS))
								.addHandlerLast(new WriteTimeoutHandler(timeout, TimeUnit.MILLISECONDS))
				);

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
