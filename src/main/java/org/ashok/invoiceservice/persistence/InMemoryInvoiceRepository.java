package org.ashok.invoiceservice.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.ashok.invoiceservice.domain.Invoice;
import org.ashok.invoiceservice.domain.InvoiceRepository;
import org.ashok.invoiceservice.domain.UserMonth;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryInvoiceRepository implements InvoiceRepository {

	private List<Invoice> invoices;
	
	
	public InMemoryInvoiceRepository() {
		this.invoices = new ArrayList<>();
	}
	
	@Override
	public void save(Invoice invoice) {
		if(invoice!= null) {
			invoices.add(invoice);
		}
	}
	
	@Override
	public List<Invoice> findByUserIdAndMonth(UserMonth userMonth) {
		if(userMonth.month().toLowerCase().equals("all")) {
			
			return findByUserId(userMonth.userId());
		}
		else {
			return this.invoices
					.stream()
					.filter(invoice -> invoice.userId().equals(userMonth.userId()) && invoice.month().equals(userMonth.month()))
					.collect(Collectors.toList());
		}
	}

	@Override
	public List<Invoice> findByUserId(String email) {
		return this.invoices
				.stream()
				.filter(invoice -> invoice.userId().equals(email))
				.collect(Collectors.toList());
	}

}
