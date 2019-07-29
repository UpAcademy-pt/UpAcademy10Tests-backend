package pt.aubay.testesproject.business;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import pt.aubay.testesproject.models.User;
import pt.aubay.testesproject.utils.PasswordUtils;;

public class UserBusiness {
	@Inject
	UserRepository userRepository;
	
	public Response add(String username, String password){
		String [] pass;
		Response response=checkIfUsernameValid(username);
		if(response==Response.ok().entity("Success").build()) {
			User user;
			user.setUsername(username);
			user.setPassword(passwordToHashcode(password));
			userRespository.addEntity(user);
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
		if(userRepository.getUser(username)==null)
			return Response.status(Status.NOT_FOUND).entity("Não existe na base de dados").build();
		return Response.ok().entity("Success").build();
	}
	
	public String[] passwordToHashcode(String password) {
		String salt = PasswordUtils.generateSalt(512).get();
		String key = PasswordUtils.hashPassword(password, salt).get();
		String[] result= {key, salt};
		return result;
	}
	
}
