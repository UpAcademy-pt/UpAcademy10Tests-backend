package pt.aubay.testesproject.services;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import pt.aubay.testesproject.business.SolvedTestBusiness;
import pt.aubay.testesproject.execptionHandling.AppException;
import pt.aubay.testesproject.models.dto.SolvedTestDTO;

@Transactional
@Path("solved")
public class SolvedTestServices {
	@Inject
	SolvedTestBusiness solvedBusiness;
	
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
	public Response addSolvedTest(SolvedTestDTO solvedTest) throws AppException {
		solvedBusiness.add(solvedTest);
		return Response.ok().entity("Success").build();
	}
	
	@POST
	@Path("add/{sessionID}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response addSolvedTest(SolvedTestDTO solvedTest, @PathParam("sessionID") long sessionID) throws AppException {
		solvedBusiness.add(solvedTest, sessionID);
		return Response.ok().entity("Success").build();
	}
	
	@GET
	@Path("all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllSolvedTests() {
		return Response.ok(solvedBusiness.getAll(), MediaType.APPLICATION_JSON).build();
	}
	
	@DELETE
	@Path("remove/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces (MediaType.TEXT_PLAIN)
	public Response deleteSolvedTest(@PathParam("id") long id) throws AppException {
		solvedBusiness.remove(id);
		return Response.ok().entity("Success").build();
	}
}
