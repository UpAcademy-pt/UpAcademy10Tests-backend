package pt.aubay.testesproject.models.dto;

import java.time.LocalDateTime;
import java.util.List;

import pt.aubay.testesproject.models.entities.Candidate;

public class SolvedTestDTO extends ModelsDTO{
	private List<AnswerDTO> answer;
	private long timeSpent;//in ms;
	private CandidateDTO candidate;
	private int score;
	private long testID;
	private String date;
	public List<AnswerDTO> getAnswer() {
		return answer;
	}
	public void setAnswer(List<AnswerDTO> answer) {
		this.answer = answer;
	}
	public long getTimeSpent() {
		return timeSpent;
	}
	public void setTimeSpent(long timeSpent) {
		this.timeSpent = timeSpent;
	}
	public CandidateDTO getCandidate() {
		return candidate;
	}
	public void setCandidate(CandidateDTO candidate) {
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
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
}
