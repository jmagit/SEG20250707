package com.example.security.dtos;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@JsonInclude(value = Include.NON_EMPTY)
@Data @AllArgsConstructor @NoArgsConstructor
@Builder
public class AuthToken implements Serializable {
	private boolean success = false;
	@JsonProperty("access_token")
    private String accessToken;
	@JsonProperty("token_type")
    private String tokenType;
	@JsonProperty("refresh_token")
    private String refreshToken;
    private String name;
    private List<String> roles;
	@JsonProperty("expires_in")
    private int expiresIn;
}
