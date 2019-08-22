package pt.aubay.testesproject.services;

import javax.inject.Inject;
import javax.transaction.Transactional;
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
import pt.aubay.testesproject.execptionHandling.AppException;
import pt.aubay.testesproject.models.entities.Category;


@Transactional
@Path("category")
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
	
	@POST
	@Path("add")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response addCategory(Category category) throws AppException {
		categoryBusiness.add(category);
		return Response.ok().entity("Success").build();
	}
	
	//There is no need for a get category for it owns solely one field (category) apart from own id;
	
	@GET
	@Path("all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllCategories() {
		return Response.ok(categoryBusiness.getAll(), MediaType.APPLICATION_JSON).build();
	}
	
	@PUT
	@Path("edit")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response editCategory(Category category) throws AppException {
		categoryBusiness.edit(category);
		return Response.ok().entity("Success").build();
	}
	
	@DELETE
	@Path("remove/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces (MediaType.TEXT_PLAIN)
	public Response deleteCategory(@PathParam("id") long id) throws AppException {
		categoryBusiness.remove(id);
		return Response.ok().entity("Success").build();
	}
}
