package edu.mum.polls.payloads;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class SignUpRequest {
	
	@NotBlank
	@Size(min = 4, max=40)
	private String name;
	
	@NotBlank
	@Size(min =3, max=15)
	private String username;
	
	@NotBlank
	@Size(max=40)
	private String email;
	
	@NotBlank
	private String password;

	public String getName() {
		return name;
	}

	public String getUsername() {
		return username;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
