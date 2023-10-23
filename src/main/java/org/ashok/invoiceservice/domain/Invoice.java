package org.ashok.invoiceservice.domain;

public record Invoice(		
		Long id,
		String userId,
		String pdfUrl,
		Integer amount,
		String month) {}