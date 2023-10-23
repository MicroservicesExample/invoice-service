package org.ashok.invoiceservice.domain;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.validation.Valid;

@Service
public class InvoiceService {
	
	private final InvoiceRepository repository;
	
	public InvoiceService(InvoiceRepository repository) {
		this.repository = repository;
	}
	
	public List<Invoice> findOrCreateInvoice(@Valid UserMonth userMonth) {
		return repository.findByUserIdAndMonth(userMonth);
		
	}

}
