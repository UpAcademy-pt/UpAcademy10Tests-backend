package pt.aubay.testesproject.models.entities;

import java.util.ArrayList;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({
	 @NamedQuery(name="SolvedTest.getSolvedTest", query="SELECT s FROM SolvedTest s WHERE s.id=:id"),
	 @NamedQuery(name="SolvedTest.getAll",query="SELECT s FROM SolvedTest s"),
	 @NamedQuery(name="SolvedTest.count", query = "SELECT COUNT(s.id) FROM SolvedTest s"),
	 @NamedQuery(name="SolvedTest.checkIfExists", query = "SELECT COUNT(s.id) FROM SolvedTest s WHERE s.id =:id"),
})

public class SolvedTest extends Models{
	private static final long serialVersionUID = 1L;
	
	private ArrayList<Answer> answer;
	private Date timeSpent;
	private TestUser user;
	private int score;
	private long testID;
	
	public ArrayList<Answer> getAnswer() {
		return answer;
	}
	public void setAnswer(ArrayList<Answer> answer) {
		this.answer = answer;
	}
	public Date getTimeSpent() {
		return timeSpent;
	}
	public void setTimeSpent(Date timeSpent) {
		this.timeSpent = timeSpent;
	}
	public TestUser getUser() {
		return user;
	}
	public void setUser(TestUser user) {
		this.user = user;
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
}
