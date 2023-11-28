package org.ashok.invoiceservice.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.ashok.invoiceservice.config.DataConfig;
import org.ashok.invoiceservice.domain.Invoice;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.test.context.ActiveProfiles;

@DataJdbcTest //each test runs in a transaction and rolls it back at its end, keeping the database clean.
@Import(DataConfig.class)
/*Disables the default behavior of relying on an embedded test database since we want to use
Testcontainers*/
@AutoConfigureTestDatabase( replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles({"integration","testdata"})
public class InvoiceRepositoryJdbcTests {
	
	@Autowired
	InvoiceRepository repository;
	
	@Autowired
	JdbcAggregateTemplate jdbcAggregateTemplate; //is used to prepare the data targeted by the test
	
	@Test
	void findInvoiceByUserIdAndMonthWhenExisting() {
		
		var invoice = Invoice.of("testuser@test.com", "test.pdf", 500, "dec", LocalDate.now().plusMonths(1));
		jdbcAggregateTemplate.insert(invoice);
		
		var resultInvoice = repository.findByUserIdAndForMonth("testuser@test.com", "dec");
		assertThat(resultInvoice).isPresent();
		assertThat(resultInvoice.get().userId())
			.isEqualTo(invoice.userId());
		assertThat(resultInvoice.get().dueDate())
			.isEqualTo(invoice.dueDate());
	}
}
