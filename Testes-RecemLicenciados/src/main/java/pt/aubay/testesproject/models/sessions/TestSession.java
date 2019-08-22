package pt.aubay.testesproject.models.sessions;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import pt.aubay.testesproject.models.dto.TestDTO;
import pt.aubay.testesproject.models.entities.Models;
import pt.aubay.testesproject.models.entities.Test;

@Entity
@NamedQueries({
	 @NamedQuery(name="TestSession.checkIfExists", query = "SELECT COUNT(s.id) FROM TestSession s WHERE s.id =:id"),
	 @NamedQuery(name="TestSession.checkIfTestExists", query = "SELECT COUNT(s.id) FROM TestSession s INNER JOIN s.test t WHERE s.test.id=:testID"),
	 @NamedQuery(name="TestSession.getTestIDs", query = "SELECT s.id FROM TestSession s INNER JOIN s.test t WHERE s.test.id=:testID"),
	 @NamedQuery(name="TestSession.checkIfSessionExistsWithTest", query = "SELECT COUNT(s.id) FROM TestSession s INNER JOIN s.test t WHERE s.test.id=:testID AND s.id=:id"),
})
public class TestSession extends Models{

	private static final long serialVersionUID = 1L;
	
	@ManyToOne
	private Test test;
	private String recruiterEmail;
	private String candidateEmail;
	
	//to check validity of starting session
	private LocalDateTime startingToken;
	
	//to check validity of test submission
	private LocalDateTime startingTest;
	
	//number of hours of validity
	private long numberOfHours;
	

	public Test getTest() {
		return test;
	}
	public void setTest(Test test) {
		this.test = test;
	}
	public String getRecruiterEmail() {
		return recruiterEmail;
	}
	public void setRecruiterEmail(String recruiterEmail) {
		this.recruiterEmail = recruiterEmail;
	}

	public long getNumberOfHours() {
		return numberOfHours;
	}
	public void setNumberOfHours(long numberOfHours) {
		this.numberOfHours = numberOfHours;
	}
	public LocalDateTime getStartingToken() {
		return startingToken;
	}
	public void setStartingToken(LocalDateTime startingToken) {
		this.startingToken = startingToken;
	}
	public LocalDateTime getStartingTest() {
		return startingTest;
	}
	public void setStartingTest(LocalDateTime startingTest) {
		this.startingTest = startingTest;
	}
	@Override
	public String toString() {
		return "TestSession [test=" + test + ", recruiterEmail=" + recruiterEmail + ", startingToken=" + startingToken
				+ ", startingTest=" + startingTest + ", numberOfHours=" + numberOfHours + ", id=" + id + "]";
	}
	public String getCandidateEmail() {
		return candidateEmail;
	}
	public void setCandidateEmail(String candidateEmail) {
		this.candidateEmail = candidateEmail;
	}
	
	
}
