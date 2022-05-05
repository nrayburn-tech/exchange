package dev.rayburn.exchange.detail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/detail")
public class DetailController {
	@Autowired
	private DetailService detailService;

	@DeleteMapping("/clearCache")
	public void clearCache() {
		detailService.clearCache();
	}

	// TODO: This mapping feels odd with it being `/api/detail/detail/{currencyCode}`.
	@GetMapping("/detail/{currencyCode}")
	public Mono<Detail> getCurrency(final @PathVariable String currencyCode) {
		return detailService.getDetail(currencyCode);
	}

	@GetMapping("/currencyList")
	public Mono<Map<String, String>> getSupportedCurrencyMap() {
		return detailService.getSupportedCurrencyMap();
	}
}
