package dev.rayburn.exchange.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.ZoneId;

public class TimeZoneValidator implements ConstraintValidator<TimeZoneConstraint, String> {
	@Override
	public void initialize(TimeZoneConstraint constraintAnnotation) {
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return ZoneId.getAvailableZoneIds().contains(value);
	}
}
