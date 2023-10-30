package org.ashok.invoiceservice.web;

import java.util.Optional;

import org.ashok.invoiceservice.domain.Invoice;
import org.ashok.invoiceservice.domain.InvoiceService;
import org.ashok.invoiceservice.domain.UserMonth;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("invoices")
public class InvoiceController {

	private final InvoiceService service;
	
	public InvoiceController(InvoiceService service) {
		this.service = service;
	}
	
	@GetMapping
	public Iterable<Invoice> getByUserEmailAndMonth(@RequestParam @Email @NotEmpty @Valid String email,
													@RequestParam @NotEmpty @Valid String month) {
		return service.findOrCreateInvoice(new UserMonth(email, month));
	}
	
	@GetMapping("{id}")
	public Optional<Invoice> getById(@PathVariable @Valid @NotNull Long id) {
		return service.findById(id);
	}
	
}
