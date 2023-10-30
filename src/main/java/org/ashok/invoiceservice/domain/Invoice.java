package org.ashok.invoiceservice.domain;

import java.time.Instant;
import java.time.LocalDate;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record Invoice(		
		
		@Id
		Long id,
		
				
		@NotBlank(message="user id must be defined")
		@Email(message="must be valid email address")
		String userId,
		
		@NotBlank(message="url must be defined")
		String pdfUrl,
		
		@NotNull(message="Invoice amount must be defined")
		@Positive(message="Invoice amount should be positive")
		Integer amount,
		
		@NotBlank(message = "month should be defined")
		@Pattern(regexp = "jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec|all")
		String forMonth,
		
		@NotBlank(message= "Due date should be defined")
		LocalDate dueDate,
		
		@Version // concurreny handling
		int version,
		
		@CreatedDate
		Instant createdDate,
		
		@LastModifiedDate
		Instant lastModifiedDate) 
	{
			public static Invoice of(String userId, String pdfUrl, int amount, String month, LocalDate dueDate) {
				return new Invoice(null, userId, pdfUrl, amount, month, dueDate, 0, null, null);
			}
	
	}