package pt.aubay.testesproject.business;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import pt.aubay.testesproject.execptionHandling.AppException;
import pt.aubay.testesproject.models.entities.Category;
import pt.aubay.testesproject.models.statistics.CategoryStatistics;
import pt.aubay.testesproject.repositories.CategoryRepository;
import pt.aubay.testesproject.repositories.QuestionRepository;

public class CategoryBusiness {
	@Inject
	CategoryRepository categoryRepository;
	
	@Inject
	QuestionRepository questionRepository;

	public void add(Category category) throws AppException{
		if(categoryRepository.categoryExists(category))
			throw new AppException("This category exists already", Status.FORBIDDEN.getStatusCode());
		categoryRepository.addEntity(category);
	}
	
	public List<CategoryStatistics> getAll() {
		List<Category> allCategories=categoryRepository.getAll();
		List<CategoryStatistics> allCategoriesDTO=new ArrayList<CategoryStatistics>();
		for(Category category : allCategories)
			allCategoriesDTO.add(convertEntityToStatistics(category));
		return allCategoriesDTO;
	}
	
	public void edit(Category newCategory) throws AppException {
		//Verifies if category exists with specified id
		if(!categoryRepository.idExists(newCategory))
			throw new AppException("No such id in database", Status.NOT_FOUND.getStatusCode());
		
		//If so, gets the old category to verify if a change was made in the category specification
		Category oldCategory=categoryRepository.getEntity(newCategory.getId());
		if(!oldCategory.getCategory().equals(newCategory.getCategory()) && categoryRepository.categoryExists(newCategory))
			throw new AppException("Cannot change to already existing category", Status.FORBIDDEN.getStatusCode());
		categoryRepository.editEntity(newCategory);
	}
	
	public void remove(long id) throws AppException {
	if(!categoryRepository.idExists(id))
		throw new AppException("No such id in database", Status.NOT_FOUND.getStatusCode());
	
	//We should not be able to delete a category used in a question already
	if(questionRepository.categoryExists(id))
		throw new AppException("Cannot delete category used in question.", Status.BAD_REQUEST.getStatusCode());
	categoryRepository.deleteEntity(id);
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////Auxiliary DTO Conversion//////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public CategoryStatistics convertEntityToStatistics(Category category){
		CategoryStatistics allCategoriesDTO= new CategoryStatistics();
		allCategoriesDTO.setCategory(category);
		allCategoriesDTO.setNumberOfQuestions(questionRepository.count(category.getCategory()));
		return allCategoriesDTO;
	}
	
}
