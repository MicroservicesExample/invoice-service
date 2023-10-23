package org.ashok.invoiceservice.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class InvoiceControllerAdvice {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Map<String,String> handleValidationException(MethodArgumentNotValidException ex) {
		var errors = new HashMap<String, String>();
		ex.getBindingResult().getAllErrors().forEach(error -> {
			String fieldName = ((FieldError)error).getField();
			String errorMsg = error.getDefaultMessage();
			
			errors.put(fieldName, errorMsg);
		}); 
		
		return errors;
		
	}
	
	@ExceptionHandler(MissingServletRequestParameterException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Map<String,String> handleValidationException(MissingServletRequestParameterException ex) {
		var errors = new HashMap<String, String>();
		
		errors.put(ex.getParameterName(), ex.getMessage());
				
		return errors;
		
	}
}
