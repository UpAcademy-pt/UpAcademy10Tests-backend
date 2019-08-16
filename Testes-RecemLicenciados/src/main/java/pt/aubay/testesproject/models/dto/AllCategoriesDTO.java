package pt.aubay.testesproject.models.dto;

import java.util.List;

import javax.inject.Inject;

import pt.aubay.testesproject.models.entities.Category;
import pt.aubay.testesproject.repositories.QuestionRepository;
import pt.aubay.testesproject.repositories.TestRepository;

public class AllCategoriesDTO {
	
	@Inject
	QuestionRepository questionRepository;
	
	private Category category;
	private Long numberOfQuestions;
	
//	public AllCategoriesDTO(Category category){
//		this.category=category;
//		//this.numberOfQuestions=questionRepository.count("JAVA");
//	}
	
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}
	public Long getNumberOfQuestions() {
		return numberOfQuestions;
	}
	public void setNumberOfQuestions(Long numberOfQuestions) {
		this.numberOfQuestions = numberOfQuestions;
	}
	
}
