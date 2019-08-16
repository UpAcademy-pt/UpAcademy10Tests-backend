package pt.aubay.testesproject.business;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import pt.aubay.testesproject.models.dto.AllCategoriesDTO;
import pt.aubay.testesproject.models.entities.Category;
import pt.aubay.testesproject.repositories.CategoryRepository;
import pt.aubay.testesproject.repositories.QuestionRepository;

public class CategoryBusiness {
	@Inject
	CategoryRepository categoryRepository;
	
	@Inject
	QuestionRepository questionRepository;

	public Response add(Category category){
		if(categoryRepository.categoryExists(category))
			return Response.status(Status.FORBIDDEN).entity("This category exists already").build();
		categoryRepository.addEntity(category);
		return Response.ok().entity("Success").build();
	}
	
	public Response getAll() {
		List<Category> allCategories=categoryRepository.getAll();
		List<AllCategoriesDTO> allCategoriesDTO=new ArrayList<AllCategoriesDTO>();
		for(Category category : allCategories)
			allCategoriesDTO.add(convertEntityToDTO(category));
		return Response.ok(allCategoriesDTO, MediaType.APPLICATION_JSON).build();
	}
	
	public Response edit(Category newCategory) {
		//Verifies if category exists with specified id
		if(!categoryRepository.idExists(newCategory))
			return Response.status(Status.NOT_FOUND).entity("No such id in database").build();
		
		//If so, gets the old category to verify if a change was made in the category specification
		Category oldCategory=categoryRepository.getEntity(newCategory.getId());
		if(!oldCategory.getCategory().equals(newCategory.getCategory()) && categoryRepository.categoryExists(newCategory))
				return Response.status(Status.FORBIDDEN).entity("Cannot change to already existing category").build();
		categoryRepository.editEntity(newCategory);
		return Response.ok().entity("Success").build();
	}
	
	public Response remove(long id) {
	if(!categoryRepository.idExists(id))
		return Response.status(Status.NOT_FOUND).entity("No such id in database").build();
	
	//We should not be able to delete a category used in a question already
	if(questionRepository.categoryExists(id))
		return Response.status(Status.FORBIDDEN).entity("Cannot delete category used in question.").build();
	categoryRepository.deleteEntity(id);
	return Response.ok().entity("Success").build();
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////Auxiliary DTO Conversion//////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public AllCategoriesDTO convertEntityToDTO(Category category){
		AllCategoriesDTO allCategoriesDTO= new AllCategoriesDTO();
		allCategoriesDTO.setCategory(category);
		allCategoriesDTO.setNumberOfQuestions(questionRepository.count(category.getCategory()));
		return allCategoriesDTO;
	}
	
}
