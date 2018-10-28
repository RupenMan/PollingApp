package edu.mum.polls.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import edu.mum.polls.audit.UserDateAudit;

@Entity
@Table(name="polls")
public class Poll extends UserDateAudit{

	private static final long serialVersionUID = 5284548407814040148L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@NotBlank
	@Size(max=140)
	private String question;
	
	@OneToMany(mappedBy="poll",cascade = CascadeType.ALL, fetch= FetchType.EAGER, orphanRemoval = true)
	@Size(min =2, max = 6)
	@Fetch(FetchMode.SELECT)
	@BatchSize(size = 30)
	private List<Choice> choices = new ArrayList<>();
	
	@NotNull
	private Instant expirationDate;

	public Long getId() {
		return id;
	}

	public String getQuestion() {
		return question;
	}

	public Instant getExpirationDate() {
		return expirationDate;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public void setExpirationDate(Instant expirationDate) {
		this.expirationDate = expirationDate;
	}
	
	public void addChoice(Choice choice) {
		choices.add(choice);
		choice.setPoll(this);
	}
	
	public void removeChoice(Choice choice) {
		choices.remove(choice);
		choice.setPoll(null);
	}

	public List<Choice> getChoices() {
		return choices;
	}

	public void setChoices(List<Choice> choices) {
		this.choices = choices;
	}
}
