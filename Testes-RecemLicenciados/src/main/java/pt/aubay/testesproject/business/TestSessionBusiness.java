package pt.aubay.testesproject.business;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
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
	
	public Response add(TestSession session, long testID){
		//check if e-mail exists and idTest exists
		Response response=checkParameters(session, testID);
		if(response.getStatus()!=Response.Status.OK.getStatusCode())
			return response;
		///set creation instance
		setDate(session);
		session.setTest(testRepository.getEntity(testID));
		session=sessionRepository.addSession(session);
		return Response.ok(session.getId(), MediaType.APPLICATION_JSON).build();
	}
	
	public Response get(long sessionID) {
		//check if session is expired
		if(!testCommonBusiness.checkIfSessionValid(sessionID))
			return Response.status(Status.REQUEST_TIMEOUT).entity("Session expired").build();
		TestSession session=sessionRepository.getEntity(sessionID);
		
		TestSessionDTO sessionDTO = convertEntityToDTO(session);
		
		sessionDTO.setTest(testBusiness.removeSolution(sessionDTO.getTest()));
		return Response.ok(sessionDTO, MediaType.APPLICATION_JSON).build();
	}
	
	public Response begin(long sessionID) {
		if(!testCommonBusiness.checkIfSessionValid(sessionID))
			return Response.status(Status.REQUEST_TIMEOUT).entity("Session expired").build();
		TestSession session=sessionRepository.getEntity(sessionID);
		
		LocalDateTime startingTest=LocalDateTime.now();
		if(session.getStartingTest()==null)
			session.setStartingTest(startingTest);
		sessionRepository.editEntity(session);
		return Response.ok().entity("Success").build();
	}
	
	public Response remove(long sessionID) {
		//check if session exists
		if(!sessionRepository.IDExists(sessionID))
			return Response.status(Status.NOT_FOUND).entity("Session not found in database").build();
		sessionRepository.deleteEntity(sessionID);
		return Response.ok().entity("Success").build();
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////Check-Methods//////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public Response checkParameters(TestSession session, long testID) {
		
		///check if all needed parameters are there
		if(session.getRecruiterEmail()==null)
			return Response.status(Status.NOT_ACCEPTABLE).entity("Parameters are missing.").build();
		
		///check if parameters are valid
		if(!testRepository.idExists(testID))
			return Response.status(Status.NOT_ACCEPTABLE).entity("Test not found in database").build();
		if(!userRepository.emailExists(session.getRecruiterEmail()))
			return Response.status(Status.NOT_ACCEPTABLE).entity("User not found in database").build();
		return Response.ok().entity("Success").build();
	}
	
	public Response checkParameters(long sessionID, long testID) {
		
		///check if parameters are valid
		if(!testRepository.idExists(testID))
			return Response.status(Status.NOT_ACCEPTABLE).entity("Test not found in database").build();
		if(!userRepository.userExists(sessionID))
			return Response.status(Status.NOT_ACCEPTABLE).entity("User not found in database").build();
		return Response.ok().entity("Success").build();
	}
	
	
	//when solvedTest is being submitted, we should first check if session is still valid
	public Response checkIfSessionValid(long sessionID, long testID) {
		
		Response response=checkParameters(sessionID, testID);
		if(response.getStatus()!=Response.Status.OK.getStatusCode())
			return response;
		
		Test test= testRepository.getEntity(testID);
		TestSession session=sessionRepository.getEntity(sessionID);
		LocalDateTime nowInstant = LocalDateTime.now();
		LocalDateTime startingInstant = session.getStartingTest();
		
		Duration duration= Duration.between(startingInstant, nowInstant);
		long durationDiff=Math.abs(duration.toMillis());
		
		///5 min were added to the session, in order to avoid Internet-speed-related issues
		if(durationDiff>(test.getTimer()+5)*60*1000)
			Response.status(Status.REQUEST_TIMEOUT).entity("Session expired").build();
		return Response.ok().entity("Success").build();
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
		sessionDTO.getTest().setAuthor(null);
		return sessionDTO;
	}
}
