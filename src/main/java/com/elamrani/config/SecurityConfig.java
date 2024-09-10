package com.elamrani.config;

import com.elamrani.security.JWTFilter;
import com.elamrani.services.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	private final JWTFilter jwtFilter;
	private final UserDetailsServiceImpl userDetailsServiceImpl;

	public SecurityConfig(JWTFilter jwtFilter, UserDetailsServiceImpl userDetailsServiceImpl) {
		this.jwtFilter = jwtFilter;
		this.userDetailsServiceImpl = userDetailsServiceImpl;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(csrf -> csrf.disable()) // Désactiver la protection CSRF pour une API stateless
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/api/register", "/api/login").permitAll()
						.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll() // Autoriser Swagger
						.requestMatchers(AppConstants.PUBLIC_URLS).permitAll()
						.requestMatchers(AppConstants.USER_URLS).hasAnyAuthority("USER", "ADMIN")
						.requestMatchers(AppConstants.ADMIN_URLS).hasAuthority("ADMIN")
						.anyRequest().authenticated()
				)
				.cors(cors -> cors.configurationSource(request -> {
					// Disable CORS by allowing all origins and headers
					org.springframework.web.cors.CorsConfiguration config = new org.springframework.web.cors.CorsConfiguration();
					config.setAllowedOrigins(Arrays.asList("*")); // Allow all origins
					config.setAllowedMethods(Arrays.asList("*")); // Allow all methods
					config.setAllowedHeaders(Arrays.asList("*")); // Allow all headers
					return config;
				}))
				.exceptionHandling(exceptions -> exceptions
						.authenticationEntryPoint((request, response, authException) ->
								response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"))
				)
				.sessionManagement(session -> session
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Utiliser les JWT pour la gestion des sessions
				)
				.authenticationProvider(daoAuthenticationProvider());

		// Ajouter le filtre JWT avant UsernamePasswordAuthenticationFilter
		http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build(); // Construire la chaîne de filtres
	}

	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsServiceImpl); // Fournir le service UserDetails
		provider.setPasswordEncoder(passwordEncoder()); // Encoder les mots de passe
		return provider;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(); // Utiliser BCrypt pour l'encodage des mots de passe
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager(); // Fournir l'AuthenticationManager
	}
}
