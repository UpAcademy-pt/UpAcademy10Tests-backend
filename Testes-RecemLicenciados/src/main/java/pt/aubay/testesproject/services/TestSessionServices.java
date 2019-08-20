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

import pt.aubay.testesproject.business.TestSessionBusiness;
import pt.aubay.testesproject.models.dto.TestDTO;
import pt.aubay.testesproject.models.sessions.TestSession;

@Transactional
@Path("test/session")
public class TestSessionServices {
	
	@Inject
	TestSessionBusiness sessionBusiness;
	
	@Context
	protected UriInfo context;
	
	@GET
	@Path("status")
	@Produces (MediaType.TEXT_PLAIN)
	public String healthCheck() {
		return "URI " + context.getRequestUri().toString() + " is OK!";
	}
	
	@POST
	@Path("add/{testID}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addTest(TestSession session, @PathParam("testID") long testID) {
		return sessionBusiness.add(session, testID);
	}
	
	@GET
	@Path("begin/{sessionID}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTest(@PathParam("sessionID") long sessionID) {
		return sessionBusiness.get(sessionID);
	}
}
