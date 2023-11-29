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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJdbcTest //each test runs in a transaction and rolls it back at its end, keeping the database clean.
@Import(DataConfig.class)
/*Disables the default behavior of relying on an embedded test database since we want to use
Testcontainers*/
@AutoConfigureTestDatabase( replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers //Activates automatic startup and cleanup of test containers
public class InvoiceRepositoryJdbcTests {
	
	@Autowired
	InvoiceRepository repository;
	
	@Autowired
	JdbcAggregateTemplate jdbcAggregateTemplate; //is used to prepare the data targeted by the test
	
	@Container
	 private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:14.4")
	   .withDatabaseName("invoiceService").withUsername("postgres").withPassword("postgres");
	 
	static {
		postgreSQLContainer.start();
	}
	
	@DynamicPropertySource
	static void dynamicProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
		registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
		registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
				
	}
	
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
