package com.example.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfig {
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
//		config.setAllowCredentials(true);
//		config.addAllowedOrigin("http://localhost:4200");
		config.setAllowCredentials(false);
		config.addAllowedOrigin("*");
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}

	@Value("${jwt.key.public}")
	private String SECRET;

	@Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .csrf((csrf) -> csrf.disable())
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterAfter(new JWTAuthorizationFilter(SECRET), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(requests -> requests
                    .requestMatchers(HttpMethod.GET, "/*.*", "/").permitAll()
                    .requestMatchers("/error").permitAll()
                    .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                    .requestMatchers("/actuator/**").permitAll()
                    .requestMatchers("/login/**").permitAll()
                    .requestMatchers("/signature/**").permitAll()
                    .requestMatchers("/hmac/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/solo-admin").hasRole("ADMINISTRADORES")
                    .anyRequest().authenticated()
                 ).exceptionHandling((exceptions) -> exceptions
 						.authenticationEntryPoint(new AuthenticationEntryPoint() {
							@Override
							public void commence(HttpServletRequest request, HttpServletResponse response,
									AuthenticationException authException) throws IOException, ServletException {
								response.setHeader("WWW-Authenticate", "Bearer realm=\"MicroserviciosJWT\"");								
								response.setHeader("content-type", "application/problem+json; charset=utf-8");
								response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
								response.getWriter()
										.print("{\n  \"type\": \"https://datatracker.ietf.org/doc/html/rfc7235#section-3.1\","
												+ "\n  \"title\": \"Unauthorized\",\n  \"status\": 401,\n  \"detail\": \""
												+ authException.getMessage() + "\"\n}");
//								response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
							}
						})
 						.accessDeniedHandler(new AccessDeniedHandler() {
							@Override
							public void handle(HttpServletRequest request, HttpServletResponse response,
									AccessDeniedException accessDeniedException) throws IOException, ServletException {
								response.setHeader("WWW-Authenticate", "Bearer realm=\"MicroserviciosJWT\", error=\"insufficient_scope\", error_description=\"Requires higher privileges\"");								
								response.setHeader("content-type", "application/problem+json; charset=utf-8");
								response.setStatus(HttpServletResponse.SC_FORBIDDEN);
								response.getWriter()
										.print("{\n  \"type\": \"https://datatracker.ietf.org/doc/html/rfc7231#section-6.5.3\","
												+ "\n  \"title\": \"Forbidden\",\n  \"status\": 403,\n  \"detail\": \"" 
												+ accessDeniedException.getMessage() +"\"\n}");
//								response.sendError(HttpServletResponse.SC_FORBIDDEN, accessDeniedException.getMessage());
							}
						})
 				)
                .build();
	}

}
