package edu.mum.polls.payloads;

public class UserSummary {
	private Long id;
	private String username;
	private String name;
	
	public UserSummary(Long id, String username, String name) {
		super();
		this.id = id;
		this.username = username;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public String getName() {
		return name;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setName(String name) {
		this.name = name;
	}
}
