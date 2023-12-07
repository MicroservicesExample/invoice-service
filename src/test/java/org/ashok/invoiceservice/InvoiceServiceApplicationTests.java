package org.ashok.invoiceservice;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Map;

import org.ashok.invoiceservice.domain.Invoice;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.startupcheck.MinimumDurationRunningStartupCheckStrategy;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;




/**
 * @SpringBootTest: Will load entire application context and will start the servlet container on random port
 * @author Ashok Mane
 *
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers //Activates automatic startup and cleanup of test containers
class InvoiceServiceApplicationTests {

	private static final DockerImageName AUTH_SERVICE_IMAGE = DockerImageName.parse("ghcr.io/microservicesexample/auth-service:latest");
	
	@Container
	 private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:14.4")
	   .withDatabaseName("invoiceService").withUsername("postgres").withPassword("postgres");
	 
	static {
		postgreSQLContainer.start();
	}
	
	@Container
	private static final GenericContainer<?> authServiceContainer;
	static {
		authServiceContainer = new GenericContainer<>(AUTH_SERVICE_IMAGE)
	    .withEnv(
	    			Map.of(
	    					"app.client.registration.client-id", "test",
	    					"app.client.registration.client-secret","{noop}testsecret",
	    					"spring.profiles.active", "testdata" //h2 db
	    					
	     				)
	    		)
		.withExposedPorts(9000)
		.withStartupCheckStrategy(
					new MinimumDurationRunningStartupCheckStrategy(Duration.ofSeconds(5))
				)
	    
		.waitingFor(Wait.forHttp("/"));
		
		authServiceContainer.start();
	//	final String logs = authServiceContainer.getLogs();
	//	System.out.println("Container logs ---------"+ logs);
		

		
	}
	private static AccessToken userAccessToken;
	
	
	@Autowired
	WebTestClient webTestClient;
			
	@DynamicPropertySource
	static void dynamicProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.driver-class-name",() -> "org.postgresql.Driver");
		registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
		registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
		registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
		
		
		registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
						() -> getAuthServiceContainerUrl());
	}
	
	
	static String getAuthServiceContainerUrl() {
		return "http://" + authServiceContainer.getHost() + ":" + authServiceContainer.getMappedPort(9000);
	}

	@BeforeAll
	static void generateAccessToken() {
		WebClient webClient = WebClient.builder()
								.baseUrl(getAuthServiceContainerUrl()+"/oauth2/token")
								.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
								//.defaultHeader("Content-Type", "application/x-www-form-urlencoded")
								.build();
		
		userAccessToken = authenticateClient(webClient, "test", "testsecret");
		System.out.println("****userAccessToken:" + userAccessToken);
	//	final String logs = authServiceContainer.getLogs();
	//	System.out.println("Container logs ---------"+ logs);
	}
	
	static AccessToken authenticateClient(WebClient webClient, String clientId, String clientSecret) {
		
		AccessToken response = 
		    webClient
				.post()
				.headers(httpHeaders -> setAuthorizationHeader(httpHeaders, clientId, clientSecret))
				.body(
						BodyInserters.fromFormData("grant_type", "client_credentials")
																
					)
				.retrieve()
				.bodyToMono(AccessToken.class)
				.block();
				
		System.out.println("*******access token response: " + response);
		return response;
	}

	
	
	private static void setAuthorizationHeader(HttpHeaders httpHeaders, String clientId, String clientSecret) {
		String headerValue = clientId + ":" + clientSecret;
		httpHeaders.add("Authorization", "Basic " + Base64.getEncoder().encodeToString(headerValue.getBytes()) );
	}


	@Test
	void whenGetRequestWithAccessTokenThenInvoiceReturned() {
		
		var expectedInvoice = Invoice.of("test@gmail.com", "url.pdf", 6500, "jan",LocalDate.now().plusMonths(1));
		webTestClient
			.get()
			.uri("/invoices?email=test@gmail.com&month=jan")
			.headers(headers -> headers.setBearerAuth(userAccessToken.token()))
			.exchange()
			.expectStatus().isOk()
			.expectBodyList(Invoice.class).value(invoiceList -> {
				var actualInvoice = invoiceList.get(0);
				assertThat(actualInvoice).isNotNull();
				assertThat(actualInvoice.forMonth()
						.equals(expectedInvoice.forMonth()));
				assertThat(actualInvoice.dueDate())
						.isEqualTo(expectedInvoice.dueDate());
			});
			
		
	}
	
		@Test
	void whenGetRequestWithoutAccessTokenThen401() {
		
		webTestClient
			.get()
			.uri("/invoices?email=test@gmail.com&month=jan")
			.exchange()
			.expectStatus().isUnauthorized();
		
	}
	
	
	private record AccessToken(String token) {
		
		@JsonCreator
		private AccessToken(@JsonProperty("access_token") final String token){
			this.token = token;
		}
	}
		
}
