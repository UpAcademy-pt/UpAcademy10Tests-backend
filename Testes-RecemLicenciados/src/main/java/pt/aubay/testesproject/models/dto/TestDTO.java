package pt.aubay.testesproject.models.dto;

import java.util.Set;

import pt.aubay.testesproject.models.entities.Questions;
import pt.aubay.testesproject.models.entities.RegisteredUser;

public class TestDTO extends ModelsDTO{
	
	
	private RegisteredUser author;
	private int timer;
	private int averageScore; //calculated
	private String testName;//must be unique
	//private String date;
	private String dateTime;
	private Set <QuestionDTO> questions;
	
	
//	public String getDate() {
//		return date;
//	}
//	public void setDate(String date) {
//		this.date = date;
//	}
	public String getDateTime() {
		return dateTime;
	}
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	public RegisteredUser getAuthor() {
		return author;
	}
	public void setAuthor(RegisteredUser author) {
		this.author = author;
	}
	public int getTimer() {
		return timer;
	}
	public void setTimer(int timer) {
		this.timer = timer;
	}
	public int getAverageScore() {
		return averageScore;
	}
	public void setAverageScore(int averageScore) {
		this.averageScore = averageScore;
	}
	public String getTestName() {
		return testName;
	}
	public void setTestName(String testName) {
		this.testName = testName;
	}
	public Set <QuestionDTO> getQuestions() {
		return questions;
	}
	public void setQuestions(Set <QuestionDTO> questions) {
		this.questions = questions;
	}
}
