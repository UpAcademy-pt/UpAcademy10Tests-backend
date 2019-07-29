package pt.aubay.testesproject.business;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import pt.aubay.testesproject.models.User;
import pt.aubay.testesproject.repositories.UserRepositories;
import pt.aubay.testesproject.utils.PasswordUtils;;

public class UserBusiness {
	@Inject
	UserRepositories userRepository;
	
	public Response add(String username, String password){
		String [] pass;
		User user=new User();
		Response response=checkIfUsernameValid(username);
		if(response==Response.ok().entity("Success").build()) {
			user.setUsername(username);
			user.setPassword(passwordToHashcode(password));
			userRepository.addEntity(user);
		}
		return response;
	}
	
	public Response get(String username, String password){
		Response response=checkIfUserValid(username, password);
		if(response!=Response.ok().entity("Success").build())
			return response;
		return Response.ok(userRepository.getUser(username), MediaType.APPLICATION_JSON).build();
	}
	
	public Response checkIfUsernameValid(String username) {
		if(userRepository.getUser(username)==null)
			return Response.status(Status.NOT_FOUND).entity("Não existe na base de dados").build();
		return Response.ok().entity("Success").build();
	}
	
	public Response checkIfUserValid(String username, String password) {
		User myUser=userRepository.getUser(username);
		String[] hashCode=myUser.getPassword();
		String key=hashCode[0];
		String salt=hashCode[1];
		Response response=checkIfUsernameValid(username);
		if(response!=Response.ok().entity("Success").build())
			return response;
		if(!PasswordUtils.verifyPassword(password, key, salt))
			return Response.status(Status.FORBIDDEN).entity("Palavra-passe inválida").build();
		return Response.ok().entity("Success").build();
	}
	
	public String[] passwordToHashcode(String password) {
		String salt = PasswordUtils.generateSalt(512).get();
		String key = PasswordUtils.hashPassword(password, salt).get();
		String[] result= {key, salt};
		return result;
	}
	
}
