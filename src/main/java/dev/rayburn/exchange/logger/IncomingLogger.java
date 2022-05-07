package dev.rayburn.exchange.logger;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * This applies to any request coming into the server.  It does not apply to any WebClient request.
 */
@Component
@Log4j2
public class IncomingLogger implements WebFilter {
	@Override
	@NonNull
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		final ServerHttpRequestDecorator loggingServerHttpRequestDecorator = new HttpServletDecorator.ServerRequestDecorator(exchange.getRequest());
		final ServerHttpResponseDecorator loggingServerHttpResponseDecorator = new HttpServletDecorator.ServerResponseDecorator(exchange.getResponse());

		return chain.filter(exchange.mutate().request(loggingServerHttpRequestDecorator).response(loggingServerHttpResponseDecorator).build());
	}
}
