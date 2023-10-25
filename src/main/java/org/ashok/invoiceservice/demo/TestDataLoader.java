package org.ashok.invoiceservice.demo;

import java.util.List;

import org.ashok.invoiceservice.domain.Invoice;
import org.ashok.invoiceservice.persistence.InvoiceRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * This bean will be created only when testdata profile is active
 * You need to pass the VM argument (-Dspring.profiles.active=testdata) or environment variable while starting the app
 * @author Ashok Mane
 *
 */

@Component
@Profile("testdata")
public class TestDataLoader {

	private final InvoiceRepository repository;
	
	public TestDataLoader(InvoiceRepository repository) {
		this.repository = repository;
	}
	
	@EventListener(ApplicationReadyEvent.class)
	void loadTestData() {
		
		repository.deleteAll(); 		
		
		var invoice1 = Invoice.of("test@gmail.com", "url.pdf", 6500, "jan");
		var invoice2 = Invoice.of("test@gmail.com", "url.pdf", 6500, "feb");
		var invoice3 = Invoice.of("test@gmail.com", "url.pdf", 6500, "mar");
		
		repository.saveAll(List.of(invoice1, invoice2, invoice3));
	}
}
