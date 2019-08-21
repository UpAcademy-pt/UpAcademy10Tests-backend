package pt.aubay.testesproject.models.entities;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
@NamedQueries({
	 @NamedQuery(name="SolvedTest.getSolvedTest", query="SELECT s FROM SolvedTest s WHERE s.id=:id"),
	 @NamedQuery(name="SolvedTest.getAll",query="SELECT s FROM SolvedTest s"),
	 @NamedQuery(name="SolvedTest.count", query = "SELECT COUNT(s.id) FROM SolvedTest s"),
	 @NamedQuery(name="SolvedTest.checkIfExists", query = "SELECT COUNT(s.id) FROM SolvedTest s WHERE s.id =:id"),
	 @NamedQuery(name="SolvedTest.checkUniqueness", query = "SELECT COUNT(s.id) FROM SolvedTest s INNER JOIN s.candidate INNER JOIN s.test WHERE s.candidate.id =:candidateID AND s.test.id=:testID"),
	 @NamedQuery(name="SolvedTest.checkIfTestExists", query = "SELECT COUNT(s.id) FROM SolvedTest s INNER JOIN s.test WHERE s.test.id=:testID")
	 
})

public class SolvedTest extends Models{
	private static final long serialVersionUID = 1L;

	//Bidirectionallity needed due to need for a List<Answer> answer -> that's what we will send to the front-end
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	List<Answer> answer;
	
	private long timeSpent;
	
	@ManyToOne(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	Candidate candidate;
	
	private int score;
	
	@ManyToOne
	Test test;
	
	private LocalDateTime date;
	
	public List<Answer> getAnswer() {
		return answer;
	}
	public void setAnswer(List<Answer> answer) {
		this.answer = answer;
	}
	public long getTimeSpent() {
		return timeSpent;
	}
	public void setTimeSpent(long timeSpent) {
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
	public Test getTest() {
		return test;
	}
	public void setTest(Test test) {
		this.test = test;
	}
	public LocalDateTime getDate() {
		return date;
	}
	public void setDate(LocalDateTime date) {
		this.date = date;
	}
}
