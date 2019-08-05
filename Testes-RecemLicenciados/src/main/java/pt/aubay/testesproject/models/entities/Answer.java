package pt.aubay.testesproject.models.entities;

import javax.persistence.Entity;
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
	private long questionId;
	private String[] givenAnswer;
	
	public Answer() {
		
	}

	public long getQuestionId() {
		return questionId;
	}

	public void setQuestionId(long questionId) {
		this.questionId = questionId;
	}

	public String[] getGivenAnswer() {
		return givenAnswer;
	}

	public void setGivenAnswer(String[] givenAnswer) {
		this.givenAnswer = givenAnswer;
	}
}
