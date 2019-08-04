package pt.aubay.testesproject.business;

import java.util.Date;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

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
		return Response.ok(testRepository.getAll(), MediaType.APPLICATION_JSON).build();
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
	
	public Response checkIfParametersThere(Test test) {
		if(	test.getAuthor()!=null &&
			test.getQuestions()!=null &&
			test.getTimer()!=0 &&
			test.getTestName()!=null)
			return Response.ok().entity("Success").build();
		return Response.status(Status.NOT_ACCEPTABLE).entity("Fields must be all present.").build();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////Auxiliary-Methods/////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void setDate(Test test) {
		Date date=new Date();
		test.setDate(date);
	}
}
