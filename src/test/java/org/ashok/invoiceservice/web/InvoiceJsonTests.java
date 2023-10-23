package org.ashok.invoiceservice.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

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
		var invoice = new Invoice(1234567890L,"test@gmail.com", null, 5000, "jan" );
		var jsonContent = json.write(invoice);
		
				
		assertThat(jsonContent).extractingJsonPathNumberValue("@.id")
					.isEqualTo(invoice.id().intValue());
		assertThat(jsonContent).extractingJsonPathStringValue("@.userId")
					.isEqualTo(invoice.userId());
		assertThat(jsonContent).extractingJsonPathNumberValue("@.amount")
					.isEqualTo(invoice.amount());
		assertThat(jsonContent).extractingJsonPathStringValue("@.month")
					.isEqualTo(invoice.month());
		
	}
	
	@Test
	void testDeserialize() throws IOException {
		var content = """
			{
			    "id": 1679231595571,
			    "userId": "test@gmail.com",
			    "pdfUrl": null,
			    "amount": 6500,
			    "month": "jan"
			}
				""";
		
		assertThat(json.parse(content))
			.usingRecursiveComparison()
			.isEqualTo(new Invoice(1679231595571L, "test@gmail.com", null, 6500, "jan"));
	}

}
