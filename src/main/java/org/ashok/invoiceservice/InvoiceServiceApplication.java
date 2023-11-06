package org.ashok.invoiceservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class InvoiceServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InvoiceServiceApplication.class, args);
	}
	
	
	/*
	 * @Bean ApplicationRunner dsRunner(DataSource ds) { return args -> {
	 * System.out.println("Current ds in application :" + ds.getConnection()); }; }
	 */
	 


}

@EnableConfigurationProperties
@ConfigurationProperties(prefix="invoice")
@Component
class MyProperties {
	private String greeting;

	public String getGreeting() {
		return greeting;
	}

	public void setGreeting(String greeting) {
		this.greeting = greeting;
	}
	
}



@RestController
class DefaultController {
	
	private final MyProperties props;
	
	public DefaultController(MyProperties props) {
		this.props = props;
	}

	@GetMapping("/")
	public String greeting() {
		return props.getGreeting();
	}
}