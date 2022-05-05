package dev.rayburn.exchange.rate;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/rate")
public class RateController {

	@Autowired
	private RateService rateService;

	@GetMapping("/clearCache")
	public void clearCache() {
		rateService.clearCache();
	}

	@GetMapping("/{currency}")
	public Mono<JsonNode> getCurrency(final @PathVariable String currency, final @Valid RateRequest rateRequest) {
		return rateService.getRate(currency, rateRequest);
	}
}
