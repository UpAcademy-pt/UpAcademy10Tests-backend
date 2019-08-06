package pt.aubay.testesproject.models.dto;

import java.time.LocalDateTime;
import java.util.List;

import pt.aubay.testesproject.models.entities.Answer;
import pt.aubay.testesproject.models.entities.Candidate;

public class SolvedTestDTO {
	private List<Answer> answer;
	private LocalDateTime timeSpent;
	private Candidate candidate;
	private int score;
	private long testID;
	private LocalDateTime date;
	public List<Answer> getAnswer() {
		return answer;
	}
	public void setAnswer(List<Answer> answer) {
		this.answer = answer;
	}
	public LocalDateTime getTimeSpent() {
		return timeSpent;
	}
	public void setTimeSpent(LocalDateTime timeSpent) {
		this.timeSpent = timeSpent;
	}
	public Candidate getCandidate() {
		return candidate;
	}
	public void setCandidate(Candidate candidate) {
		this.candidate = candidate;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public long getTestID() {
		return testID;
	}
	public void setTestID(long testID) {
		this.testID = testID;
	}
	public LocalDateTime getDate() {
		return date;
	}
	public void setDate(LocalDateTime date) {
		this.date = date;
	}
	
}
