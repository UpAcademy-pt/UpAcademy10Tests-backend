package pt.aubay.testesproject.business;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.NotFoundException;

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

@Transactional
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

	@Transactional
	public void add(TestDTO test){
		//We need to check if test object is valid
		checkTestValidToAdd(test);
		//Saves current time;
		Test testEntity=new Test();
		testEntity=addDTOAsEntity(test);
		setDate(testEntity);
		testRepository.addEntity(testEntity);
	}
	
	@Transactional
	//optimization of getAll -> only retrieves what is needed in tables -> lazy fetching (simplified=true)
	public Set<TestStatistics> getAll(boolean simplified) {
		Set<TestDTO> allTestDTO=new HashSet<TestDTO>();
		for(Test elem:testRepository.getAll())
			allTestDTO.add(convertEntityToDTO(elem, simplified));
		Set<TestStatistics> allTests=new HashSet<TestStatistics>();
		for(TestDTO elem:allTestDTO)
			allTests.add(convertDTOToStatistics(elem));
		return allTests;
	}
	
	@Transactional
	public Set<TestStatistics> getAll(){
		return getAll(false);
	}
	
	@Transactional
	public TestDTO get(long id, boolean solutions) {
		if(!testRepository.idExists(id))
			throw new NotFoundException("No such id in database");
		TestDTO testDTO=new TestDTO();
		testDTO=convertEntityToDTO(testRepository.getEntity(id));
		
		//remove solution
		if(!solutions)
			testDTO=removeSolution(testDTO);
		/*for(QuestionDTO question: testDTO.getQuestions())
			question.setSolution(null);*/
		return testDTO;
		
		//return Response.ok(testRepository.getAll(), MediaType.APPLICATION_JSON).build();
	}
	
	@Transactional
	public TestDTO get(long id) {
		return get(id, false);
	}
	
	@Transactional
	public void edit(TestDTO newTest) {
		checkTestValidToEdit(newTest);
		//We also need to reset back-end-determined values (Date and Average Score)
		Test test=new Test();
		test=convertDTOToEntity(newTest);
		//resetValues(test);
		testRepository.editEntity(test);
	}
	
	@Transactional
	public void remove(long id) {
		if(!testRepository.idExists(id))
			throw new NotFoundException("No such id in database");
		
		//First, we need to check if test exists in any solved test or test session
		if(solvedRepository.checkIfTestExists(id))
			throw new BadRequestException("Cannot delete Test used in a solved test present in the database");
		
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
				throw new BadRequestException("Cannot delete Test used in an open session");
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
	
	public void checkTestValidToAdd(TestDTO test){
		//First, we need to check if all parameters needed were introduced
		checkIfParametersThere(test);
		//We need to check if the test name is new (must be unique)
		if(testRepository.testExists(test.getTestName()))
			throw new NotAcceptableException("Test Name exists already");
	}
	
	public void checkTestValidToEdit(TestDTO newTest) {
		//First, we need to check if all parameters needed were introduced 
		checkIfParametersThere(newTest,true);
		//We then need to check if ID exists in database
		if(!testRepository.idExists(newTest.getId()))
			throw new NotAcceptableException("There is no such ID in database");
		//We also need to check if there is any change in testName, author and timer and check the changed fields accordingly
		//To do so, first we need to retrieve the corresponding entity
	}
	
	public void checkIfParametersThere(TestDTO test, boolean needID){
		if(needID && test.getId()==0)
			throw new NotAcceptableException("Fields must be all present, including ID.");
		if(!(test.getAuthor()!=null &&
			test.getQuestions()!=null &&
			test.getTimer()!=0 &&
			test.getTestName()!=null))
			throw new NotAcceptableException("Fields must be all present.");
	}
	
	public void checkIfParametersThere(TestDTO test) {
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
	public TestDTO convertEntityToDTO(Test test, boolean simplified) {
		//String dateString;
		String dateTimeString;

		TestDTO testDTO=new TestDTO();
		
		///This takes into account simplification procedure (optimization) - not all data might be needed for a particular purpose
		if(!simplified) {
			///We need to convert Questions Entity to DTO
			Set <QuestionDTO> questionsDTO=new HashSet();
			for(Questions elem: test.getQuestions())
				questionsDTO.add(questionBusiness.convertEntityToDTO(elem));
			
			testDTO.setQuestions(questionsDTO);
			
		}
		//We need to convert user Entity to DTO
		testDTO.setAuthor(userBusiness.convertEntityToDTO(test.getAuthor()));
		
		testDTO.setAverageScore(test.getAverageScore());
		testDTO.setTestName(test.getTestName());
		testDTO.setTimer(test.getTimer());
		testDTO.setId(test.getId());
		testDTO.setSubmittedTests(test.getSubmittedTests());
		
		DateTimeFormatter formatter =DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		dateTimeString=test.getDateTime().format(formatter);
		testDTO.setDateTime(dateTimeString);
		return testDTO;
	}
	
	public TestDTO convertEntityToDTO(Test test) {
		return convertEntityToDTO(test, false);
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
	
		//Need to check if necessary, for it is expected that the average score be dynamically determined
		test.setAverageScore(0);
		
		//We change the date for each edit
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
