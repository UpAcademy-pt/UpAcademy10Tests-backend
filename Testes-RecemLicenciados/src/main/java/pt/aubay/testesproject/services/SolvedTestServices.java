package pt.aubay.testesproject.services;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import pt.aubay.testesproject.business.SolvedTestBusiness;
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
	public Response addSolvedTest(SolvedTestDTO solvedTest) {
		return solvedBusiness.add(solvedTest);
	}
	
	@GET
	@Path("all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllSolvedTests() {
		return solvedBusiness.getAll();
	}
	
	@DELETE
	@Path("remove")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces (MediaType.TEXT_PLAIN)
	public Response deleteSolvedTest(SolvedTestDTO solvedTest) {
		return solvedBusiness.remove(solvedTest);
	}
}
