package pt.aubay.testesproject.business;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import pt.aubay.testesproject.execptionHandling.AppException;
import pt.aubay.testesproject.models.dto.QuestionDTO;
import pt.aubay.testesproject.models.dto.TestDTO;
import pt.aubay.testesproject.models.entities.Questions;
import pt.aubay.testesproject.models.entities.RegisteredUser;
import pt.aubay.testesproject.models.entities.Test;
import pt.aubay.testesproject.models.statistics.TestStatistics;
import pt.aubay.testesproject.repositories.RegisteredUserRepository;
import pt.aubay.testesproject.repositories.SolvedTestRepository;
import pt.aubay.testesproject.repositories.TestRepository;
import pt.aubay.testesproject.repositories.TestSessionRepository;

public class TestBusiness {
	@Inject
	TestRepository testRepository;
	
	@Inject
	SolvedTestRepository solvedRepository;
	
	@Inject
	QuestionBusiness questionBusiness;
	
	@Inject
	RegisteredUserRepository userRepository;
	
	@Inject
	RegisteredUserBusiness userBusiness;
	
	@Inject
	TestSessionRepository sessionRepository;
	
	@Inject
	TestCommonBusiness testCommonBusiness;

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////CRUD-Methods//////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void add(TestDTO test) throws AppException{
		//We need to check if test object is valid
		checkTestValidToAdd(test);
		//Saves current time;
		Test testEntity=new Test();
		testEntity=addDTOAsEntity(test);
		setDate(testEntity);
		testRepository.addEntity(testEntity);
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
	
	public TestDTO get(long id) throws AppException {
		if(!testRepository.idExists(id))
			throw new AppException("No such id in database", Status.NOT_FOUND.getStatusCode());
		TestDTO testDTO=new TestDTO();
		testDTO=convertEntityToDTO(testRepository.getEntity(id));
		
		//remove solution
		testDTO=removeSolution(testDTO);
		/*for(QuestionDTO question: testDTO.getQuestions())
			question.setSolution(null);*/
		return testDTO;
		
		//return Response.ok(testRepository.getAll(), MediaType.APPLICATION_JSON).build();
	}
	
	
	public void edit(TestDTO newTest) throws AppException {
		checkTestValidToEdit(newTest);
		//We also need to reset back-end-determined values (Date and Average Score)
		Test test=new Test();
		test=convertDTOToEntity(newTest);
		//resetValues(test);
		testRepository.editEntity(test);
	}
	
	public void remove(long id) throws AppException {
		if(!testRepository.idExists(id))
			throw new AppException("No such id in database", Status.NOT_FOUND.getStatusCode());
		
		//First, we need to check if test exists in any solved test or test session
		if(solvedRepository.checkIfTestExists(id))
			throw new AppException("Cannot delete Test used in a solved test present in the database", Status.BAD_REQUEST.getStatusCode());
		
		boolean allInvalid=true;
		if(sessionRepository.checkIfTestExists(id)) {
			//we need to check if session all sessions involving said test are still valid
			List<Long> sessionIDs=sessionRepository.getSessionIDsOfTest(id);
			for(long sessionID: sessionIDs) {
				if(!testCommonBusiness.checkIfSessionValid(sessionID)) {
					sessionRepository.deleteEntity(sessionID);
				}
				else {
					allInvalid=false;
				}
			}
			if(!allInvalid)
				throw new AppException("Cannot delete Test used in an open session", Status.BAD_REQUEST.getStatusCode());
		}

		//This delete is performed via query
		testRepository.deleteEntity(id);
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
	
	public void checkTestValidToAdd(TestDTO test) throws AppException {
		//First, we need to check if all parameters needed were introduced
		checkIfParametersThere(test);
		//We need to check if the test name is new (must be unique)
		if(testRepository.testExists(test.getTestName()))
			throw new AppException("Test Name exists already", Status.NOT_ACCEPTABLE.getStatusCode());
	}
	
	public void checkTestValidToEdit(TestDTO newTest) throws AppException {
		//First, we need to check if all parameters needed were introduced 
		checkIfParametersThere(newTest,true);
		//We then need to check if ID exists in database
		if(!testRepository.idExists(newTest.getId()))
			throw new AppException("There is no such ID in database", Status.NOT_ACCEPTABLE.getStatusCode());
		//We also need to check if there is any change in testName, author and timer and check the changed fields accordingly
		//To do so, first we need to retrieve the corresponding entity
	}
	
	public void checkIfParametersThere(TestDTO test, boolean needID) throws AppException {
		if(needID && test.getId()==0)
			throw new AppException("Fields must be all present, including ID.", Status.NOT_ACCEPTABLE.getStatusCode());
		if(!(test.getAuthor()!=null &&
			test.getQuestions()!=null &&
			test.getTimer()!=0 &&
			test.getTestName()!=null))
			throw new AppException("Fields must be all present.", Status.NOT_ACCEPTABLE.getStatusCode());
	}
	
	public void checkIfParametersThere(TestDTO test) throws AppException {
		checkIfParametersThere(test, false);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////Auxiliary-Methods/////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void setDate(Test test) {
		LocalDateTime dateTime=LocalDateTime.now();
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
	
	public TestDTO removeSolution(TestDTO test) {
		for(QuestionDTO question: test.getQuestions()) {
			question.setSolution(null);
		}
		return test;
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
