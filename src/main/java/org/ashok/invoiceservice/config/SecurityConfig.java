package org.ashok.invoiceservice.config;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		return http
				.authorizeHttpRequests(authorize -> 
							authorize.anyRequest()
							.hasAnyRole("user", "admin"))
				.oauth2ResourceServer(customizer -> customizer.jwt(Customizer.withDefaults()))
				.sessionManagement(customizer -> customizer.sessionCreationPolicy(STATELESS)) // each request will include an access token and no session
				.csrf(AbstractHttpConfigurer::disable) // no browser based direct client so csrf disabled 
				.build();
		
	}
	
	/**
	 * Custom strategy to extract role information from jwt access token and build GrantedAuthority object
	 * @see hasAnyRole(...) customization in filterChain method above. The converter makes it available from Jwt.
	 * @return
	 */
	@Bean
	public JwtAuthenticationConverter jwtAuthenticationConverter() {
		
		var jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
		jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
		jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
		
		var jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
		
		return jwtAuthenticationConverter;
		
	}
}
