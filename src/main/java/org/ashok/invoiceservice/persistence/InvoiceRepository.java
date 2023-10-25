package org.ashok.invoiceservice.persistence;

import java.util.List;
import java.util.Optional;

import org.ashok.invoiceservice.domain.Invoice;
import org.springframework.data.repository.CrudRepository;


public interface InvoiceRepository extends CrudRepository<Invoice, Long>{
	
	Optional<Invoice> findByUserIdAndMonth(String userId, String month);
	List<Invoice> findByUserId(String userId);
	
}
