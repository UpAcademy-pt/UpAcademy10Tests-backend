package pt.aubay.testesproject.services;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
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

import pt.aubay.testesproject.business.RegisteredUserBusiness;
import pt.aubay.testesproject.models.dto.RegisteredUserDTO;
import pt.aubay.testesproject.repositories.RegisteredUserRepository;

@Transactional
@Path("user")
public class RegisteredUserServices {
	@Inject
	protected RegisteredUserBusiness userBusiness;
	
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
	public Response addUser(RegisteredUserDTO user) {
		return userBusiness.add(user);
	}
	
	@GET
	@Path("login/{username}/{password}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUser(@PathParam("username") String usernameOrEmail, @PathParam("password") String password) {
		return userBusiness.get(usernameOrEmail, password);
	}
	
	@GET
	@Path("all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllUsers() {
		return userBusiness.getAllUsers();
	}
	
	@PUT
	@Path("changePassword/{username}/{oldPassword}/{newPassword}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response changePassword(@PathParam("username") String username,@PathParam("oldPassword") String oldPassword, @PathParam("newPassword") String newPassword) {
		return userBusiness.changePassword(username, oldPassword,newPassword);
	}
	
}
