package pt.aubay.testesproject.services;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import pt.aubay.testesproject.credentials.UserCredentials;
import pt.aubay.testesproject.business.UserBusiness;

@Transactional
@Path("usertest")
public class UserServices {

	@Inject
	protected UserBusiness userBusiness;
	
	@Context
	protected UriInfo context;
	
	@GET
	@Path("Status")
	@Produces (MediaType.TEXT_PLAIN)
	public String healthCheck() {
		return "URI " + context.getRequestUri().toString() + " is OK!";
	}
	
	@POST
	@Path("adduser")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response addUser(UserCredentials user) {
		return userBusiness.add(user);
	}
	
	@GET
	@Path("login/{username}/{password}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUser(@PathParam("username") String username, @PathParam("password") String password) {
		UserCredentials user = new UserCredentials(username, password);
		return userBusiness.get(user);
	}
	
	@GET
	@Path("all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllUsers() {
		return userBusiness.getAllUsers();
	}
}
