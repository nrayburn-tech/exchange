package dev.rayburn.exchange.rate;

import dev.rayburn.exchange.validator.FolderConstraint;
import dev.rayburn.exchange.validator.TimeZoneConstraint;
import lombok.Data;

@Data
public class RateRequest {
	@FolderConstraint
	private String folder;

	@TimeZoneConstraint
	private String timeZone;

	@Override
	public String toString() {
		return folder + " - " + timeZone;
	}
}
