package org.ashok.invoiceservice.domain;


public interface InvoiceGenerator {
	
	Invoice generate(String userId, String month);
}
