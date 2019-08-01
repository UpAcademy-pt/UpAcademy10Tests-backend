package pt.aubay.testesproject.models.entities;

import java.util.Date;
import java.util.ArrayList;
import pt.aubay.testesproject.models.entities.Questions;


public class Test extends Models{
	private ArrayList <Questions> questions;
	private String author;
	private Date date;
	private int timer;
	private int averageScore;
	private String availability;
	
	public Test() {
	}
	
	public ArrayList<Questions> getQuestions() {
		return questions;
	}

	public void setQuestions(ArrayList<Questions> questions) {
		this.questions = questions;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
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

	public String getAvailability() {
		return availability;
	}

	public void setAvailability(String availability) {
		this.availability = availability;
	}
	
}
