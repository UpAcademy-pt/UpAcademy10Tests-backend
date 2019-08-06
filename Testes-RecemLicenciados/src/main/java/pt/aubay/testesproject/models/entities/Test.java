package pt.aubay.testesproject.models.entities;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;
import java.util.ArrayList;
import pt.aubay.testesproject.models.entities.Questions;

@Entity
@NamedQueries({
	 @NamedQuery(name="Test.getAll",query="SELECT t FROM Test t"),
	 @NamedQuery(name="Test.count", query = "SELECT COUNT(t.id) FROM Test t"),
	 @NamedQuery(name="Test.checkIfExists", query = "SELECT COUNT(t.id) FROM Test t WHERE t.id =:id"),
	 @NamedQuery(name="Test.checkIfTestNameExists", query = "SELECT COUNT(t.id) FROM Test t WHERE t.testName =:testName"),
	 @NamedQuery(name="Test.getTest", query="SELECT t FROM Test t WHERE t.id=:id")
})

public class Test extends Models{

	private static final long serialVersionUID = 1L;
	
	//@Column(length=100000)
	@ManyToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@JoinTable(
	        name = "test_question", 
	        joinColumns = { @JoinColumn(name = "test_id") }, 
	        inverseJoinColumns = { @JoinColumn(name = "question_id") }
	    )
	@JsonIgnoreProperties("test")
	private Set <Questions> questions;
	
	@ManyToOne
	RegisteredUser author;
	
	private LocalDate date; //auto
	private int timer;
	private int averageScore; //calculated
	private String testName;//must be unique
	
	public Test() {
	}
	
	public Set<Questions> getQuestions() {
		return questions;
	}

	public void setQuestions(Set<Questions> questions) {
		this.questions = questions;
	}

	public RegisteredUser getAuthor() {
		return author;
	}

	public void setAuthor(RegisteredUser author) {
		this.author = author;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
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

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}
	
}
