package edu.mum.polls.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import edu.mum.polls.audit.DateAudit;

@Entity
@Table(name="votes",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = {"poll_id", "user_id"})
		})
public class Vote extends DateAudit{

	private static final long serialVersionUID = -5361680155494157489L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name="poll_id", nullable = false)
	private Poll poll;
	
	@ManyToOne(fetch=FetchType.LAZY, optional = false)
	@JoinColumn(name="choice_id", nullable = false)
	private Choice choice;
	
	@ManyToOne(fetch=FetchType.LAZY, optional = false)
	@JoinColumn(name="user_id", nullable = false)
	private User user;

	public Long getId() {
		return id;
	}

	public Poll getPoll() {
		return poll;
	}

	public Choice getChoice() {
		return choice;
	}

	public User getUser() {
		return user;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setPoll(Poll poll) {
		this.poll = poll;
	}

	public void setChoice(Choice choice) {
		this.choice = choice;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
