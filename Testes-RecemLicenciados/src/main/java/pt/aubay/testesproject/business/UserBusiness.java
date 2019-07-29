package pt.aubay.testesproject.business;

import javax.ws.rs.core.Response;

import pt.aubay.models.User;

public class UserBusiness {
	@Inject
	UserRepository userRepository;
	
	public Response add(String username, String password){
		Response response=Response.ok().entity("Success").build();
		if(response.getStatus()==Response.Status.OK.getStatusCode()){
			userRepository.addEntity(user);
		}
		return response;
	}
	
	public Response get(String username, String password){
		Response response=Response.ok().entity("Success").build();
		if(response.getStatus()==Response.Status.OK.getStatusCode()){
			userRepository.addEntity(user);
		}
		return response;
	}
	
	public Response checkIfUsernameValid(String username) {
		//if(getUser(username))
		return null;
	}
	
}
