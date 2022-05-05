package dev.rayburn.exchange.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FolderValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FolderConstraint {
	Class<?>[] groups() default {};

	String message() default "Invalid folder name.";

	Class<? extends Payload>[] payload() default {};
}
