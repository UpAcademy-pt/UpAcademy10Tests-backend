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

import pt.aubay.testesproject.business.QuestionBusiness;
import pt.aubay.testesproject.models.dto.QuestionDTO;
import pt.aubay.testesproject.models.entities.Questions;
import pt.aubay.testesproject.repositories.QuestionRepository;

@Transactional
@Path("question")
public class QuestionServices {
	@Inject
	protected QuestionBusiness questionBusiness;
	
	@Inject
	protected QuestionRepository questionRepository;
	
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
	public Response addQuestion(QuestionDTO question) {
		return questionBusiness.add(question);
	}
	
	
	//if need be
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getQuestion(@PathParam("id") long id) {
		return questionBusiness.get(id);
	}
	
	@GET
	@Path("all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllQuestions() {
		return questionBusiness.getAll();
	}
	
	@GET
	@Path("questions/{category}/{number}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRandomQuestions(@PathParam("category") String category, @PathParam("number") long number) {
		return questionBusiness.getRandomQuestions(category, number);
	}
	
	@PUT
	@Path("edit")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response editQuestion(QuestionDTO question) {
		return questionBusiness.edit(question);
	}
	
	@DELETE
	@Path("remove/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces (MediaType.TEXT_PLAIN)
	public Response deleteQuestion(@PathParam("id") long id) {
		return questionBusiness.remove(id);
	}
	
	@GET
	@Path("getnumber/{category}")
	@Produces(MediaType.APPLICATION_JSON)
	public Long getRandomQuestions(@PathParam("category") String category) {
		return questionRepository.count(category);
	}
}
