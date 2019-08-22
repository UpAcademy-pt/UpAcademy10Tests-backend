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
import pt.aubay.testesproject.execptionHandling.AppException;
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
	public Response addUser(RegisteredUserDTO user) throws AppException {
		userBusiness.add(user);
		return Response.ok().entity("Success").build();
	}
	
	@GET
	@Path("login/{username}/{password}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUser(@PathParam("username") String usernameOrEmail, @PathParam("password") String password) throws AppException {
		return Response.ok(userBusiness.get(usernameOrEmail, password), MediaType.APPLICATION_JSON).build();
	}
	
	@GET
	@Path("all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllUsers() {
		return Response.ok(userBusiness.getAllUsers(), MediaType.APPLICATION_JSON).build();
	}
	
	@GET
	@Path("all/{currentID}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllUsers(@PathParam("currentID") long currentID) throws AppException {
		return Response.ok(userBusiness.getAllUsers(currentID), MediaType.APPLICATION_JSON).build();
	}
	
	@PUT
	@Path("changePassword/{username}/{oldPassword}/{newPassword}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response changePassword(@PathParam("username") String username,@PathParam("oldPassword") String oldPassword, @PathParam("newPassword") String newPassword) throws AppException {
		userBusiness.changePassword(username, oldPassword,newPassword);
		return Response.ok().entity("Success").build();
	}
	
	@PUT
	@Path("edit")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response editUser(RegisteredUserDTO user) throws AppException {
		return Response.ok(userBusiness.edit(user), MediaType.APPLICATION_JSON).build();
	}
	
	@DELETE
	@Path("remove/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces (MediaType.TEXT_PLAIN)
	public Response deleteUser(@PathParam("id") long id) throws AppException {
		userBusiness.remove(id);
		return Response.ok().entity("Success").build();
	}
	
	//temporary
	//replace the reset password with e-mail - to not forget
	@PUT
	@Path("resetPassword")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response resetPassword(RegisteredUserDTO user) throws AppException {
		try {
			userBusiness.resetPassword(user);
			return Response.ok().entity("Success").build();
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
