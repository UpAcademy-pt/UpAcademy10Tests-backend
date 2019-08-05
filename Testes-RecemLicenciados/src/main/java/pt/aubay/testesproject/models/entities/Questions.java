package pt.aubay.testesproject.models.entities;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({
	 @NamedQuery(name="Questions.getQuestions", query="SELECT q FROM Questions q WHERE q.id=:id"),
	 @NamedQuery(name="Questions.getAll",query="SELECT q FROM Questions q"),
	 @NamedQuery(name="Questions.count", query = "SELECT COUNT(q.id) FROM Questions q"),
	 @NamedQuery(name="Questions.checkIfExists", query = "SELECT COUNT(q.id) FROM Questions q WHERE q.question =:question"),
	 @NamedQuery(name="Questions.checkIfIdExists", query = "SELECT COUNT(q.id) FROM Questions q WHERE q.id =:id")
})
public class Questions extends Models{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String question;
	
	@ManyToOne
	private Category category;
	
	private String[] options;
	private int[] solution;
	
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}
	public String[] getOptions() {
		return options;
	}
	public void setOptions(String[] options) {
		this.options = options;
	}
	public int[] getSolution() {
		return solution;
	}
	public void setSolution(int[] solution) {
		this.solution = solution;
	}

}
