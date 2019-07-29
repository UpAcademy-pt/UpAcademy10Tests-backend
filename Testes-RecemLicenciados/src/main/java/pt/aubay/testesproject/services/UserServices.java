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

import pt.aubay.testesproject.auxiliary.TempUser;
import pt.aubay.testesproject.business.UserBusiness;

@Transactional
@Path("user")
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
		//return userBusiness.healthCheck(context);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response addUser(TempUser user) {
		return userBusiness.add(user);
	}
	
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUser(TempUser user) {
		return userBusiness.get(user);
	}
	
	@GET
	@Path("all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllUsers() {
		return userBusiness.getAllUsers();
	}
}
