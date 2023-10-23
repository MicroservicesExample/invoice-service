package org.ashok.invoiceservice.web;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.ashok.invoiceservice.domain.InvoiceService;
import org.ashok.invoiceservice.domain.UserMonth;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;


/**
 * @WebMvcTest: MockMvc is a utility class that lets you test web endpoints without loading a server like Tomcat.
 * This annotation will disable full auto-configuration and instead apply only
 * configuration relevant to MVC tests- till the controllers, you can further limit to specific controller(s)
 * by adding them in the annotation like below.
 * but not @Component, @Service & @Repository
 * see Java doc for @WebMvcTest
 * @author Ashok Mane
 *
 */

@WebMvcTest(controllers = {InvoiceController.class})
public class InvoiceControllerMvcTests {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	InvoiceService service;
	
	@Test
	void whenGetInvoiceNotExistingReturnsEmpty() throws Exception {
		
		UserMonth um = new UserMonth("invalid@gmail.com","jan");
		
		given(service.findOrCreateInvoice(um))
			.willReturn(Collections.emptyList());
		
		mockMvc
			.perform(get("/invoices?email=invalid@gmail.com&month=jan"))
			.andExpect(status().isOk())
			.andExpect(result -> result.getResponse().equals("[]"));
	}
}
