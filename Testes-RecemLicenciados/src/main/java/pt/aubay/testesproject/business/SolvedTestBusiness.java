package pt.aubay.testesproject.business;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import javax.inject.Inject;
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
import pt.aubay.testesproject.models.statistics.SolvedTestStatistics;
import pt.aubay.testesproject.repositories.CandidateRepository;
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
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////CRUD-Methods//////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//There is no need for an editable solved test
	
	
	///adding solved test from session
	public Response add(SolvedTestDTO test, long sessionID) {
		
		//we should check if sessionID exists associated with testID
		if(!sessionRepository.checkIfSessionExistsWithTest(sessionID, test.getTestID()))
			return Response.status(Status.NOT_FOUND).entity("Session not found").build();	 
		Response response=sessionBusiness.checkIfSessionValid(sessionID, test.getTestID());
		
		//we should remove session -> if valid, remove session and proceed with adding solved test (session no longer needed); if not, just remove session
		sessionBusiness.remove(sessionID);
		
		if(response.getStatus()!=Response.Status.OK.getStatusCode())
			return response;
		
		return add(test);
	}
	
	public Response add(SolvedTestDTO test){
		//We need to check if SolvedTest object is valid
		Response response=checkTestValidToAdd(test);
		if(response.getStatus()!=Response.Status.OK.getStatusCode())
			return response;
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
		
		return Response.ok().entity("Success").build();
	}
	
	public Response getAll() {
		ArrayList<SolvedTestDTO> allSolved=new ArrayList<SolvedTestDTO>();
		for(SolvedTest elem:solvedRepository.getAll())
			allSolved.add(convertEntityToDTO(elem));
		return Response.ok(allSolved, MediaType.APPLICATION_JSON).build();
	}
	
	public Response remove(long id) {
		if(!solvedRepository.idExists(id))
			return Response.status(Status.NOT_FOUND).entity("No such id in database").build();	
		solvedRepository.deleteEntity(id);
		return Response.ok().entity("Success").build();
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
		percentage=(int)(100.0*correctPoints/(totalPoints));
		
		//Saves info
		test.setScore(percentage);
		
		return percentage;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////Checking-Methods//////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public Response checkTestValidToAdd(SolvedTestDTO test) {
		//First, we need to check if all parameters needed were introduced
		if(checkIfParametersThere(test).getStatus()!=Response.Status.OK.getStatusCode())
			return checkIfParametersThere(test);
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
				return Response.status(Status.NOT_ACCEPTABLE).entity("Candidate already took this test.").build();
			}
		}
		
		//if(solvedRepository.testExists(test.getTestName()))
		//	return Response.status(Status.NOT_ACCEPTABLE).entity("SolvedTest Name exists already").build();
		return Response.ok().entity("Success").build();
	}
	
	public Response checkIfParametersThere(SolvedTestDTO test, boolean needID) {
		if(needID && test.getId()==0)
			return Response.status(Status.NOT_ACCEPTABLE).entity("Fields must be all present, including ID.").build();
		if(	test.getAnswer()==null ||
			test.getCandidate()==null ||
			test.getTestID()==0 //&&
			//test.getTimeSpent()!=null
			)
			return Response.status(Status.NOT_ACCEPTABLE).entity("Fields must be all present.").build();
		
		//We also need to check if candidate has all needed info
		Response response=candidateBusiness.checkIfParametersThere(test.getCandidate());
		if(response.getStatus()!=Response.Status.OK.getStatusCode())
			return response;
		return Response.ok().entity("Success").build();
	}
	
	public Response checkIfParametersThere(SolvedTestDTO test) {
		return checkIfParametersThere(test, false);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////DTO-ENTITY CONVERSION/////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public SolvedTestDTO convertEntityToDTO(SolvedTest solved) {
		SolvedTestDTO solvedDTO=new SolvedTestDTO();
		
		List<Answer> answerList=solved.getAnswer();
		List<AnswerDTO> answerDTOList=new ArrayList<AnswerDTO>();
		for(Answer elem:answerList)
			answerDTOList.add(answerBusiness.convertEntityToDTO(elem));
		solvedDTO.setAnswer(answerDTOList);
		
		
		solvedDTO.setCandidate(candidateBusiness.convertEntityToDTO(solved.getCandidate()));
		
		//When we pass from an Entity to a DTO, we need to set Date as a string with the following format
		DateTimeFormatter formatter =DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
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
	
	public SolvedTestStatistics convertDTOToStatistics(SolvedTestDTO solvedTestDTO) {
		SolvedTestStatistics solvedStatistics=new SolvedTestStatistics();
		solvedStatistics.setSolvedTest(solvedTestDTO);
		return solvedStatistics;
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
	
}
