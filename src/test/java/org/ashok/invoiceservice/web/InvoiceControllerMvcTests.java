package org.ashok.invoiceservice.web;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import org.apache.catalina.security.SecurityConfig;
import org.ashok.invoiceservice.domain.Invoice;
import org.ashok.invoiceservice.domain.InvoiceService;
import org.ashok.invoiceservice.domain.UserMonth;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;



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
@Import(SecurityConfig.class)
public class InvoiceControllerMvcTests {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	InvoiceService service;
	
	@MockBean
	JwtDecoder jwtDecoder;
	
	@Test
	void whenGetInvoiceNotExistingReturnsEmpty() throws Exception {
		
		UserMonth um = new UserMonth("invalid@gmail.com","jan");
		
		given(service.findOrCreateInvoice(um))
			.willReturn(Collections.emptyList());
		
		mockMvc
			.perform(get("/invoices?email=invalid@gmail.com&month=jan")
					
						.with(SecurityMockMvcRequestPostProcessors.jwt()
								.authorities(new SimpleGrantedAuthority("ROLE_user"))
							)		
					)
			
			.andExpect(status().isOk())
			.andExpect(result -> result.getResponse().equals("[]"));
	}
	
	
	
	@Test
	void whenGetInvoiceByIdNotAuthenticatedThenReturns401() throws Exception {
		
		mockMvc
			.perform(get("/invoices/1234"))
			.andExpect(MockMvcResultMatchers.status().isUnauthorized());
	}
	
	
	
	@Test
	void whenGetInvoiceByIdExistingThenReturnsInvoice() throws Exception {
		long id = 1234;
		var invoice = Invoice.of("testuser@gmail.com", "test.pdf", 10, "jan", LocalDate.now());
		
		given(service.findById(id))
				.willReturn(Optional.of(invoice));
		
		mockMvc
			.perform(get("/invoices/1234")
					
					.with(SecurityMockMvcRequestPostProcessors.jwt()
							.authorities(new SimpleGrantedAuthority("ROLE_user"))
						)		
				)
			.andExpect(status().isOk())
			.andExpect(result -> result.getResponse().equals(invoice));
	}
}
