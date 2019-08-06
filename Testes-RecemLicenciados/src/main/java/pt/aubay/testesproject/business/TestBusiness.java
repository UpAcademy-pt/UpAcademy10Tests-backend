package pt.aubay.testesproject.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import pt.aubay.testesproject.models.dto.RegisteredUserDTO;
import pt.aubay.testesproject.models.entities.RegisteredUser;
import pt.aubay.testesproject.models.entities.Test;
import pt.aubay.testesproject.repositories.TestRepository;

public class TestBusiness {
	@Inject
	TestRepository testRepository;
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////CRUD-Methods//////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public Response add(Test test){
		//We need to check if test object is valid
		Response response=checkTestValidToAdd(test);
		if(response.getStatus()!=Response.Status.OK.getStatusCode())
			return response;
		//Saves current time;
		setDate(test);
		
		testRepository.addEntity(test);
		return Response.ok().entity("Success").build();
	}
	
	public Response getAll() {
		/*Set<Test> allTest=new HashSet();
		for(Test elem:testRepository.getAll())
			allTest.add(convertEntityToDTO(elem));
		return Response.ok(allTest, MediaType.APPLICATION_JSON).build();*/
		
		return Response.ok(testRepository.getAll(), MediaType.APPLICATION_JSON).build();
	}
	
	
	public Response edit(Test newTest) {
		Response response=checkTestValidToEdit(newTest);
		if(response.getStatus()!=Response.Status.OK.getStatusCode())
			return response;
		//We also need to reset back-end-determined values (Date and Average Score)
		resetValues(newTest);
		testRepository.editEntity(newTest);
		return Response.ok().entity("Success").build();
	}
	
	public Response remove(Test test) {
		if(!testRepository.idExists(test.getId()))
			return Response.status(Status.NOT_FOUND).entity("No such id in database").build();	
		testRepository.deleteEntity(test.getId());
		return Response.ok().entity("Success").build();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////Checking-Methods//////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public Response checkTestValidToAdd(Test test) {
		//First, we need to check if all parameters needed were introduced
		if(checkIfParametersThere(test).getStatus()!=Response.Status.OK.getStatusCode())
			return checkIfParametersThere(test);
		//We need to check if the test name is new (must be unique)
		if(testRepository.testExists(test.getTestName()))
			return Response.status(Status.NOT_ACCEPTABLE).entity("Test Name exists already").build();
		return Response.ok().entity("Success").build();
	}
	
	public Response checkTestValidToEdit(Test newTest) {
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
	
	public Response checkIfParametersThere(Test test, boolean needID) {
		if(needID && test.getId()==0)
			return Response.status(Status.NOT_ACCEPTABLE).entity("Fields must be all present, including ID.").build();
		if(	test.getAuthor()!=null &&
			test.getQuestions()!=null &&
			test.getTimer()!=0 &&
			test.getTestName()!=null)
			return Response.ok().entity("Success").build();
		return Response.status(Status.NOT_ACCEPTABLE).entity("Fields must be all present.").build();
	}
	
	public Response checkIfParametersThere(Test test) {
		return checkIfParametersThere(test, false);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////Auxiliary-Methods/////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void setDate(Test test) {
		Date date=new Date();
		test.setDate(date);
	}
	
	public void resetValues(Test test) {
		setDate(test);
		test.setAverageScore(0);
	}
}
