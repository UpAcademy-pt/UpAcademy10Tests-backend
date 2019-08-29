package pt.aubay.testesproject.services;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import pt.aubay.testesproject.business.CategoryBusiness;
import pt.aubay.testesproject.models.entities.Category;

@Path("categories")
public class CategoryServices {
	@Inject
	protected CategoryBusiness categoryBusiness;
	
	@Context
	protected UriInfo context;
	
	@GET
	@Path("status")
	@Produces (MediaType.TEXT_PLAIN)
	public String healthCheck() {
		return "URI " + context.getRequestUri().toString() + " is OK!";
	}
	
	/**
	 * @param category		a string specifying the category we want to add (ex: category="JAVA");
	 * @return				a response message
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	
	public Response addCategory(Category category) {
		categoryBusiness.add(category);
		return Response.ok().entity("Success").build();
	}
	
	
	/**
	 * @return				all categories with respective ID
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllCategories() {
		return Response.ok(categoryBusiness.getAll(), MediaType.APPLICATION_JSON).build();
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response editCategory(Category category) {
		categoryBusiness.edit(category);
		return Response.ok().entity("Success").build();
	}
	
	@DELETE
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces (MediaType.TEXT_PLAIN)
	public Response deleteCategory(@PathParam("id") long id) {
		categoryBusiness.remove(id);
		return Response.ok().entity("Success").build();
	}
}
