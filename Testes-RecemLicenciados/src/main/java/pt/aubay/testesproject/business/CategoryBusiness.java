package pt.aubay.testesproject.business;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import pt.aubay.testesproject.models.entities.Category;
import pt.aubay.testesproject.models.statistics.CategoryStatistics;
import pt.aubay.testesproject.repositories.CategoryRepository;
import pt.aubay.testesproject.repositories.QuestionRepository;

public class CategoryBusiness {
	@Inject
	private CategoryRepository categoryRepository;

	@Inject
	QuestionRepository questionRepository;

	@Transactional
	public void add(Category category) {
		if(categoryRepository.categoryExists(category))
			throw new BadRequestException("This category already exists");
		categoryRepository.addEntity(category);
	}

	public List<CategoryStatistics> getAll() {
		List<Category> allCategories=categoryRepository.getAll();
		List<CategoryStatistics> allCategoriesDTO=new ArrayList<CategoryStatistics>();
		for(Category category : allCategories)
			allCategoriesDTO.add(convertEntityToStatistics(category));
		return allCategoriesDTO;
	}

	@Transactional
	public void edit(Category newCategory) {
		//Verifies if category exists with specified id
		if(!categoryRepository.idExists(newCategory))
			throw new NotFoundException("No such id in database");

		//If so, gets the old category to verify if a change was made in the category specification
		Category oldCategory=categoryRepository.getEntity(newCategory.getId());
		if(!oldCategory.getCategory().equals(newCategory.getCategory()) && categoryRepository.categoryExists(newCategory))
			throw new BadRequestException("Cannot change to already existing category");
		categoryRepository.editEntity(newCategory);
	}

	@Transactional
	public void remove(long id) {
		if(!categoryRepository.idExists(id))
			throw new NotFoundException("No such id in database");

		//We should not be able to delete a category used in a question already
		if(questionRepository.categoryExists(id))
			throw new BadRequestException("Cannot delete category used in question.");
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
