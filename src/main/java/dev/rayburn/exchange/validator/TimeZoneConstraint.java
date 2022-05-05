package dev.rayburn.exchange.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TimeZoneValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TimeZoneConstraint {
	Class<?>[] groups() default {};

	String message() default "Invalid or unsupported time zone.";

	Class<? extends Payload>[] payload() default {};
}
