package pt.aubay.testesproject.business;

import java.util.Date;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import pt.aubay.testesproject.models.entities.SolvedTest;
import pt.aubay.testesproject.repositories.SolvedTestRepository;


public class SolvedTestBusiness {
	
	@Inject
	SolvedTestRepository solvedRepository;
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
	//////////////////////////////////////////Auxiliary-Methods/////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void setDate(SolvedTest test) {
		Date date=new Date();
		test.setDate(date);
	}
	
}
