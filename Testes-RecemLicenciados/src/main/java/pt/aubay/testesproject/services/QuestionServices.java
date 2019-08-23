package pt.aubay.testesproject.services;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;

import pt.aubay.testesproject.business.QuestionBusiness;
import pt.aubay.testesproject.execptionHandling.AppException;
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
	public Response addQuestion(QuestionDTO question) throws AppException {
		questionBusiness.add(question);
		return Response.ok().entity("Success").build();
	}
	
	
	//if need be
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getQuestion(@PathParam("id") long id) throws AppException {
		return Response.ok(questionBusiness.get(id), MediaType.APPLICATION_JSON).build();
	}
	
	@GET
	@Path("all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllQuestions() {
		return Response.ok(questionBusiness.getAll(), MediaType.APPLICATION_JSON).build();
	}
	
	@GET
	@Path("questions/{category}/{number}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRandomQuestions(@PathParam("category") String category, @PathParam("number") long number) throws AppException {
		return Response.ok(questionBusiness.getRandomQuestions(category, number), MediaType.APPLICATION_JSON).build();
	}
	
	@PUT
	@Path("edit")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response editQuestion(QuestionDTO question) throws AppException {
		questionBusiness.edit(question);
		return Response.ok().entity("Success").build();
		
	}
	
	@DELETE
	@Path("remove/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces (MediaType.TEXT_PLAIN)
	public Response deleteQuestion(@PathParam("id") long id) throws AppException {
		questionBusiness.remove(id);
		return Response.ok().entity("Success").build();
	}
	
	@GET
	@Path("filter")
	@Produces({MediaType.APPLICATION_JSON})
	public List<QuestionDTO> getFilteredQuestions(@QueryParam("category") String category){
		List<QuestionDTO> questions=questionBusiness.getAll().stream().filter(question -> 
		question.getCategory().getCategory().equals(category)).collect(Collectors.toList());
		return questions;
	}
	
//	@GET
//	@Path("getnumber/{category}")
//	@Produces(MediaType.APPLICATION_JSON)
//	public Long getRandomQuestions(@PathParam("category") String category) {
//		return questionRepository.count(category);
//	}
}
