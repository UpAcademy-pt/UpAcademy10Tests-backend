package pt.aubay.testesproject.models.entities;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({
	 @NamedQuery(name="Answer.getAnswer", query="SELECT a FROM Answer a WHERE a.id=:id"),
	 @NamedQuery(name="Answer.getAll",query="SELECT a FROM Answer a"),
	 @NamedQuery(name="Answer.count", query = "SELECT COUNT(a.id) FROM Answer a"),
	 @NamedQuery(name="Answer.checkIfExists", query = "SELECT COUNT(a.id) FROM Answer a WHERE a.id =:id"),
})


public class Answer extends Models{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@ManyToOne
	Questions question;
	private int[] givenAnswer;
	
	@ManyToOne
	SolvedTest test;
	
	public SolvedTest getTest() {
		return test;
	}

	public void setTest(SolvedTest test) {
		this.test = test;
	}

	public Answer() {
		
	}

	public Questions getQuestion() {
		return question;
	}

	public void setQuestion(Questions question) {
		this.question = question;
	}

	public int[] getGivenAnswer() {
		return givenAnswer;
	}

	public void setGivenAnswer(int[] givenAnswer) {
		this.givenAnswer = givenAnswer;
	}
}
