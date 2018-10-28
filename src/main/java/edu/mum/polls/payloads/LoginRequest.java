package edu.mum.polls.payloads;

import javax.validation.constraints.NotBlank;

public class LoginRequest {
	
	@NotBlank
	private String userNameOrEmail;
	
	@NotBlank 
	private String password;

	public String getUserNameOrEmail() {
		return userNameOrEmail;
	}

	public String getPassword() {
		return password;
	}

	public void setUserNameOrEmail(String userNameOrEmail) {
		this.userNameOrEmail = userNameOrEmail;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
