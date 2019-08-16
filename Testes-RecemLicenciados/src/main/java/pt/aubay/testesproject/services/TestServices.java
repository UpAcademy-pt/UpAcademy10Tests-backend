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

import pt.aubay.testesproject.business.TestBusiness;
import pt.aubay.testesproject.models.dto.TestDTO;
import pt.aubay.testesproject.models.entities.Questions;
import pt.aubay.testesproject.models.entities.Test;

@Transactional
@Path("test")
public class TestServices {
	
	@Inject
	protected TestBusiness testBusiness;
	
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
	public Response addTest(TestDTO test) {
		return testBusiness.add(test);
	}
	
	@GET
	@Path("all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllTests() {
		return testBusiness.getAll();
	}
	
	@GET
	@Path("test/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTest(@PathParam("id") long id) {
		return testBusiness.get(id);
	}
	
	@PUT
	@Path("edit")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces (MediaType.TEXT_PLAIN)
	public Response editTest(TestDTO test) {
		return testBusiness.edit(test);
	}
	
	@DELETE
	@Path("remove/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces (MediaType.TEXT_PLAIN)
	public Response deleteTest(@PathParam("id") long id) {
		return testBusiness.remove(id);
	}
}
