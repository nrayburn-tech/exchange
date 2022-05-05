package dev.rayburn.exchange.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FolderValidator implements ConstraintValidator<FolderConstraint, String> {

	// Don't think the AppConfig.apiDateTimeFormatter can be autowired here.
	private static final DateTimeFormatter apiDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	@Override
	public void initialize(FolderConstraint constraintAnnotation) {
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		// Throws on a `null` value or invalid format.
		LocalDate.parse(value, apiDateTimeFormatter);

		return true;
	}
}
