package pt.aubay.testesproject.business;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import pt.aubay.testesproject.auxiliary.MyEmail;
import pt.aubay.testesproject.models.dto.AnswerDTO;
import pt.aubay.testesproject.models.dto.CandidateDTO;
import pt.aubay.testesproject.models.dto.SolvedTestDTO;
import pt.aubay.testesproject.models.entities.Answer;
import pt.aubay.testesproject.models.entities.Candidate;
import pt.aubay.testesproject.models.entities.Questions;
import pt.aubay.testesproject.models.entities.SolvedTest;
import pt.aubay.testesproject.models.entities.Test;
import pt.aubay.testesproject.models.statistics.CategoryStatistics;
import pt.aubay.testesproject.models.statistics.SolvedTestStatistics;
import pt.aubay.testesproject.repositories.CandidateRepository;
import pt.aubay.testesproject.repositories.CategoryRepository;
import pt.aubay.testesproject.repositories.SolvedTestRepository;
import pt.aubay.testesproject.repositories.TestRepository;
import pt.aubay.testesproject.repositories.TestSessionRepository;
import pt.aubay.testesproject.services.EmailServices;

public class SolvedTestBusiness {
	
	@Inject
	SolvedTestRepository solvedRepository;
	
	@Inject
	TestRepository testRepository;
	
	@Inject
	TestBusiness testBusiness;
	
	@Inject
	AnswerBusiness answerBusiness;
	
	@Inject
	CandidateRepository candidateRepository;
	
	@Inject
	CandidateBusiness candidateBusiness;
	
	@Inject
	EmailServices emailService;
	
	@Inject
	TestSessionBusiness sessionBusiness;
	
	@Inject
	TestSessionRepository sessionRepository;
	
	@Inject
	CategoryRepository categoryRepository;
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////CRUD-Methods//////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//There is no need for an editable solved test
	
	
	///adding solved test from session
	public void add(SolvedTestDTO test, long sessionID){
		
		//we should check if sessionID exists associated with testID
		if(!sessionRepository.checkIfSessionExistsWithTest(sessionID, test.getTestID()))
			throw new NotFoundException("Session not found");
		sessionBusiness.checkIfSessionValid(sessionID, test.getTestID());
		
		//we should remove session -> if valid, remove session and proceed with adding solved test (session no longer needed);
		sessionBusiness.remove(sessionID);
		
		add(test);
	}
	
	@Transactional
	public void add(SolvedTestDTO test){
		//We need to check if SolvedTest object is valid
		checkTestValidToAdd(test);

		SolvedTest solved=new SolvedTest();
		solved=addDTOAsEntity(test);
		
		int score=calculateResult(solved);
		
		//We need to include test object in answer
		List<Answer> answerList=solved.getAnswer();
		for(Answer elem:answerList)
			elem.setTest(solved);
		
		solvedRepository.addEntity(solved);
		
		//Update averageScore of Test
		testBusiness.updateAverageScore(test.getTestID(), score);
		sendEmail(solved);
		
	}
	
	public List<SolvedTestStatistics> getAll(boolean simplified) {
		List<SolvedTestStatistics> allSolved=new ArrayList<SolvedTestStatistics>();
		for(SolvedTest elem:solvedRepository.getAll())
			allSolved.add(convertEntityToStatistics(elem, simplified));
		return allSolved;
	}
	
	public List<SolvedTestStatistics> getAll(){
		return getAll(false);
	}
	
	public List<SolvedTest> getAllEntities(){
		return solvedRepository.getAll();
	}
	

	@Transactional
	public void remove(long id){
		if(!solvedRepository.idExists(id))
			throw new NotFoundException("No such id in database");
		solvedRepository.deleteEntity(id);
	}
	
	public SolvedTestStatistics get(long id){
		if(!solvedRepository.idExists(id))
			throw new NotFoundException("No such id in database");
		SolvedTest solved=solvedRepository.getEntity(id);
		return convertEntityToStatistics(solved);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////Method to determine result////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	
	//change question to questionID;
	public int calculateResult(SolvedTest test) {
		int totalPoints=0;
		double correctPoints=0;
		int percentage;
		Test myTest=testRepository.getEntity(test.getTest().getId());
		Set <Questions> questions=myTest.getQuestions();
		List<Answer> answers=test.getAnswer();
	
		totalPoints=questions.size();
		
//Old way		
//		//Determines the total number of Points
//		for(Questions elem:questions)
//			totalPoints+=(elem.getSolution()).length;
//		
//		//Determines the number of correct answers
//		//We just need to check if Solution array has each element on the answer array.
//		for(Answer elem:answers) {
//			for(int optionGiven: elem.getGivenAnswer())
//				if(elementInArray(optionGiven,elem.getQuestion().getSolution())
//					correctPoints+=1;
//		}
		
		
		for(Answer elem:answers) {
			double addPoints=0;
			for(int optionGiven: elem.getGivenAnswer()) {
				if(elementInArray(optionGiven,elem.getQuestion().getSolution()))
					addPoints+=1./elem.getQuestion().getSolution().length;
				else {
					addPoints=0; break;
				}
			}
			correctPoints+=addPoints;
		}

		//Determines percentage (as int)
		percentage=(int)Math.round(100.0*correctPoints/(totalPoints));
		
		//Saves info
		test.setScore(percentage);
		
		return percentage;
	}
	
	public int calculateResult(SolvedTest test, String category) {
		int totalPoints=0;
		double correctPoints=0;
		int percentage;
		List<Answer> answers=test.getAnswer();
		
		for(Answer elem:answers) {
			if(elem.getQuestion().getCategory().getCategory().equals(category)) {
				double addPoints=0;
				totalPoints+=1;
				for(int optionGiven: elem.getGivenAnswer()) {
					if(elementInArray(optionGiven,elem.getQuestion().getSolution()))
						addPoints+=1./elem.getQuestion().getSolution().length;
					else {
						addPoints=0; break;
					}
				}
				correctPoints+=addPoints;
			}
		}

		//Determines percentage (as int)
		percentage=(int)Math.round(100.0*correctPoints/(totalPoints));
		
		return percentage;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////Checking-Methods//////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void checkTestValidToAdd(SolvedTestDTO test){
		//First, we need to check if all parameters needed were introduced
		checkIfParametersThere(test);
		//Then, we need to check if test ID related to solved test exists
		if(!testRepository.idExists(test.getTestID()))
			throw new NotFoundException("Test ID does not match any test in database");
		
		//We need to check if both candidate and testID are new (the combination must be unique)
		
		CandidateDTO candidate=test.getCandidate();
		System.out.println(candidate);
		
		Candidate databaseCandidate;
		//We check if candidate exists in database
		System.out.println(candidate.getEmail());
		if(	candidateRepository.CandidateExists(candidate.getEmail())) {
			//Then we get candidate
			databaseCandidate=candidateRepository.getCandidate(candidate.getEmail());
			//Next, we check if Candidate_ID and test_ID are both in a solved test belonging to the SolvedTest Repository
			System.out.println(databaseCandidate.getId());
			System.out.println(test.getTestID());
			if(solvedRepository.checkUniqueness(databaseCandidate.getId(), test.getTestID())) {
				throw new NotAcceptableException("Candidate already took this test.");
			}
		}
		
		//if(solvedRepository.testExists(test.getTestName()))
		//	return Response.status(Status.NOT_ACCEPTABLE).entity("SolvedTest Name exists already").build();
	}
	
	public void checkIfParametersThere(SolvedTestDTO test, boolean needID) {
		if(needID && test.getId()==0)
			throw new NotAcceptableException("Fields must be all present, including ID.");
		if(	test.getAnswer()==null ||
			test.getCandidate()==null ||
			test.getTestID()==0 //&&
			//test.getTimeSpent()!=null
			)
			throw new NotAcceptableException("Fields must be all present.");
		
		//We also need to check if candidate has all needed info
		candidateBusiness.checkIfParametersThere(test.getCandidate());
	}
	
	public void checkIfParametersThere(SolvedTestDTO test){
		checkIfParametersThere(test, false);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////DTO-ENTITY CONVERSION/////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Transactional
	public SolvedTestDTO convertEntityToDTO(SolvedTest solved, boolean simplified) {
		SolvedTestDTO solvedDTO=new SolvedTestDTO();

		if(!simplified) {
			List<Answer> answerList=solved.getAnswer();
			List<AnswerDTO> answerDTOList=new ArrayList<AnswerDTO>();
		//we won't need to know the actual answers in the data tables.

			for(Answer elem:answerList)
				answerDTOList.add(answerBusiness.convertEntityToDTO(elem));
			solvedDTO.setAnswer(answerDTOList);
		}
		
		solvedDTO.setCandidate(candidateBusiness.convertEntityToDTO(solved.getCandidate()));
		
		//When we pass from an Entity to a DTO, we need to set Date as a string with the following format
		DateTimeFormatter formatter =DateTimeFormatter.ofPattern("dd-MM-yyyy");
		String dateTimeString=solved.getDate().format(formatter);
		
		solvedDTO.setDate(dateTimeString);
		solvedDTO.setScore(solved.getScore());
		solvedDTO.setTimeSpent(solved.getTimeSpent());
		solvedDTO.setId(solved.getId());
		solvedDTO.setTestName(solved.getTest().getTestName());
		//Notice that we only send the test ID to the front-end to avoid unnecessary parameters
		solvedDTO.setTestID(solved.getTest().getId());
		
		return solvedDTO;
	}
	
	public SolvedTestDTO convertEntityToDTO(SolvedTest solved) {
		return convertEntityToDTO(solved, false);
	}
	
	//We won't need a convertDTOToEntity in edit, due to the fact that the solved test is not editable
	
	public SolvedTest addDTOAsEntity(SolvedTestDTO solvedDTO) {
		SolvedTest solved = new SolvedTest();
		
		List<AnswerDTO> answerDTOList=solvedDTO.getAnswer();
		List<Answer> answerEntityList=new ArrayList<Answer>();
		
		//We need to convert the answer-DTO list into an answer-Entity list
		for(AnswerDTO elem:answerDTOList)
			answerEntityList.add(answerBusiness.convertDTOToEntity(elem));
		solved.setAnswer(answerEntityList);
		
		solved.setCandidate(candidateBusiness.addDTOAsEntity(solvedDTO.getCandidate()));
		
		//Saves current time;
		setDate(solved);
		solved.setTimeSpent(solvedDTO.getTimeSpent());
		//Notice that we only send the test ID to the front-end to avoid unnecessary parameters
		solved.setTest(testRepository.getEntity(solvedDTO.getTestID()));
		
		return solved;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////DTO-STATISTICS CONVERSION/////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Transactional
	public SolvedTestStatistics convertEntityToStatistics(SolvedTest solvedTest, boolean simplified) {
		
		SolvedTestStatistics solvedStatistics=new SolvedTestStatistics();
		solvedStatistics.setSolvedTest(convertEntityToDTO(solvedTest, simplified));
		
		if(!simplified) {
			Set<CategoryStatistics> categoryStatisticsSet= new HashSet<CategoryStatistics>();
			Set<String> testCategories=testBusiness.getCategories(solvedTest.getTest().getId());
			
			//get scores for each categories of a solved test
			for(String category: testCategories) {
				CategoryStatistics categoryStatistics=new CategoryStatistics();
				categoryStatistics.setScore(calculateResult(solvedTest,category));
				categoryStatistics.setCategory(categoryRepository.getCategory(category));
				categoryStatisticsSet.add(categoryStatistics);
			}
			
			solvedStatistics.setCategoryStatistics(categoryStatisticsSet);
		}
		
		return solvedStatistics;
	}
	
	public SolvedTestStatistics convertEntityToStatistics(SolvedTest solvedTest) {
		return convertEntityToStatistics(solvedTest, false);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////Auxiliary-Methods/////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void setDate(SolvedTest test) {
		LocalDateTime date=LocalDateTime.now();
		test.setDate(date);
	}
	
	public boolean elementInArray(int element, int[] array) {
		for(int i:array)
			if(element==i)
				return true;
		return false;
	}
	
	public void sendEmail(SolvedTest test) {
		MyEmail myEmail=new MyEmail();
		String text="O resultado do aluno "+test.getCandidate().getName()+" foi: "+test.getScore()+"%.\n"+
		"Consulte a plataforma para mais informações.";
		myEmail.setSubject("Resultado do aluno "+test.getCandidate().getName());
		myEmail.setEmailTo(test.getCandidate().getRecruiter().getEmail());
		myEmail.setBody(text);
		/*try {
			emailService.sendEmail(myEmail);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	
	public boolean lessOrEqualsNumberOfDays(long numberOfDays, SolvedTest test) {
		
		LocalDateTime testDate=test.getDate();
		LocalDateTime nowInstant = LocalDateTime.now();
		Duration duration= Duration.between(testDate, nowInstant);
		long durationDiff=Math.abs(duration.toMillis());
		
		return durationDiff<numberOfDays*24*60*60*1000 ? true : false;
	}
	
	public Comparator<SolvedTest> comparator(String comparator){
		Comparator<SolvedTest> sortingByDate=(SolvedTest s1, SolvedTest s2)->s1.getDate().compareTo(s2.getDate());
		Comparator<SolvedTest> sortingByScore=(SolvedTest s1, SolvedTest s2)->s2.getScore()-s1.getScore();
		Comparator<SolvedTest> sortingByTestName=(SolvedTest s1, SolvedTest s2)->s1.getTest().getTestName().compareTo(s2.getTest().getTestName());
		switch(comparator) {
			case "date":
				return sortingByDate;
			case "score":
				return sortingByScore;
			default:
				return sortingByTestName;
		}
	}
	
}
