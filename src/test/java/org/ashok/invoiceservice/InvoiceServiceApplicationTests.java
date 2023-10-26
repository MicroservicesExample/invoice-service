package org.ashok.invoiceservice;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.ashok.invoiceservice.domain.Invoice;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;


/**
 * @SpringBootTest: Will load entire application context and will start the servlet container on random port
 * @author Ashok Mane
 *
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("testdata")
class InvoiceServiceApplicationTests {

	@Autowired
	WebTestClient webTestClient; //
	
	@Test
	void whenGetRequestThenInvoiceReturned() {
		
		var expectedInvoice = Invoice.of("test@gmail.com", "url.pdf", 6500, "jan",LocalDate.now().plusMonths(1));
		webTestClient
			.get()
			.uri("/invoices?email=test@gmail.com&month=jan")
			.exchange()
			.expectStatus().isOk()
			.expectBodyList(Invoice.class).value(invoiceList -> {
				var actualInvoice = invoiceList.get(0);
				assertThat(actualInvoice).isNotNull();
				assertThat(actualInvoice.forMonth()
						.equals(expectedInvoice.forMonth()));
				assertThat(actualInvoice.dueDate())
						.isEqualTo(expectedInvoice.dueDate());
			});
			
		
	}

}
