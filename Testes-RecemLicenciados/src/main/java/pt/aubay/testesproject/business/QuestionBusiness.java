package pt.aubay.testesproject.business;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import pt.aubay.testesproject.models.entities.Questions;
import pt.aubay.testesproject.repositories.CategoryRepository;
import pt.aubay.testesproject.repositories.QuestionRepository;

////NOTES: Edit removes identification (ID) from category - to correct - if category is left as object instead of its string

public class QuestionBusiness {
	@Inject
	QuestionRepository questionRepository;
	
	@Inject
	CategoryRepository categoryRepository;
	
	public Response add(Questions question){
		//We need to check if question object is valid
		Response response=checkQuestionValidToAdd(question);
		if(response.getStatus()!=Response.Status.OK.getStatusCode())
			return response;
		question.setCategory(categoryRepository.getCategory(question.getCategory().getCategory()));	
		questionRepository.addEntity(question);
		return Response.ok().entity("Success").build();
	}
	
	public Response get(long id) {
		if(!questionRepository.idExists(id))
			return Response.status(Status.NOT_ACCEPTABLE).entity("There is no such ID in database").build();
		return Response.ok(questionRepository.getEntity(id), MediaType.APPLICATION_JSON).build();
	}
	
	public Response getAll() {
		return Response.ok(questionRepository.getAll(), MediaType.APPLICATION_JSON).build();
	}
	
	public Response edit(Questions newQuestions) {
		Response response=checkQuestionValidToEdit(newQuestions);
		if(response.getStatus()!=Response.Status.OK.getStatusCode())
			return response;
		questionRepository.editEntity(newQuestions);
		return Response.ok().entity("Success").build();
	}
	
	public Response remove(Questions question) {
	if(!questionRepository.idExists(question.getId()))
		return Response.status(Status.NOT_FOUND).entity("No such id in database").build();	
	questionRepository.deleteEntity(question.getId());
	return Response.ok().entity("Success").build();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////Checking-Methods//////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public Response checkQuestionValidToAdd(Questions question) {
		//First, we need to check if all parameters needed were introduced
		if(checkIfParametersThere(question).getStatus()!=Response.Status.OK.getStatusCode())
			return checkIfParametersThere(question);
		//We need to check if question already exists
		if(questionRepository.questionExists(question.getQuestion()))
			return Response.status(Status.FORBIDDEN).entity("This question exists already").build();
		//We need to check if category is new -> if so, we need to add category.
		if(!categoryRepository.categoryExists(question.getCategory()))
			return Response.status(Status.NOT_ACCEPTABLE).entity("This category was not created beforehand").build();
		return Response.ok().entity("Success").build();
	}
	
	public Response checkQuestionValidToEdit(Questions newQuestion) {
		//First, we need to check if all parameters needed were introduced
		if(checkIfParametersThere(newQuestion,true).getStatus()!=Response.Status.OK.getStatusCode())
			return checkIfParametersThere(newQuestion,true);
		//We then need to check if ID exists in database
		if(!questionRepository.idExists(newQuestion.getId()))
			return Response.status(Status.NOT_ACCEPTABLE).entity("There is no such ID in database").build();
		//We also need to check if there is a change in both the question and category fields and check the changed fields accordingly
		//To do so, first we need to retrieve the corresponding entity
		Questions oldQuestion=questionRepository.getEntity(newQuestion.getId());
		if(	!oldQuestion.getCategory().equals(newQuestion.getCategory()) && 
			!categoryRepository.categoryExists(newQuestion.getCategory()))
			return Response.status(Status.NOT_ACCEPTABLE).entity("This category was not created beforehand").build();
		if( !oldQuestion.getQuestion().equals(newQuestion.getQuestion()) && 
			questionRepository.questionExists(newQuestion.getQuestion()))
			return Response.status(Status.FORBIDDEN).entity("This question exists already").build();
		return Response.ok().entity("Success").build();
	}
	
	public Response checkIfParametersThere(Questions question, boolean needID) {
		if(needID && question.getId()==0)
			return Response.status(Status.NOT_ACCEPTABLE).entity("Fields must be all present, including ID.").build();
		if(		question.getCategory()!=null && 
				question.getOptions()!=null &&
				question.getQuestion()!=null &&
				question.getSolution()!=null)
			return Response.ok().entity("Success").build();
		return Response.status(Status.NOT_ACCEPTABLE).entity("Fields must be all present.").build();
	}
	public Response checkIfParametersThere(Questions question) {
		return checkIfParametersThere(question, false);
	}
	
}
