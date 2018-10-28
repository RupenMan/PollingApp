package edu.mum.polls.model;

public class ChoiceVoteCount {
	private Long choiceId;
	private Long voteCount;
	
	public ChoiceVoteCount(Long choiceId, Long voteCount) {
		super();
		this.choiceId = choiceId;
		this.voteCount = voteCount;
	}

	public Long getChoiceId() {
		return choiceId;
	}

	public Long getVoteCount() {
		return voteCount;
	}

	public void setChoiceId(Long choiceId) {
		this.choiceId = choiceId;
	}

	public void setVoteCount(Long voteCount) {
		this.voteCount = voteCount;
	}
}
