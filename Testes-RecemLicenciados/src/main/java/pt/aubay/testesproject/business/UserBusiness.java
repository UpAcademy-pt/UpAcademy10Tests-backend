package pt.aubay.testesproject.business;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import pt.aubay.testesproject.auxiliary.TempUser;
import pt.aubay.testesproject.models.User;
import pt.aubay.testesproject.repositories.UserRepositories;
import pt.aubay.testesproject.utils.PasswordUtils;;

public class UserBusiness {
	@Inject
	UserRepositories userRepository;
	
	public String healthCheck(UriInfo context) {
		return "URI " + context.getRequestUri().toString() + " is OK!";
	}
		
	public Response add(TempUser tempUser){
		String username=tempUser.getUsername();
		String password=tempUser.getPassword();
		String [] pass;
		User user=new User();
		boolean checkIfExist=checkIfUsernameExists(username);
		if(checkIfExist==false) {
			user.setUsername(username);
			user.setHashPass(passwordToHashcode(password)[0]);
			user.setSalt(passwordToHashcode(password)[1]);
			userRepository.addEntity(user);
			return Response.ok().entity("Success").build();
		}
		return Response.status(Status.FORBIDDEN).entity("Este username já existe").build();
	}
	
	public Response getAllUsers() {
		return Response.ok(userRepository.getAll(), MediaType.APPLICATION_JSON).build();
	}
	
	public Response get(TempUser tempUser){
		Response response=checkIfUserValid(tempUser);
		if(response.getStatus()!=Response.Status.OK.getStatusCode())
			return response;
		return Response.ok(userRepository.getUser(tempUser.getUsername()), MediaType.APPLICATION_JSON).build();
	}
	
	public Response checkIfUsernameValid(String username) {
		if(checkIfUsernameExists(username)==false)
			return Response.status(Status.NOT_FOUND).entity("Não existe na base de dados").build();
		return Response.ok().entity("Success").build();
	}
	
	public boolean checkIfUsernameExists(String username) {
		return userRepository.userExists(username);
	}
	
	public Response checkIfUserValid(TempUser tempUser) {
		Response response=checkIfUsernameValid(tempUser.getUsername());
		if(response.getStatus()!=Response.Status.OK.getStatusCode())
			return response;
		
		User myUser=userRepository.getUser(tempUser.getUsername());
		String key=myUser.getHashPass();
		String salt=myUser.getSalt();
		
		if(!PasswordUtils.verifyPassword(tempUser.getPassword(), key, salt))
			return Response.status(Status.FORBIDDEN).entity("Palavra-passe inválida").build();
		return Response.ok().entity("Success").build();
	}
	
	public String[] passwordToHashcode(String password) {
		String salt = PasswordUtils.generateSalt(50).get();
		String key = PasswordUtils.hashPassword(password, salt).get();
		String[] result= {key, salt};
		return result;
	}
	
}
