package pt.aubay.testesproject.business;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import pt.aubay.testesproject.models.dto.TestDTO;
import pt.aubay.testesproject.models.entities.Questions;
import pt.aubay.testesproject.models.entities.Test;
import pt.aubay.testesproject.models.sessions.TestSession;
import pt.aubay.testesproject.models.sessions.TestSessionDTO;
import pt.aubay.testesproject.repositories.RegisteredUserRepository;
import pt.aubay.testesproject.repositories.TestRepository;
import pt.aubay.testesproject.repositories.TestSessionRepository;

@Transactional
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

	public long add(TestSession session, long testID){
		//check if e-mail exists and idTest exists
		checkParameters(session, testID);
		///set creation instance
		setDate(session);
		session.setTest(testRepository.getEntity(testID));
		session=sessionRepository.addSession(session);
		return session.getId();
	}
	
	public TestSessionDTO get(long sessionID) {

		if(!sessionRepository.IDExists(sessionID))
			throw new NotFoundException("Session not found in Database");
		//check if session is expired
		if(!testCommonBusiness.checkIfSessionValid(sessionID))
			throw new BadRequestException("Session expired");
		TestSession session=sessionRepository.getEntity(sessionID);
		
		TestSessionDTO sessionDTO = convertEntityToDTO(session);
		
		sessionDTO.setTest(testBusiness.removeSolution(sessionDTO.getTest()));
		return sessionDTO;
	}
	

	public void begin(long sessionID) {
		if(!sessionRepository.IDExists(sessionID))
			throw new NotFoundException("Session not found in Database");
		if(!testCommonBusiness.checkIfSessionValid(sessionID))
			throw new BadRequestException("Session expired");
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

	public void remove(long sessionID) {
		//check if session exists
		if(!sessionRepository.IDExists(sessionID))
			throw new NotFoundException("Session not found in database");
		sessionRepository.deleteEntity(sessionID);
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////Check-Methods//////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void checkParameters(TestSession session, long testID) {
		
		///check if all needed parameters are there
		if(session.getRecruiterEmail()==null || session.getCandidateEmail()==null)
			throw new NotAcceptableException("Parameters are missing.");
					
		///check if parameters are valid
		if(!testRepository.idExists(testID))
			throw new NotAcceptableException("Test not found in database.");
		if(!userRepository.emailExists(session.getRecruiterEmail()))
			throw new NotAcceptableException("User not found in database.");
	}
	
	public void checkParameters(long sessionID, long testID) {
		
		///check if parameters are valid
		if(!testRepository.idExists(testID))
			throw new NotAcceptableException("Test not found in database.");
		if(!userRepository.emailExists(sessionRepository.getEntity(sessionID).getRecruiterEmail()))
			throw new NotAcceptableException("User not found in database.");
	}
	
	
	//when solvedTest is being submitted, we should first check if session is still valid
	public void checkIfSessionValid(long sessionID, long testID){
		
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
			throw new BadRequestException("Session expired");
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
