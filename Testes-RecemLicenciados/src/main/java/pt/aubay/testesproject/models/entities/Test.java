package pt.aubay.testesproject.models.entities;

import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;

import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@NamedQueries({
	 @NamedQuery(name="Test.getAll",query="SELECT t FROM Test t"),
	 @NamedQuery(name="Test.count", query = "SELECT COUNT(t.id) FROM Test t"),
	 @NamedQuery(name="Test.checkIfExists", query = "SELECT COUNT(t.id) FROM Test t WHERE t.id =:id"),
	 @NamedQuery(name="Test.checkIfTestNameExists", query = "SELECT COUNT(t.id) FROM Test t WHERE t.testName =:testName"),
	 @NamedQuery(name="Test.getTest", query="SELECT t FROM Test t WHERE t.id=:id"),
	 @NamedQuery(name="Test.checkQuestion", query = "SELECT COUNT(t.id) FROM Test t LEFT JOIN t.questions q WHERE q.id =:questionID")
})

public class Test extends Models{

	private static final long serialVersionUID = 1L;
	
	//@Column(length=100000)
	@ManyToMany(cascade = {/*CascadeType.ALL*/CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH}, fetch = FetchType.EAGER)
	@JsonIgnoreProperties("test")
	private Set <Questions> questions;
	
	@ManyToOne
	RegisteredUser author;
	
	//private LocalDate date; //auto
	private LocalDateTime dateTime;//auto
	private int timer;
	private int averageScore; //calculated
	private String testName;//must be unique
	private long submittedTests;
	
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

//	public LocalDate getDate() {
//		return date;
//	}
//
//	public void setDate(LocalDate date) {
//		this.date = date;
//	}

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

	public LocalDateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}

	public long getSubmittedTests() {
		return submittedTests;
	}

	public void setSubmittedTests(long submittedTests) {
		this.submittedTests = submittedTests;
	}
	
}
