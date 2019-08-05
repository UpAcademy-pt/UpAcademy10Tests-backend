package pt.aubay.testesproject.models.dto;

import pt.aubay.testesproject.models.entities.Category;

public class QuestionDTO extends ModelsDTO{
	Category category;
	private String question;
	private String[] options;
	private int[] solution;
	
	
	
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
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
