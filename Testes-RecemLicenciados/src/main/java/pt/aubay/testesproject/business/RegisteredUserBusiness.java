package pt.aubay.testesproject.business;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import pt.aubay.testesproject.credentials.AddUserCredentials;
import pt.aubay.testesproject.credentials.UserCredentials;
import pt.aubay.testesproject.models.RegisteredUser;
import pt.aubay.testesproject.repositories.RegisteredUserRepository;
import pt.aubay.testesproject.utils.PasswordUtils;

public class RegisteredUserBusiness {
	@Inject
	RegisteredUserRepository userRepository;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////CRUD-Methods//////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
	public Response add(AddUserCredentials userCredentials){
		String username=userCredentials.getUsername();
		String password=userCredentials.getPassword();
		String email=userCredentials.getEmail();
		String accessType=userCredentials.getAccessType();
		RegisteredUser user=new RegisteredUser();
		if(!checkIfUsernameExists(username)) {
			String[] hashCode=passwordToHashcode(password);
			user.setUsername(username);
			user.setHashcode(hashCode[0]);
			user.setSalt(hashCode[1]);
			user.setEmail(email);
			user.setAccesstype(accessType);
			userRepository.addEntity(user);
			return Response.ok().entity("Success").build();
		}
		return Response.status(Status.FORBIDDEN).entity("This username exists already").build();
	}
	
	public Response getAllUsers() {
		return Response.ok(userRepository.getAll(), MediaType.APPLICATION_JSON).build();
	}
	
	public Response get(UserCredentials userCredentials){
		Response response=checkIfUserValid(userCredentials);
		if(response.getStatus()!=Response.Status.OK.getStatusCode())
			return response;
		return Response.ok(userRepository.getUser(userCredentials.getUsername()), MediaType.APPLICATION_JSON).build();
	}
	
	public Response changePassword(String username, String oldPassword, String newPassword) {
		UserCredentials user= new UserCredentials(username, oldPassword);
		Response response=checkIfUserValid(user);
		if(response.getStatus()==Response.Status.OK.getStatusCode()) {
			String[] newHash;
			newHash=passwordToHashcode(newPassword);
			userRepository.changePassword(username, newHash);
			return Response.ok().entity("Success").build();
		}
		return response;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////Checking-Methods//////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public Response checkIfUsernameValid(String username) {
		if(checkIfUsernameExists(username)==false)
			return Response.status(Status.NOT_FOUND).entity("No such user in database").build();
		return Response.ok().entity("Success").build();
	}
	
	public Response checkIfPasswordValid(UserCredentials userCredentials) {
		RegisteredUser myUser=userRepository.getUser(userCredentials.getUsername());
		String key=myUser.getHashcode();
		String salt=myUser.getSalt();
		
		if(!PasswordUtils.verifyPassword(userCredentials.getPassword(), key, salt))
			return Response.status(Status.FORBIDDEN).entity("Invalid Password").build();
		return Response.ok().entity("Success").build();
	}
	
	public boolean checkIfUsernameExists(String username) {
		return userRepository.userExists(username);
	}
	
	public Response checkIfUserValid(UserCredentials userCredentials) {
		Response response=checkIfUsernameValid(userCredentials.getUsername());
		if(response.getStatus()!=Response.Status.OK.getStatusCode())
			return response;
		return checkIfPasswordValid(userCredentials);
	}

	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////Password-Methods//////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public String[] passwordToHashcode(String password) {
		String salt = PasswordUtils.generateSalt(50).get();
		String key = PasswordUtils.hashPassword(password, salt).get();
		String[] result= {key, salt};
		return result;
	}
}
