package pt.aubay.testesproject.models.entities;

import java.util.ArrayList;
import java.util.Date;

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
