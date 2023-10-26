package org.ashok.invoiceservice.domain;

import java.util.List;

import org.ashok.invoiceservice.persistence.InvoiceRepository;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;

@Service
public class InvoiceService {
	
	private final InvoiceRepository repository;
	private final InvoiceGenerator invoiceGenerator;
	
	public InvoiceService(InvoiceRepository repository, InvoiceGenerator invoiceGenerator) {
		this.repository = repository;
		this.invoiceGenerator = invoiceGenerator;
	}
	
	public List<Invoice> findOrCreateInvoice(@Valid UserMonth userMonth) {
		
		if(userMonth.month().equalsIgnoreCase("all")) {
			return repository.findByUserId(userMonth.userId());
		}
		
		var invoice = repository.findByUserIdAndForMonth(userMonth.userId(), userMonth.month());
		if(invoice.isEmpty()) {
			Invoice generatedInvoice = invoiceGenerator.generate(userMonth.userId(), userMonth.month());
    		repository.save(generatedInvoice);
    		return List.of(generatedInvoice);
		}
		
		return List.of(invoice.get());
				
	}

}
