package pt.aubay.testesproject.business;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import pt.aubay.testesproject.models.dto.QuestionDTO;
import pt.aubay.testesproject.models.entities.Questions;
import pt.aubay.testesproject.repositories.CategoryRepository;
import pt.aubay.testesproject.repositories.QuestionRepository;
import pt.aubay.testesproject.repositories.TestRepository;
import pt.aubay.testesproject.utils.RandomGeneratorUtils;


public class QuestionBusiness {
	@Inject
	QuestionRepository questionRepository;
	
	@Inject
	CategoryRepository categoryRepository;
	
	@Inject
	TestRepository testRepository;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////CRUD-Methods//////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public Response add(QuestionDTO question){
		//We need to check if question object is valid
		Response response=checkQuestionValidToAdd(question);
		if(response.getStatus()!=Response.Status.OK.getStatusCode())
			return response;
		
		//question.setCategory(categoryRepository.getCategory(question.getCategory().getCategory()));
		
		
		//converts DTO to Entity
		Questions questionEntity=addDTOasEntity(question);
		questionRepository.addEntity(questionEntity);
		return Response.ok().entity("Success").build();
	}
	
	public Response get(long id) {
		if(!questionRepository.idExists(id))
			return Response.status(Status.NOT_ACCEPTABLE).entity("There is no such ID in database").build();
		QuestionDTO questionDTO=convertEntityToDTO(questionRepository.getEntity(id));
		return Response.ok(questionDTO, MediaType.APPLICATION_JSON).build();
	}
	
	public Response getAll() {
		ArrayList<QuestionDTO> allUsers=new ArrayList<QuestionDTO>();
		for(Questions elem:questionRepository.getAll())
			allUsers.add(convertEntityToDTO(elem));
		return Response.ok(allUsers, MediaType.APPLICATION_JSON).build();
		//return Response.ok(questionRepository.getAll(), MediaType.APPLICATION_JSON).build();
	}
	
	public Response edit(QuestionDTO newQuestions) {
		Response response=checkQuestionValidToEdit(newQuestions);
		if(response.getStatus()!=Response.Status.OK.getStatusCode())
			return response;
		
		//converts DTO to Entity
		Questions newQuestionEntity=convertDTOToEntity(newQuestions);
		questionRepository.editEntity(newQuestionEntity);
		return Response.ok().entity("Success").build();
	}
	
	public Response remove(long id) {
	if(!questionRepository.idExists(id))
		return Response.status(Status.NOT_FOUND).entity("No such id in database").build();	
	if(testRepository.questionExists(id))
		return Response.status(Status.FORBIDDEN).entity("Cannot delete question used in test.").build();
	questionRepository.deleteEntity(id);
	return Response.ok().entity("Success").build();
	}
	
	public Response getRandomQuestions(String category, long number) {
		//We need to get all questions ID's with said category
		List<Long> questions=questionRepository.getQuestionIDS(category);
		long maxRange = questions.size();
		if(number>maxRange)
			 Response.status(Status.FORBIDDEN).entity("Invalid number of questions.").build();
		int[] randomIndexes=RandomGeneratorUtils.getRandomNumbers((int)number,(int)maxRange);
		List<Long> questionsFiltered=new ArrayList<Long>();
		for(int i : randomIndexes)
			questionsFiltered.add(questions.get(i));
		List<Questions> randomQuestions=questionRepository.getRandomQuestions(questionsFiltered);
		return Response.ok(randomQuestions, MediaType.APPLICATION_JSON).build();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////Checking-Methods//////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public Response checkQuestionValidToAdd(QuestionDTO question) {
		//First, we need to check if all parameters needed were introduced
		if(checkIfParametersThere(question).getStatus()!=Response.Status.OK.getStatusCode())
			return checkIfParametersThere(question);
		//We need to check if question already exists in database
		if(questionRepository.questionExists(question.getQuestion()))
			return Response.status(Status.FORBIDDEN).entity("This question exists already").build();
		//We need to check if category is new -> if so, we need to add category.
		if(!categoryRepository.categoryExists(question.getCategory()))
			return Response.status(Status.NOT_ACCEPTABLE).entity("This category was not created beforehand").build();
		return Response.ok().entity("Success").build();
	}
	
	public Response checkQuestionValidToEdit(QuestionDTO newQuestion) {
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
	
	public Response checkIfParametersThere(QuestionDTO question, boolean needID) {
		if(needID && question.getId()==0)
			return Response.status(Status.NOT_ACCEPTABLE).entity("Fields must be all present, including ID.").build();
		if(		question.getCategory()!=null && 
				question.getOptions()!=null &&
				question.getQuestion()!=null &&
				question.getSolution()!=null)
			return Response.ok().entity("Success").build();
		return Response.status(Status.NOT_ACCEPTABLE).entity("Fields must be all present.").build();
	}
	public Response checkIfParametersThere(QuestionDTO question) {
		return checkIfParametersThere(question, false);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////DTO-ENTITY CONVERSION/////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public QuestionDTO convertEntityToDTO(Questions question) {
		QuestionDTO questionDTO=new QuestionDTO();
		questionDTO.setCategory(question.getCategory());
		questionDTO.setId(question.getId());
		questionDTO.setOptions(question.getOptions());
		questionDTO.setQuestion(question.getQuestion());
		questionDTO.setSolution(question.getSolution());
		return questionDTO;
	}
	
	public Questions convertDTOToEntity(QuestionDTO questionDTO) {
		Questions question=questionRepository.getEntity(questionDTO.getId());
		question.setCategory(questionDTO.getCategory());
		question.setId(questionDTO.getId());
		question.setOptions(questionDTO.getOptions());
		question.setQuestion(questionDTO.getQuestion());
		question.setSolution(questionDTO.getSolution());
		return question;
	}
	
	public Questions addDTOasEntity(QuestionDTO questionDTO) {
		Questions question=new Questions();
		
		question.setCategory(questionDTO.getCategory());
		question.setOptions(questionDTO.getOptions());
		question.setQuestion(questionDTO.getQuestion());
		question.setSolution(questionDTO.getSolution());
		return question;
	}
	
}
