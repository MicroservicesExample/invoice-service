package org.ashok.invoiceservice.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.ashok.invoiceservice.domain.Invoice;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

/**
 * Using the @JsonTest annotation, you can test JSON serialization and
	deserialization for your domain objects
 * @author Ashok Mane
 *
 */

@JsonTest
public class InvoiceJsonTests {
	
	@Autowired
	JacksonTester<Invoice> json;
	
	@Test
	void testSerialize() throws IOException {
		var invoice = Invoice.of("test@gmail.com", "url.pdf", 5000, "jan",LocalDate.now().plusMonths(1) );
		var jsonContent = json.write(invoice);
		
				
		assertThat(jsonContent).extractingJsonPathNumberValue("@.id")
					.isNull();
		assertThat(jsonContent).extractingJsonPathStringValue("@.userId")
					.isEqualTo(invoice.userId());
		assertThat(jsonContent).extractingJsonPathNumberValue("@.amount")
					.isEqualTo(invoice.amount());
		assertThat(jsonContent).extractingJsonPathStringValue("@.forMonth")
					.isEqualTo(invoice.forMonth());
		assertThat(jsonContent).extractingJsonPathStringValue("@.dueDate")
					.isEqualTo(invoice.dueDate().toString());
		assertThat(jsonContent).extractingJsonPathNumberValue("@.version")
					.isEqualTo(0);
		assertThat(jsonContent).extractingJsonPathValue("@.created_date")
					.isNull();
		assertThat(jsonContent).extractingJsonPathValue("@.last_modified_date")
					.isNull();
		assertThat(jsonContent).extractingJsonPathValue("@.created_by")
					.isNull();
		assertThat(jsonContent).extractingJsonPathValue("@.last_modified_by")
					.isNull();
		
	}
	
	@Test
	void testDeserialize() throws IOException {
		var content = """
			{
			    "id": null,
			    "version":0,
			    "userId": "test@gmail.com",
			    "pdfUrl": "url.pdf",
			    "amount": 6500,
			    "forMonth": "jan",
			    "dueDate": "2030-01-01",
			    "created_date": null,
			    "last_modified_date": null,
			    "created_by": null,
			    "last_modified_by": null
			}
				""";
		
		assertThat(json.parse(content))
			.usingRecursiveComparison()
			.isEqualTo(Invoice.of("test@gmail.com", "url.pdf", 6500, "jan", LocalDate.parse("2030-01-01", DateTimeFormatter.ISO_LOCAL_DATE)));
	}

}
