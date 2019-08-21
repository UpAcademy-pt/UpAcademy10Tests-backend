package pt.aubay.testesproject.models.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.JoinColumn;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import pt.aubay.testesproject.models.entities.Test;

@Entity
@NamedQueries({
	 @NamedQuery(name="Questions.getQuestions", query="SELECT q FROM Questions q WHERE q.id=:id"),
	 @NamedQuery(name="Questions.getAll",query="SELECT q FROM Questions q"),
	 @NamedQuery(name="Questions.count", query = "SELECT COUNT(q.id) FROM Questions q"),
	 @NamedQuery(name="Questions.checkIfExists", query = "SELECT COUNT(q.id) FROM Questions q WHERE q.question =:question"),
	 @NamedQuery(name="Questions.checkIfIdExists", query = "SELECT COUNT(q.id) FROM Questions q WHERE q.id =:id"),
	 @NamedQuery(name="Questions.checkCategory", query = "SELECT COUNT(q.id) FROM Questions q WHERE q.category.id =:categoryID"),
	 @NamedQuery(name="Questions.getAllQuestionIDsOfCategory", query = "SELECT q.id FROM Questions q WHERE q.category.category =:category"),
	 @NamedQuery(name="Questions.getRandomQuestionOfCategory", query = "SELECT q FROM Questions q WHERE q.id IN :ids"),
	 @NamedQuery(name="Questions.getQuestionsNumberOfCategory", query = "SELECT COUNT(q.id) FROM Questions q WHERE q.category.category=:category"),
	 @NamedQuery(name="Questions.checkTest", query = "SELECT COUNT(t) FROM Questions q INNER JOIN q.test t WHERE q.id=:questionID"),
})
public class Questions extends Models{

	private static final long serialVersionUID = 1L;
	
	@ManyToOne
	Category category;
	
	@ManyToMany(cascade = { CascadeType.ALL}, fetch = FetchType.EAGER, mappedBy="questions")
//	@JoinTable(
//	        name = "test_question", 
//	        inverseJoinColumns = { @JoinColumn(name = "test_id") }, 
//	        joinColumns  = { @JoinColumn(name = "question_id") }
//	    )
	@JsonIgnoreProperties("questions")
	Set <Test> test;
	
	private String question;
	
	@Column(length=1000)
	private String[] options;
	private int[] solution;
	

	public Set<Test> getTest() {
		return test;
	}
	public void setTest(Set<Test> test) {
		this.test = test;
	}

	
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
