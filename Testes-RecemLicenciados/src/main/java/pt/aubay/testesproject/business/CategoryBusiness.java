package pt.aubay.testesproject.business;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import pt.aubay.testesproject.models.entities.Category;
import pt.aubay.testesproject.repositories.CategoryRepository;

public class CategoryBusiness {
	@Inject
	CategoryRepository categoryRepository;

	public Response add(Category category){
		if(categoryRepository.categoryExists(category))
			return Response.status(Status.FORBIDDEN).entity("This category exists already").build();
		categoryRepository.addEntity(category);
		return Response.ok().entity("Success").build();
	}
	
	public Response getAll() {
		return Response.ok(categoryRepository.getAll(), MediaType.APPLICATION_JSON).build();
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
	categoryRepository.deleteEntity(id);
	return Response.ok().entity("Success").build();
	}
	
}
