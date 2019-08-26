package pt.aubay.testesproject.business;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import pt.aubay.testesproject.execptionHandling.AppException;
import pt.aubay.testesproject.models.dto.TestDTO;
import pt.aubay.testesproject.models.entities.Questions;
import pt.aubay.testesproject.models.entities.Test;
import pt.aubay.testesproject.models.sessions.TestSession;
import pt.aubay.testesproject.models.sessions.TestSessionDTO;
import pt.aubay.testesproject.repositories.RegisteredUserRepository;
import pt.aubay.testesproject.repositories.TestRepository;
import pt.aubay.testesproject.repositories.TestSessionRepository;

public class TestSessionBusiness {

	@Inject
	TestSessionRepository sessionRepository;
	
	@Inject
	TestRepository testRepository;
	
	@Inject
	TestBusiness testBusiness;
	
	@Inject
	RegisteredUserRepository userRepository;
	
	@Inject
	TestCommonBusiness testCommonBusiness;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////CRUD-Methods//////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public long add(TestSession session, long testID) throws AppException{
		//check if e-mail exists and idTest exists
		checkParameters(session, testID);
		///set creation instance
		setDate(session);
		session.setTest(testRepository.getEntity(testID));
		session=sessionRepository.addSession(session);
		return session.getId();
	}
	
	public TestSessionDTO get(long sessionID) throws AppException {

		if(!sessionRepository.IDExists(sessionID))
			throw new AppException("Session not found in Database", Status.NOT_FOUND.getStatusCode());
		//check if session is expired
		if(!testCommonBusiness.checkIfSessionValid(sessionID))
			throw new AppException("Session expired", Status.REQUEST_TIMEOUT.getStatusCode());
		TestSession session=sessionRepository.getEntity(sessionID);
		
		TestSessionDTO sessionDTO = convertEntityToDTO(session);
		
		sessionDTO.setTest(testBusiness.removeSolution(sessionDTO.getTest()));
		return sessionDTO;
	}
	
	public long begin(long sessionID) throws AppException {
		if(!sessionRepository.IDExists(sessionID))
			throw new AppException("Session not found in Database", Status.NOT_FOUND.getStatusCode());
		if(!testCommonBusiness.checkIfSessionValid(sessionID))
			throw new AppException("Session expired", Status.REQUEST_TIMEOUT.getStatusCode());
		TestSession session=sessionRepository.getEntity(sessionID);
		
		LocalDateTime startingTest=LocalDateTime.now();
		if(session.getStartingTest()==null)
			session.setStartingTest(startingTest);
		sessionRepository.editEntity(session);
		
		//Determines the interval between starting test and now
		Duration duration=Duration.between(session.getStartingTest(), LocalDateTime.now());
		long durationDiff=Math.abs(duration.toMillis());
		return durationDiff;
	}
	
	public void remove(long sessionID) throws AppException {
		//check if session exists
		if(!sessionRepository.IDExists(sessionID))
			throw new AppException("Session not found in database", Status.NOT_FOUND.getStatusCode());
		sessionRepository.deleteEntity(sessionID);
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////Check-Methods//////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void checkParameters(TestSession session, long testID) throws AppException {
		
		///check if all needed parameters are there
		if(session.getRecruiterEmail()==null || session.getCandidateEmail()==null)
			throw new AppException("Parameters are missing.", Status.NOT_ACCEPTABLE.getStatusCode());
		
		///check if parameters are valid
		if(!testRepository.idExists(testID))
			throw new AppException("Test not found in database", Status.NOT_ACCEPTABLE.getStatusCode());
		if(!userRepository.emailExists(session.getRecruiterEmail()))
			throw new AppException("User not found in database", Status.NOT_ACCEPTABLE.getStatusCode());
	}
	
	public void checkParameters(long sessionID, long testID) throws AppException {
		
		///check if parameters are valid
		if(!testRepository.idExists(testID))
			throw new AppException("Test not found in database", Status.NOT_ACCEPTABLE.getStatusCode());
		if(!userRepository.emailExists(sessionRepository.getEntity(sessionID).getRecruiterEmail()))
			throw new AppException("User not found in database", Status.NOT_ACCEPTABLE.getStatusCode());
	}
	
	
	//when solvedTest is being submitted, we should first check if session is still valid
	public void checkIfSessionValid(long sessionID, long testID) throws AppException {
		
		checkParameters(sessionID, testID);
		
		Test test= testRepository.getEntity(testID);
		TestSession session=sessionRepository.getEntity(sessionID);
		LocalDateTime nowInstant = LocalDateTime.now();
		LocalDateTime startingInstant = session.getStartingTest();
		
		Duration duration= Duration.between(startingInstant, nowInstant);
		long durationDiff=Math.abs(duration.toMillis());
		
		///10 min were added to the session, in order to avoid Internet-speed-related issues
		if(durationDiff>(test.getTimer()+10)*60*1000) {
			remove(sessionID);
			throw new AppException("Session expired", Status.REQUEST_TIMEOUT.getStatusCode());
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////Auxiliary-Methods/////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void setDate(TestSession session) {
		LocalDateTime dateTime=LocalDateTime.now();
		session.setStartingToken(dateTime);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////Convert Entity To DTO/////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public TestSessionDTO convertEntityToDTO(TestSession session) {
		TestSessionDTO sessionDTO = new TestSessionDTO();
		sessionDTO.setRecruiterEmail(session.getRecruiterEmail());
		sessionDTO.setTest(testBusiness.convertEntityToDTO(session.getTest()));
		sessionDTO.setCandidateEmail(session.getCandidateEmail());
		sessionDTO.setSessionID(session.getId());
				
		//one should remove sensitive information
		sessionDTO.getTest().setAuthor(null);
		sessionDTO.getTest().setAverageScore(0);
		return sessionDTO;
	}
}
