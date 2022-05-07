package dev.rayburn.exchange.logger;

import lombok.extern.log4j.Log4j2;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.http.client.reactive.ClientHttpRequestDecorator;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.http.client.reactive.ClientHttpResponseDecorator;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.lang.NonNull;
import org.springframework.util.StreamUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;

@Log4j2
public class HttpServletDecorator {

	private HttpServletDecorator() {
	}

	private static Flux<? extends DataBuffer> logBody(@NonNull Publisher<? extends DataBuffer> body, final String logLabel) {
		final StringBuilder bodyBuilder = new StringBuilder();
		return Flux.from(body).doOnNext(dataBuffer -> {
			if (log.isDebugEnabled()) { // Only doing this when logging is enabled should save some performance.
				try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
					Channels.newChannel(byteArrayOutputStream).write(dataBuffer.asByteBuffer().asReadOnlyBuffer());
					bodyBuilder.append(StreamUtils.copyToString(byteArrayOutputStream, StandardCharsets.UTF_8));
				} catch (IOException e) {
					log.error("Failed to log " + logLabel + ".", e);
				}
			}
		}).doOnTerminate(() -> log.debug(logLabel + ":\n{}", () -> bodyBuilder));
	}

	public static class ClientRequestDecorator extends ClientHttpRequestDecorator {

		public ClientRequestDecorator(ClientHttpRequest delegate) {
			super(delegate);
		}

		@Override
		@NonNull
		public Mono<Void> writeWith(@NonNull Publisher<? extends DataBuffer> body) {
			return super.writeWith(logBody(body, "Client Request"));
		}
	}

	public static class ClientResponseDecorator extends ClientHttpResponseDecorator {
		public ClientResponseDecorator(ClientHttpResponse delegate) {
			super(delegate);
		}

		@Override
		@NonNull
		public Flux<DataBuffer> getBody() {
			return (Flux<DataBuffer>) logBody(super.getBody(), "Client Response");
		}
	}

	public static class ServerRequestDecorator extends ServerHttpRequestDecorator {
		public ServerRequestDecorator(ServerHttpRequest delegate) {
			super(delegate);
		}

		@Override
		@NonNull
		public Flux<DataBuffer> getBody() {
			return (Flux<DataBuffer>) logBody(super.getBody(), "Server Request");
		}
	}

	public static class ServerResponseDecorator extends ServerHttpResponseDecorator {

		public ServerResponseDecorator(ServerHttpResponse delegate) {
			super(delegate);
		}

		@Override
		@NonNull
		public Mono<Void> writeWith(@NonNull Publisher<? extends DataBuffer> body) {
			return super.writeWith(logBody(body, "Server Response"));
		}
	}
}
