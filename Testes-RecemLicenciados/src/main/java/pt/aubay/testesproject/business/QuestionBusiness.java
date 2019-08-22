package pt.aubay.testesproject.business;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import pt.aubay.testesproject.execptionHandling.AppException;
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
	
	public void add(QuestionDTO question) throws AppException{
		//We need to check if question object is valid
		checkQuestionValidToAdd(question);
		
		//question.setCategory(categoryRepository.getCategory(question.getCategory().getCategory()));
		
		
		//converts DTO to Entity
		Questions questionEntity=addDTOasEntity(question);
		questionRepository.addEntity(questionEntity);
	}
	
	public QuestionDTO get(long id) throws AppException {
		if(!questionRepository.idExists(id))
			throw new AppException("There is no such ID in database", Status.NOT_ACCEPTABLE.getStatusCode());
		QuestionDTO questionDTO=convertEntityToDTO(questionRepository.getEntity(id));
		return questionDTO;
	}
	
	public List<QuestionDTO> getAll() {
		List<QuestionDTO> allQuestions=new ArrayList<QuestionDTO>();
		for(Questions elem:questionRepository.getAll())
			allQuestions.add(convertEntityToDTO(elem));
		return allQuestions;
		//return Response.ok(questionRepository.getAll(), MediaType.APPLICATION_JSON).build();
	}
	
	public void edit(QuestionDTO newQuestions) throws AppException {
		checkQuestionValidToEdit(newQuestions);
		
		//converts DTO to Entity
		Questions newQuestionEntity=convertDTOToEntity(newQuestions);
		questionRepository.editEntity(newQuestionEntity);
	}
	
	public void remove(long id) throws AppException {
	if(!questionRepository.idExists(id))
		throw new AppException("No such id in database", Status.NOT_FOUND.getStatusCode());
	if(questionRepository.checkIfQuestionInTest(id))
		throw new AppException("Cannot delete question used in test.", Status.BAD_REQUEST.getStatusCode());
	questionRepository.deleteEntity(id);
	}
	
	public List<QuestionDTO> getRandomQuestions(String category, long number) throws AppException {
		//We need to get all questions ID's with said category
		List<Long> questions=questionRepository.getQuestionIDS(category);
		//the maximum range allowed is the number of questions of said category
		long maxRange = questions.size();
		if(number>maxRange)
			throw new AppException("Invalid number of questions.", Status.BAD_REQUEST.getStatusCode());
		//we generate randomIndexes
		int[] randomIndexes=RandomGeneratorUtils.getRandomNumbers((int)number,(int)maxRange);
		
		//then, a list with the questions IDs we wanted is created
		List<Long> questionsFiltered=new ArrayList<Long>();
		for(int i : randomIndexes)
			questionsFiltered.add(questions.get(i));
		
		//finally, we get the questions corresponding to those ID's
		List<Questions> randomQuestions=questionRepository.getRandomQuestions(questionsFiltered);
		
		//we then convert to DTO
		List<QuestionDTO> randomQuestionsDTO=new ArrayList<QuestionDTO>();
		for(Questions elem:randomQuestions)
			randomQuestionsDTO.add(convertEntityToDTO(elem));
		return randomQuestionsDTO;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////Checking-Methods//////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void checkQuestionValidToAdd(QuestionDTO question) throws AppException {
		//First, we need to check if all parameters needed were introduced
		checkIfParametersThere(question);
		//We need to check if question already exists in database
		if(questionRepository.questionExists(question.getQuestion()))
			throw new AppException("This question exists already", Status.BAD_REQUEST.getStatusCode());
		//We need to check if category is new -> if so, we need to add category.
		if(!categoryRepository.categoryExists(question.getCategory()))
			throw new AppException("This category was not created beforehand", Status.NOT_ACCEPTABLE.getStatusCode());
	}
	
	public void checkQuestionValidToEdit(QuestionDTO newQuestion) throws AppException {
		//First, we need to check if all parameters needed were introduced
		checkIfParametersThere(newQuestion,true);
		//We then need to check if ID exists in database
		if(!questionRepository.idExists(newQuestion.getId()))
			throw new AppException("There is no such ID in database", Status.NOT_FOUND.getStatusCode());
		//We also need to check if there is a change in both the question and category fields and check the changed fields accordingly
		//To do so, first we need to retrieve the corresponding entity
		Questions oldQuestion=questionRepository.getEntity(newQuestion.getId());
		if(	!oldQuestion.getCategory().equals(newQuestion.getCategory()) && 
			!categoryRepository.categoryExists(newQuestion.getCategory()))
			throw new AppException("This category was not created beforehand", Status.NOT_ACCEPTABLE.getStatusCode());
		if( !oldQuestion.getQuestion().equals(newQuestion.getQuestion()) && 
			questionRepository.questionExists(newQuestion.getQuestion()))
			throw new AppException("This question exists already", Status.BAD_REQUEST.getStatusCode());
	}
	
	public void checkIfParametersThere(QuestionDTO question, boolean needID) throws AppException {
		if(needID && question.getId()==0)
			throw new AppException("Fields must be all present, including ID.", Status.NOT_ACCEPTABLE.getStatusCode());
		if(!(	question.getCategory()!=null && 
				question.getOptions()!=null &&
				question.getQuestion()!=null &&
				question.getSolution()!=null))
		throw new AppException("Fields must be all present.", Status.NOT_ACCEPTABLE.getStatusCode());
	}
	
	
	public void checkIfParametersThere(QuestionDTO question) throws AppException {
		checkIfParametersThere(question, false);
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
	
	public Questions addDTOasEntity(QuestionDTO questionDTO, boolean needID) {
		Questions question=new Questions();
		
		//used when adding tests
		if(needID)
			question.setId(questionDTO.getId());
		
		question.setCategory(questionDTO.getCategory());
		question.setOptions(questionDTO.getOptions());
		question.setQuestion(questionDTO.getQuestion());
		question.setSolution(questionDTO.getSolution());
		return question;
	}
	
	public Questions addDTOasEntity(QuestionDTO questionDTO) {
		return addDTOasEntity(questionDTO, false);
	}
	
}
