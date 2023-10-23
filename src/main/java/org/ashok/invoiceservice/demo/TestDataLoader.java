package org.ashok.invoiceservice.demo;

import org.ashok.invoiceservice.domain.Invoice;
import org.ashok.invoiceservice.domain.InvoiceRepository;
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
		 repository.save(new Invoice(1679231595571L, "test@gmail.com", null, 6500, "jan"));
		 repository.save(new Invoice(1679231595571L, "test@gmail.com", null, 6500, "feb"));
		 repository.save(new Invoice(1679231595571L, "test@gmail.com", null, 6500, "mar"));
	}
}
