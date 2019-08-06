package pt.aubay.testesproject.business;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import pt.aubay.testesproject.models.dto.QuestionDTO;
import pt.aubay.testesproject.models.dto.TestDTO;
import pt.aubay.testesproject.models.entities.Questions;
import pt.aubay.testesproject.models.entities.RegisteredUser;
import pt.aubay.testesproject.models.entities.Test;
import pt.aubay.testesproject.repositories.TestRepository;

public class TestBusiness {
	@Inject
	TestRepository testRepository;
	
	@Inject
	QuestionBusiness questionBusiness;
	
	/*@Inject
	RegisteredUserBusiness userBusiness;*/
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////CRUD-Methods//////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public Response add(TestDTO test){
		//We need to check if test object is valid
		Response response=checkTestValidToAdd(test);
		if(response.getStatus()!=Response.Status.OK.getStatusCode())
			return response;
		//Saves current time;
		Test testEntity=new Test();
		testEntity=addDTOAsEntity(test);
		setDate(testEntity);
		testRepository.addEntity(testEntity);
		return Response.ok().entity("Success").build();
	}
	
	public Response getAll() {
		Set<TestDTO> allTest=new HashSet();
		for(Test elem:testRepository.getAll())
			allTest.add(convertEntityToDTO(elem));
		return Response.ok(allTest, MediaType.APPLICATION_JSON).build();
		
		//return Response.ok(testRepository.getAll(), MediaType.APPLICATION_JSON).build();
	}
	
	
	public Response edit(TestDTO newTest) {
		Response response=checkTestValidToEdit(newTest);
		if(response.getStatus()!=Response.Status.OK.getStatusCode())
			return response;
		//We also need to reset back-end-determined values (Date and Average Score)
		Test test=new Test();
		test=convertDTOToEntity(newTest);
		//resetValues(test);
		testRepository.editEntity(test);
		return Response.ok().entity("Success").build();
	}
	
	public Response remove(TestDTO test) {
		if(!testRepository.idExists(test.getId()))
			return Response.status(Status.NOT_FOUND).entity("No such id in database").build();	
		testRepository.deleteEntity(test.getId());
		return Response.ok().entity("Success").build();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////Checking-Methods//////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public Response checkTestValidToAdd(TestDTO test) {
		//First, we need to check if all parameters needed were introduced
		if(checkIfParametersThere(test).getStatus()!=Response.Status.OK.getStatusCode())
			return checkIfParametersThere(test);
		//We need to check if the test name is new (must be unique)
		if(testRepository.testExists(test.getTestName()))
			return Response.status(Status.NOT_ACCEPTABLE).entity("Test Name exists already").build();
		return Response.ok().entity("Success").build();
	}
	
	public Response checkTestValidToEdit(TestDTO newTest) {
		//First, we need to check if all parameters needed were introduced
		if(checkIfParametersThere(newTest,true).getStatus()!=Response.Status.OK.getStatusCode())
			return checkIfParametersThere(newTest,true);
		//We then need to check if ID exists in database
		if(!testRepository.idExists(newTest.getId()))
			return Response.status(Status.NOT_ACCEPTABLE).entity("There is no such ID in database").build();
		//We also need to check if there is any change in testName, author and timer and check the changed fields accordingly
		//To do so, first we need to retrieve the corresponding entity
		return null;
	}
	
	public Response checkIfParametersThere(TestDTO test, boolean needID) {
		if(needID && test.getId()==0)
			return Response.status(Status.NOT_ACCEPTABLE).entity("Fields must be all present, including ID.").build();
		if(	test.getAuthor()!=null &&
			test.getQuestions()!=null &&
			test.getTimer()!=0 &&
			test.getTestName()!=null)
			return Response.ok().entity("Success").build();
		return Response.status(Status.NOT_ACCEPTABLE).entity("Fields must be all present.").build();
	}
	
	public Response checkIfParametersThere(TestDTO test) {
		return checkIfParametersThere(test, false);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////Auxiliary-Methods/////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void setDate(Test test) {
		LocalDateTime dateTime=LocalDateTime.now();
		//LocalDate date=LocalDate.now();
		//test.setDate(date);
		test.setDateTime(dateTime);
	}
	
	public void resetValues(Test test) {
		setDate(test);
		test.setAverageScore(0);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////DTO-ENTITY CONVERSION/////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public TestDTO convertEntityToDTO(Test test) {
	//String dateString;
	String dateTimeString;
	
	
	///We need to convert Questions Entity to DTO
	TestDTO testDTO=new TestDTO();
	Set <QuestionDTO> questionsDTO=new HashSet();
	for(Questions elem: test.getQuestions())
		questionsDTO.add(questionBusiness.convertEntityToDTO(elem));
	
	testDTO.setQuestions(questionsDTO);
	testDTO.setAuthor(test.getAuthor());
	testDTO.setAverageScore(test.getAverageScore());
	testDTO.setTestName(test.getTestName());
	testDTO.setTimer(test.getTimer());
	testDTO.setId(test.getId());
	
	DateTimeFormatter formatter =DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	dateTimeString=test.getDateTime().format(formatter);
	testDTO.setDateTime(dateTimeString);
	return testDTO;
	}
	
	public Test convertDTOToEntity(TestDTO testDTO) {
		Test test=testRepository.getEntity(testDTO.getId());
		test.setId(testDTO.getId());
		
		///We need to convert Questions DTO to Entity
		Set <Questions> questions=new HashSet();
		for(QuestionDTO elem: testDTO.getQuestions())
			questions.add(questionBusiness.convertDTOToEntity(elem));
		
		test.setQuestions(questions);
		test.setAuthor(testDTO.getAuthor());
		test.setTestName(testDTO.getTestName());
		test.setTimer(testDTO.getTimer());
	
		//Need to check if necessary, for it is expected that the averagescore will dynamically determined
		test.setAverageScore(0);
		
		//We change the date for each edit (or save its "created in" date?)
		LocalDateTime newTime=LocalDateTime.now();
		test.setDateTime(newTime);
		
		return test;
	}
	
	
	public Test addDTOAsEntity(TestDTO testDTO) {
		Test test=new Test();
		
		///We need to convert Questions DTO to Entity
		Set <Questions> questions=new HashSet();
		for(QuestionDTO elem: testDTO.getQuestions())
			questions.add(questionBusiness.addDTOasEntity(elem));
		
		test.setQuestions(questions);
		
		//We need to convert Author DTO to Entity
		
		//RegisteredUser author=
		
		test.setAuthor(testDTO.getAuthor());
		test.setTestName(testDTO.getTestName());
		test.setTimer(testDTO.getTimer());
		test.setAverageScore(0);
		LocalDateTime newTime=LocalDateTime.now();
		test.setDateTime(newTime);
		return test;
	}
}
