package edu.mum.polls.payloads;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

public class PollResponse {
	private Long id;
	private String question;
	private List<ChoiceResponse> choices;
	private UserSummary createdBy;
	private Instant creationDateTime;
	private Instant expirationDateTime;
	private Boolean isExpired;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Long selectedChoice;
	
	private Long totalVotes;

	public Long getId() {
		return id;
	}

	public String getQuestion() {
		return question;
	}

	public List<ChoiceResponse> getChoices() {
		return choices;
	}

	public UserSummary getCreatedBy() {
		return createdBy;
	}

	public Instant getCreationDateTime() {
		return creationDateTime;
	}

	public Instant getExpirationDateTime() {
		return expirationDateTime;
	}

	public Boolean getIsExpired() {
		return isExpired;
	}

	public Long getSelectedChoice() {
		return selectedChoice;
	}

	public Long getTotalVotes() {
		return totalVotes;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public void setChoices(List<ChoiceResponse> choices) {
		this.choices = choices;
	}

	public void setCreatedBy(UserSummary createdBy) {
		this.createdBy = createdBy;
	}

	public void setCreationDateTime(Instant creationDateTime) {
		this.creationDateTime = creationDateTime;
	}

	public void setExpirationDateTime(Instant expirationDateTime) {
		this.expirationDateTime = expirationDateTime;
	}

	public void setIsExpired(Boolean isExpired) {
		this.isExpired = isExpired;
	}

	public void setSelectedChoice(Long selectedChoice) {
		this.selectedChoice = selectedChoice;
	}

	public void setTotalVotes(Long totalVotes) {
		this.totalVotes = totalVotes;
	}
}
