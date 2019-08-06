package pt.aubay.testesproject.business;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import pt.aubay.testesproject.models.dto.SolvedTestDTO;
import pt.aubay.testesproject.models.entities.Answer;
import pt.aubay.testesproject.models.entities.Questions;
import pt.aubay.testesproject.models.entities.SolvedTest;
import pt.aubay.testesproject.models.entities.Test;
import pt.aubay.testesproject.repositories.SolvedTestRepository;
import pt.aubay.testesproject.repositories.TestRepository;


public class SolvedTestBusiness {
	
	@Inject
	SolvedTestRepository solvedRepository;
	
	@Inject
	TestRepository testRepository;
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////CRUD-Methods//////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//There is no need for an editable solved test
	
	public Response add(SolvedTest test){
		//We need to check if SolvedTest object is valid
		Response response=checkTestValidToAdd(test);
		if(response.getStatus()!=Response.Status.OK.getStatusCode())
			return response;
		//Saves current time;
		setDate(test);
		
		solvedRepository.addEntity(test);
		return Response.ok().entity("Success").build();
	}
	
	public Response getAll() {
		return Response.ok(solvedRepository.getAll(), MediaType.APPLICATION_JSON).build();
	}
	
	public Response remove(SolvedTest test) {
		if(!solvedRepository.idExists(test.getId()))
			return Response.status(Status.NOT_FOUND).entity("No such id in database").build();	
		solvedRepository.deleteEntity(test.getId());
		return Response.ok().entity("Success").build();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////Method to determine result////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	
	//change question to questionID;
	public int calculateResult(SolvedTest test) {
		int totalPoints=0;
		int correctPoints=0;
		int percentage;
		Test myTest=testRepository.getEntity(test.getId());
		Set <Questions> questions=myTest.getQuestions();
		List<Answer> answers=test.getAnswer();
		
		//Determines the total number of Points
		for(Questions elem:questions)
			totalPoints+=(elem.getSolution()).length;
		
		//Determines the number of correct answers
		//We just need to check if Solution array has each element of the answer array.
		for(Answer elem:answers) {
			for(int optionGiven: elem.getGivenAnswer())
				if(IntStream.of(elem.getQuestion().getSolution()).anyMatch(x->x==optionGiven))
					correctPoints+=1;
		}

		//Determines percentage (as int)
		percentage=(int)((double)correctPoints/(totalPoints));
		return percentage;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////Checking-Methods//////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public Response checkTestValidToAdd(SolvedTest test) {
		//First, we need to check if all parameters needed were introduced
		if(checkIfParametersThere(test).getStatus()!=Response.Status.OK.getStatusCode())
			return checkIfParametersThere(test);
		//We need to check if both candidate and testID are new (the combination must be unique)
		//if(solvedRepository.testExists(test.getTestName()))
		//	return Response.status(Status.NOT_ACCEPTABLE).entity("SolvedTest Name exists already").build();
		return Response.ok().entity("Success").build();
	}
	
	public Response checkIfParametersThere(SolvedTest test, boolean needID) {
		if(needID && test.getId()==0)
			return Response.status(Status.NOT_ACCEPTABLE).entity("Fields must be all present, including ID.").build();
		if(	test.getAnswer()!=null &&
			test.getCandidate()!=null &&
			test.getTest()!=null &&
			test.getTimeSpent()!=null)
			return Response.ok().entity("Success").build();
		return Response.status(Status.NOT_ACCEPTABLE).entity("Fields must be all present.").build();
	}
	
	public Response checkIfParametersThere(SolvedTest test) {
		return checkIfParametersThere(test, false);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////DTO-ENTITY CONVERSION/////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public SolvedTestDTO convertEntityToDTO(SolvedTest solved) {
		SolvedTestDTO solvedDTO=new SolvedTestDTO();
		
		solvedDTO.setAnswer(solved.getAnswer());
		solvedDTO.setCandidate(solved.getCandidate());
		solvedDTO.setDate(solved.getDate());
		solvedDTO.setScore(solved.getScore());
		solvedDTO.setTimeSpent(solved.getTimeSpent());
		//Notice that we only send the test ID to the front-end to avoid unnecessary parameters
		solvedDTO.setTestID(solved.getTest().getId());
		
		return solvedDTO;
	}
	
	//We won't need a convertDTOToEntity in edit, due to the fact that the solved test is not editable
	
	public SolvedTest addDTOAsEntity(SolvedTestDTO solvedDTO) {
		SolvedTest solved = new SolvedTest();
		solved.setAnswer(solvedDTO.getAnswer());
		solved.setCandidate(solvedDTO.getCandidate());
		solved.setDate(solvedDTO.getDate());
		solved.setScore(solvedDTO.getScore());
		solved.setTimeSpent(solvedDTO.getTimeSpent());
		//Notice that we only send the test ID to the front-end to avoid unnecessary parameters
		solved.setTest(testRepository.getEntity(solvedDTO.getTestID()));
		return solved;
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////Auxiliary-Methods/////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void setDate(SolvedTest test) {
		LocalDateTime date=LocalDateTime.now();
		test.setDate(date);
	}
	
}
