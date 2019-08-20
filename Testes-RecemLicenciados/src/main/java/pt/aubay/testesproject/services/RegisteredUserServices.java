package pt.aubay.testesproject.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import javax.ws.rs.QueryParam;

import pt.aubay.testesproject.business.RegisteredUserBusiness;
import pt.aubay.testesproject.models.dto.RegisteredUserDTO;
import pt.aubay.testesproject.models.entities.RegisteredUser;

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
	@Produces(MediaType.APPLICATION_JSON)
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
	
	@GET
	@Path("all/{currentID}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllUsers(@PathParam("currentID") long currentID) {
		return userBusiness.getAllUsers(currentID);
	}
	
	@PUT
	@Path("changePassword/{username}/{oldPassword}/{newPassword}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response changePassword(@PathParam("username") String username,@PathParam("oldPassword") String oldPassword, @PathParam("newPassword") String newPassword) {
		return userBusiness.changePassword(username, oldPassword,newPassword);
	}
	
	@PUT
	@Path("edit")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response editUser(RegisteredUserDTO user) {
		return userBusiness.edit(user);
	}
	
	@DELETE
	@Path("remove/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces (MediaType.TEXT_PLAIN)
	public Response deleteUser(@PathParam("id") long id) {
		return userBusiness.remove(id);
	}
	
	//temporary
	//replace the reset password with e-mail - to not forget
	@PUT
	@Path("resetPassword")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response resetPassword(RegisteredUserDTO user) {
		try {
			return userBusiness.resetPassword(user);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.status(Status.EXPECTATION_FAILED).entity("Some unknown issue occured").build();
	}
	
/*	@GET
	@Path("filter")
	@Produces({MediaType.APPLICATION_JSON})
	public List<RegisteredUser> getFilterRegisteredUsers(
			@NotNull @Min(0) @QueryParam("page") int page,
			@NotNull @Min(2) @Max(10) @QueryParam("pageSize") int pageSize) {
		return userBusiness.getFilterRegisteredUsers(page, pageSize);
	}
*/	
}
