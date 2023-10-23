package org.ashok.invoiceservice.domain;

import java.util.List;


public interface InvoiceRepository {
	
	List<Invoice> findByUserIdAndMonth(UserMonth userMonth);
	List<Invoice> findByUserId(String email);
	void save(Invoice invoice);

}
