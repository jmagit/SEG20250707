package com.example.security;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTAuthorizationFilter extends OncePerRequestFilter {
	private final Log log = LogFactory.getLog(getClass());
	private static final String HEADER = "Authorization";
	private static final String PREFIX = "Bearer ";
	private final Algorithm algorithm;

	public JWTAuthorizationFilter(String secret) throws NoSuchAlgorithmException, InvalidKeySpecException {
		super();
		KeyFactory kf = KeyFactory.getInstance("RSA");
		X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(secret));
		RSAPublicKey publicKey = (RSAPublicKey) kf.generatePublic(keySpecX509);
		algorithm = Algorithm.RSA256(publicKey, null);
	}

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain)
			throws ServletException, IOException {
		try {
			String authenticationHeader = request.getHeader(HEADER);
			if (authenticationHeader != null && authenticationHeader.startsWith(PREFIX)) {
				DecodedJWT token = JWT.require(algorithm).withIssuer("MicroserviciosJWT").withAudience("authorization")
						.build().verify(authenticationHeader.substring(PREFIX.length()));
				List<GrantedAuthority> authorities = token.getClaim("roles").asList(String.class).stream()
						.map(role -> (GrantedAuthority) (new SimpleGrantedAuthority("ROLE_" + role.toUpperCase())))
						.toList();
				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(token.getSubject(),
						null, authorities);
				SecurityContextHolder.getContext().setAuthentication(auth);
			}
			chain.doFilter(request, response);
		} catch (JWTVerificationException ex) {
			SecurityContextHolder.clearContext();
			response.setHeader("WWW-Authenticate",
					"Bearer realm=\"MicroserviciosJWT\", error=\"invalid_token\", error_description=\""
							+ ex.getMessage() + "\"");
			response.setHeader("content-type", "application/problem+json; charset=utf-8");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter()
					.print("{\n  \"type\": \"https://datatracker.ietf.org/doc/html/rfc7235#section-3.1\","
							+ "\n  \"title\": \"Unauthorized\",\n  \"status\": 401," + "\n  \"detail\": \""
							+ ex.getMessage() + "\"\n}");
			log.error("JWTVerificationException: " + ex.getMessage());
			ex.printStackTrace();
//			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());
		}
	}
}
