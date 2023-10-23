package org.ashok.invoiceservice.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public record UserMonth(
		
		@NotEmpty(message = "Email should not be empty")
	    @Email
		String userId, 
		
		@NotEmpty(message = "Month should not be empty")
		@Pattern(regexp = "jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec|all")
		String month) {}
