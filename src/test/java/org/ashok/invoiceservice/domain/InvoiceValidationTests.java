package org.ashok.invoiceservice.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class InvoiceValidationTests {

	private static Validator validator;
	
	
	@BeforeAll
	static void setup() {
		ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
		validator = validatorFactory.getValidator();
	}
	
	@Test
	void validationSuccess() {
		
		UserMonth um = new UserMonth("abc@abc.com", "jan");
		
		Set<ConstraintViolation<UserMonth>> violations = validator.validate(um);
		assertThat(violations).isEmpty();;
	}
	
	@Test
	void emptyEmailValidionFails() {
		UserMonth um = new UserMonth("", "jan");
		
		Set<ConstraintViolation<UserMonth>> violations = validator.validate(um);
		assertThat(violations).hasSize(1);
	}
	
	@Test
	void invalidEmailValidationFails() {
		UserMonth um = new UserMonth("abcabc.com", "jan");
		
		Set<ConstraintViolation<UserMonth>> violations = validator.validate(um);
		assertThat(violations).hasSize(1);
		
	}
	
	@Test
	void invalidMonthValidationFails() {
		UserMonth um = new UserMonth("abc@abc.com", "january");
		
		Set<ConstraintViolation<UserMonth>> violations = validator.validate(um);
		assertThat(violations).hasSize(1);
		System.out.println(violations);
	}
}
