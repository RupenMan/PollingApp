package edu.mum.polls.payloads;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

public class PollLength {
	
	@NotNull
	@Max(7)
	private Integer days;
	
	@NotNull
	@Max(23)
	private Integer hours;

	public Integer getDays() {
		return days;
	}

	public Integer getHours() {
		return hours;
	}

	public void setDays(Integer days) {
		this.days = days;
	}

	public void setHours(Integer hours) {
		this.hours = hours;
	}
}
