package edu.mum.polls.payloads;

import java.time.Instant;

public class UserProfile {
	private Long id;
	private String username;
	private String name;
	private Instant joinedAt;
	private Long pollCount;
	private Long voteCount;
	
	public UserProfile(Long id, String username, String name, Instant joinedAt, Long pollCount, Long voteCount) {
		super();
		this.id = id;
		this.username = username;
		this.name = name;
		this.joinedAt = joinedAt;
		this.pollCount = pollCount;
		this.voteCount = voteCount;
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
	public Instant getJoinedAt() {
		return joinedAt;
	}
	public Long getPollCount() {
		return pollCount;
	}
	public Long getVoteCount() {
		return voteCount;
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
	public void setJoinedAt(Instant joinedAt) {
		this.joinedAt = joinedAt;
	}
	public void setPollCount(Long pollCount) {
		this.pollCount = pollCount;
	}
	public void setVoteCount(Long voteCount) {
		this.voteCount = voteCount;
	}
}
