package org.ashok.invoiceservice;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Map;

import org.ashok.invoiceservice.domain.Invoice;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.containers.GenericContainer;
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
@ActiveProfiles({"integration","proddata"})
@Testcontainers //Activates automatic startup and cleanup of test containers
class InvoiceServiceApplicationTests {

	private static final DockerImageName AUTH_SERVICE_IMAGE = DockerImageName.parse("ghcr.io/microservicesexample/auth-service:latest");
	
	@Container
	private static final GenericContainer<?> authServiceContainer = new GenericContainer<>(AUTH_SERVICE_IMAGE)
	    .withEnv(
	    			Map.of(
	    					"app.client.registration.client-id", "test",
	    					"app.client.registration.client-secret","testsecret",
	    					"spring.profiles.active", "testdata", //h2 db
	    					"app.users[0].username", "testuser",
	    					"app.users[0].password", "{noop}1234",
	    					"app.users[0].roles[0]", "USER"
	     				)
	    		)
		.withExposedPorts(9000)
	    .waitingFor(Wait.forHttp("/"));
	
	private static AccessToken userAccessToken;
	
	
	@Autowired
	WebTestClient webTestClient;
			
	@DynamicPropertySource
	static void dynamicProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
						() -> getAuthServiceContainerUrl());
	}
	
	
	static String getAuthServiceContainerUrl() {
		return "http://" + authServiceContainer.getHost() + ":" + authServiceContainer.getMappedPort(9000);
	}

	@BeforeAll
	static void generateAccessToken() {
		WebClient webClient = WebClient.builder()
								.baseUrl(getAuthServiceContainerUrl())
								.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
								.build();
		
		userAccessToken = authenticateClient(webClient, "test", "testuser", "1234");
	}
	
	static AccessToken authenticateClient(WebClient webClient, String clientId, String username, String password) {
		
		return webClient
				.post()
				.body(
						BodyInserters.fromFormData("grant_type", "password")
								.with("client_id", clientId)
								.with("username", username)
								.with("password", password)
					)
				.retrieve()
				.bodyToMono(AccessToken.class)
				.block();
	}

	
	
	@Test
	void whenGetRequestThenInvoiceReturned() {
		
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
	
	
	private record AccessToken(String token) {
		
		@JsonCreator
		private AccessToken(@JsonProperty("access_token") final String token){
			this.token = token;
		}
	}
		
}
