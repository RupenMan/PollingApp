package edu.mum.polls.payloads;

public class UserIdAvailability {
	private Boolean available;

	public UserIdAvailability(Boolean available) {
		this.available = available;
	}

	public Boolean getAvailable() {
		return available;
	}

	public void setAvailable(Boolean available) {
		this.available = available;
	}
}
