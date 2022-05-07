package dev.rayburn.exchange.logger;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.net.URI;
import java.util.function.Function;

public class LoggingClientConnector extends ReactorClientHttpConnector {
	public LoggingClientConnector(HttpClient httpClient) {
		super(httpClient);
	}

	@Override
	@NonNull
	public Mono<ClientHttpResponse> connect(@NonNull HttpMethod method, @NonNull URI uri, @NonNull Function<? super ClientHttpRequest, Mono<Void>> requestCallback) {
		return super.connect(method, uri, cb -> requestCallback.apply(new HttpServletDecorator.ClientRequestDecorator(cb))).map(HttpServletDecorator.ClientResponseDecorator::new);
	}
}
