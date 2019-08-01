package pt.aubay.testesproject.business;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import pt.aubay.testesproject.models.dto.RegisteredUserDTO;
import pt.aubay.testesproject.models.entities.RegisteredUser;
import pt.aubay.testesproject.repositories.RegisteredUserRepository;
import pt.aubay.testesproject.utils.PasswordUtils;

public class RegisteredUserBusiness {
	@Inject
	RegisteredUserRepository userRepository;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////CRUD-Methods//////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
	public Response add(RegisteredUserDTO userDTO){
		
		String username=userDTO.getUsername();
		String password=userDTO.getPassword();
		String email=userDTO.getEmail();
		String accessType=userDTO.getAccessType();
		RegisteredUser user=new RegisteredUser();
		
		
		if(!checkIfUsernameExists(username)) {
			//password->(hash, salt)
			String[] hashCode=passwordToHashcode(password);
			
			//set Atributos para um Entity
			user.setUsername(username);
			user.setHashcode(hashCode[0]);
			user.setSalt(hashCode[1]);
			user.setEmail(email);
			user.setAccesstype(accessType);
			
			//Adicionar entity ao repositório
			userRepository.addEntity(user);
			return Response.ok().entity("Success").build();
		}
		return Response.status(Status.FORBIDDEN).entity("This username exists already").build();
	}
	
	public Response getAllUsers() {
		return Response.ok(userRepository.getAll(), MediaType.APPLICATION_JSON).build();
	}
	
	public Response get(String usernameOrEmail, String password){
		RegisteredUserDTO userDTO=new RegisteredUserDTO();
		String type=userRepository.isUsernameOrEmail(usernameOrEmail);
		if(type.equals("email")) {
			userDTO.setUsername(userRepository.getUsernameByEmail(usernameOrEmail));
		}
		else
			userDTO.setUsername(usernameOrEmail);
		userDTO.setPassword(password);
		
		Response response=checkIfUserValid(userDTO);
		if(response.getStatus()!=Response.Status.OK.getStatusCode())
			return response;
		return Response.ok(userRepository.getUser(userDTO.getUsername()), MediaType.APPLICATION_JSON).build();
	}
	
	public Response changePassword(String username, String oldPassword, String newPassword) {
		//Create DTO with pass e username
		RegisteredUserDTO userDTO= new RegisteredUserDTO();
		userDTO.setUsername(username);
		userDTO.setPassword(oldPassword);
		
		Response response=checkIfUserValid(userDTO);
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
	
	public Response checkIfPasswordValid(RegisteredUserDTO userDTO) {
		RegisteredUser myUser=userRepository.getUser(userDTO.getUsername());
		String key=myUser.getHashcode();
		String salt=myUser.getSalt();
		
		if(!PasswordUtils.verifyPassword(userDTO.getPassword(), key, salt))
			return Response.status(Status.FORBIDDEN).entity("Invalid Password").build();
		return Response.ok().entity("Success").build();
	}
	
	public boolean checkIfUsernameExists(String username) {
		return userRepository.userExists(username);
	}
	
	public Response checkIfUserValid(RegisteredUserDTO userDTO) {
		Response response=checkIfUsernameValid(userDTO.getUsername());
		if(response.getStatus()!=Response.Status.OK.getStatusCode())
			return response;
		return checkIfPasswordValid(userDTO);
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
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////DTO-ENTITY CONVERSION/////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public RegisteredUserDTO convertEntityToDTO(RegisteredUser user) {
		RegisteredUserDTO userDTO=new RegisteredUserDTO();
		userDTO.setEmail(user.getEmail());
		userDTO.setAccessType(user.getAccesstype());
		userDTO.setId(user.getId());
		userDTO.setUsername(user.getUsername());
		return userDTO;
	}
	
	public RegisteredUser convertDTOToEntity(RegisteredUserDTO userDTO) {
		RegisteredUser user=userRepository.getEntity(userDTO.getId());
		user.setAccesstype(userDTO.getAccessType());
		user.setEmail(userDTO.getEmail());
		user.setUsername(userDTO.getUsername());
		return user;
	}
}
