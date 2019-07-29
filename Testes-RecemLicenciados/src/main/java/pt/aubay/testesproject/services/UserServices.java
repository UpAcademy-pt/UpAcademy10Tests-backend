package pt.aubay.testesproject.services;

import javax.inject.Inject;
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

public class UserServices {

	@Inject
	protected UserBusiness userBusiness;
	
	@Context
	protected UriInfo context;
	
	@GET
	@Path("Status")
	@Produces (MediaType.TEXT_PLAIN)
	public String healthCheck(UriInfo context) {
		return userBusiness.healthcheck(context);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String addUser(String username, String password) {
		return userBusiness.add(username, password);
	}
	
	@GET
	@Path("/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getUser(@PathParam("username") String username, String password) {
		return userBusiness.get(username, password);
	}
}
