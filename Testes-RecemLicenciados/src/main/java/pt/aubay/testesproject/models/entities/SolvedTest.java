package pt.aubay.testesproject.models.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

@Entity
@NamedQueries({
	 @NamedQuery(name="SolvedTest.getSolvedTest", query="SELECT s FROM SolvedTest s WHERE s.id=:id"),
	 @NamedQuery(name="SolvedTest.getAll",query="SELECT s FROM SolvedTest s"),
	 @NamedQuery(name="SolvedTest.count", query = "SELECT COUNT(s.id) FROM SolvedTest s"),
	 @NamedQuery(name="SolvedTest.checkIfExists", query = "SELECT COUNT(s.id) FROM SolvedTest s WHERE s.id =:id"),
})

public class SolvedTest extends Models{
	private static final long serialVersionUID = 1L;
	
	//@OneToMany(cascade = { CascadeType.ALL })
	private List<Answer> answer;
	private Date timeSpent;
	
	@ManyToOne
	Candidate candidate;
	
	private int score;
	
	@ManyToOne
	Test test;
	
	private Date date;
	
	public List<Answer> getAnswer() {
		return answer;
	}
	public void setAnswer(List<Answer> answer) {
		this.answer = answer;
	}
	public Date getTimeSpent() {
		return timeSpent;
	}
	public void setTimeSpent(Date timeSpent) {
		this.timeSpent = timeSpent;
	}
	public Candidate getCandidate() {
		return candidate;
	}
	public void setUser(Candidate candidate) {
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
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
}
