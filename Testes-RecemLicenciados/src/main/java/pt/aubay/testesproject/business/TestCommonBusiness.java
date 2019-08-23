package pt.aubay.testesproject.business;

import java.time.Duration;
import java.time.LocalDateTime;

import javax.inject.Inject;

import pt.aubay.testesproject.models.sessions.TestSession;
import pt.aubay.testesproject.repositories.TestSessionRepository;

public class TestCommonBusiness {
	///This business was created to avoid circular dependency of injections. The methods here are used by tests, solved tests, and test sessions.
	
	
	@Inject
	TestSessionRepository sessionRepository;
	
	//////////////////// Special Case: Check if session is valid
	//used in many places
	
	public boolean checkIfSessionValid(long sessionID) {
		TestSession session=sessionRepository.getEntity(sessionID);
		LocalDateTime nowInstant = LocalDateTime.now();
		LocalDateTime startingInstant = session.getStartingToken();
		long numberOfDays=session.getNumberOfDays();
		
		Duration duration= Duration.between(startingInstant, nowInstant);
		long durationDiff=Math.abs(duration.toMillis());
		
		if(durationDiff>numberOfDays*24*60*60*1000)
			return false;
		return true;
	}
}
