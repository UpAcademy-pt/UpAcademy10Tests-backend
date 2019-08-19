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
import pt.aubay.testesproject.models.dto.SolvedTestDTO;
import pt.aubay.testesproject.models.dto.TestDTO;
import pt.aubay.testesproject.models.entities.Questions;
import pt.aubay.testesproject.models.entities.RegisteredUser;
import pt.aubay.testesproject.models.entities.Test;
import pt.aubay.testesproject.models.statistics.SolvedTestStatistics;
import pt.aubay.testesproject.models.statistics.TestStatistics;
import pt.aubay.testesproject.repositories.RegisteredUserRepository;
import pt.aubay.testesproject.repositories.TestRepository;

public class TestBusiness {
	@Inject
	TestRepository testRepository;
	
	@Inject
	QuestionBusiness questionBusiness;
	
	@Inject
	RegisteredUserRepository userRepository;
	
	@Inject
	RegisteredUserBusiness userBusiness;

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
		Set<TestDTO> allTestDTO=new HashSet<TestDTO>();
		for(Test elem:testRepository.getAll())
			allTestDTO.add(convertEntityToDTO(elem));
		Set<TestStatistics> allTests=new HashSet<TestStatistics>();
		for(TestDTO elem:allTestDTO)
			allTests.add(convertDTOToStatistics(elem));
		return Response.ok(allTests, MediaType.APPLICATION_JSON).build();
		
		//return Response.ok(testRepository.getAll(), MediaType.APPLICATION_JSON).build();
	}
	
	public Response get(long id) {
		if(!testRepository.idExists(id))
			return Response.status(Status.NOT_FOUND).entity("No such id in database").build();
		TestDTO testDTO=new TestDTO();
		testDTO=convertEntityToDTO(testRepository.getEntity(id));
		for(QuestionDTO question: testDTO.getQuestions())
			question.setSolution(null);
		return Response.ok(testDTO, MediaType.APPLICATION_JSON).build();
		
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
	
	public Response remove(long id) {
		if(!testRepository.idExists(id))
			return Response.status(Status.NOT_FOUND).entity("No such id in database").build();
		
		/*In order to delete a test, we must be cautious -> if we simply delete a test which has questions, the questions will be deleted as well, because
		the test has been set as the owning side of the bidirectional relationship between test-questions - remember that we specify this relationship in the test DTO and not
		in the Question DTO. One way to solve this issue is to nullify all questions belonging to the test entity before deleting said test*/
		Test test=testRepository.getEntity(id);
		test.setQuestions(null);
		testRepository.editEntity(test);
		
		///
		testRepository.deleteEntity(id);
		return Response.ok().entity("Success").build();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////Average-Methods///////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//idea: jpql -> get all solved tests that have a specified test id -> do the average using
	//2nd idea: update function gets each time new score and determines new average
	//2nd idea is best, for it keeps a faithful average score even after deleting a solvedTest
	
	public void updateAverageScore(long idTest, int scoreToAdd){
		Test test=testRepository.getEntity(idTest);
		int oldAverage=test.getAverageScore();
		int newAverage;
		long sampleCardinality=test.getSubmittedTests();
		newAverage=(int)((oldAverage*sampleCardinality+scoreToAdd)/(double)(sampleCardinality+1));
		sampleCardinality++;
		test.setSubmittedTests(sampleCardinality);
		test.setAverageScore(newAverage);
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
	
	public Set<String> getCategories(long id){
		
		Set<String> categories=new HashSet<String>();
		
		//next, we retrieve the test from the database, and the set of questions
		Test test=testRepository.getEntity(id);
		Set <Questions> questions=test.getQuestions();
		//then, we run through all questions and gather distinct categories
		for(Questions question : questions)
			categories.add(question.getCategory().getCategory());
		return categories;
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
		
		//We need to convert user Entity to DTO
		testDTO.setAuthor(userBusiness.convertEntityToDTO(test.getAuthor()));
		testDTO.setAverageScore(test.getAverageScore());
		testDTO.setTestName(test.getTestName());
		testDTO.setTimer(test.getTimer());
		testDTO.setId(test.getId());
		testDTO.setSubmittedTests(test.getSubmittedTests());
		
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
		test.setAuthor(userBusiness.convertDTOToEntity(testDTO.getAuthor()));
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
			questions.add(questionBusiness.addDTOasEntity(elem, true));
		
		test.setQuestions(questions);
		
		//We need to convert Author DTO to Entity
		RegisteredUser author=userRepository.getEntity(testDTO.getAuthor().getId());
		
		test.setAuthor(author);
		test.setTestName(testDTO.getTestName());
		test.setTimer(testDTO.getTimer());
		test.setAverageScore(0);
		test.setSubmittedTests(0);
		LocalDateTime newTime=LocalDateTime.now();
		test.setDateTime(newTime);
		return test;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////DTO-STATISTICS CONVERSION/////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public TestStatistics convertDTOToStatistics(TestDTO testDTO) {
		TestStatistics testStatistics=new TestStatistics();
		testStatistics.setTest(testDTO);
		testStatistics.setCategories(getCategories(testDTO.getId()));
		return testStatistics;
	}
}
