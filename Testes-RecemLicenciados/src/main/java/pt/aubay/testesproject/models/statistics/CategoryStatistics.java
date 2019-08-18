package pt.aubay.testesproject.models.statistics;

import java.util.List;

import javax.inject.Inject;

import pt.aubay.testesproject.models.entities.Category;
import pt.aubay.testesproject.repositories.QuestionRepository;
import pt.aubay.testesproject.repositories.TestRepository;

public class CategoryStatistics {
	
	@Inject
	QuestionRepository questionRepository;
	
	private Category category;
	private long numberOfQuestions;
	private long score;
	
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
	public long getNumberOfQuestions() {
		return numberOfQuestions;
	}
	public void setNumberOfQuestions(long numberOfQuestions) {
		this.numberOfQuestions = numberOfQuestions;
	}
	public long getScore() {
		return score;
	}
	public void setScore(long score) {
		this.score = score;
	}
	
}
