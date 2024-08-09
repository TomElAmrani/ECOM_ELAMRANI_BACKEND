package com.elamrani.dtos;

import lombok.Data;

@Data
public class JWTAuthRequest {
	private String username;  // email
	private String password;
}