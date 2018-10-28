package edu.mum.polls.payloads;

public class ChoiceResponse {
	private Long id;
	private String text;
	private long voteCount;
	public Long getId() {
		return id;
	}
	public String getText() {
		return text;
	}
	public long getVoteCount() {
		return voteCount;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public void setText(String text) {
		this.text = text;
	}
	public void setVoteCount(long voteCount) {
		this.voteCount = voteCount;
	}
}
