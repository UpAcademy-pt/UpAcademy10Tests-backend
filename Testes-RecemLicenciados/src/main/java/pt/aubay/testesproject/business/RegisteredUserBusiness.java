package pt.aubay.testesproject.business;

import java.util.ArrayList;

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
		
		Response response=checkParameters(userDTO, true, false);
		if(response.getStatus()!=Response.Status.OK.getStatusCode())
			return response;
		response=checkIfEmailExists(userDTO.getEmail());
		if(response.getStatus()!=Response.Status.OK.getStatusCode())
			return response;
		
		String username=userDTO.getUsername();
		String password=userDTO.getPassword();
		String email=userDTO.getEmail();
		String accessType=userDTO.getAccessType();
		RegisteredUser user=new RegisteredUser();
		
		
		if(!userRepository.userExists(username)) {
			//password->(hash, salt)
			String[] hashCode=passwordToHashcode(password);
			
			//set Atributos para um Entity
			user.setUsername(username); user.setHashcode(hashCode[0]);
			user.setSalt(hashCode[1]); user.setEmail(email);
			user.setAccessType(accessType);
			
			//Adicionar entity ao repositório
			userRepository.addEntity(user);
			return Response.ok().entity("Success").build();
		}
		return Response.status(Status.FORBIDDEN).entity("This username exists already").build();
	}
	
	public Response getAllUsers() {
		ArrayList<RegisteredUserDTO> allUsers=new ArrayList<RegisteredUserDTO>();
		for(RegisteredUser elem:userRepository.getAll())
			allUsers.add(convertEntityToDTO(elem));
		return Response.ok(allUsers, MediaType.APPLICATION_JSON).build();
	}
	
	public Response get(String usernameOrEmail, String password){
		RegisteredUserDTO userDTO=new RegisteredUserDTO();
		//type checks if input is username or email - login might be achieved by both username and email
		String type=isUsernameOrEmail(usernameOrEmail);
		if(type.equals("email"))
			userDTO.setEmail(usernameOrEmail);
		else
			userDTO.setUsername(usernameOrEmail);
		userDTO.setPassword(password);
		//Checks if both username/email and password are valid
		Response response=checkIfUserValid(userDTO,type);
		if(response.getStatus()!=Response.Status.OK.getStatusCode())
			return response;
		//Sets username corresponding to email given
		RegisteredUser user = userRepository.getUser(userDTO.getUsername());
		return Response.ok(convertEntityToDTO(user), MediaType.APPLICATION_JSON).build();
	}
	
	public Response changePassword(String username, String oldPassword, String newPassword) {
		//Creates DTO with pass e username
		RegisteredUserDTO userDTO= new RegisteredUserDTO();
		userDTO.setUsername(username);
		userDTO.setPassword(oldPassword);
		
		//We must check if User is valid (username and old password)
		Response response=checkIfUserValid(userDTO);
		if(response.getStatus()==Response.Status.OK.getStatusCode()) {
			//Changes password
			String[] newHash;
			newHash=passwordToHashcode(newPassword);
			userRepository.changePassword(username, newHash);
			return Response.ok().entity("Success").build();
		}
		return response;
	}
	
	public Response edit(RegisteredUserDTO userDTO) {
		
		Response response=checkParameters(userDTO,false,true);
		if(response.getStatus()!=Response.Status.OK.getStatusCode())
			return response;
		
		response=checkIfChangesValid(userDTO);
		if(response.getStatus()!=Response.Status.OK.getStatusCode())
			return response;
		
		RegisteredUser updatedUser=convertDTOToEntity(userDTO);
		userRepository.editEntity(updatedUser);
		return Response.ok(updatedUser, MediaType.APPLICATION_JSON).build();
	}
	
	public Response remove(RegisteredUserDTO userDTO) {
		
		///To do afterwards: when session is achieved -> check if admin is deleting own account (must be avoided)
		
		if(userDTO.getId()==0 || !(userRepository.userExists(userDTO.getId())))
			return Response.status(Status.FORBIDDEN).entity("Invalid ID").build();
		userRepository.deleteEntity(userDTO.getId());
		return Response.ok().entity("Success").build();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////Checking-Methods//////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public Response checkIfUsernameValid(String username) {
		if(!userRepository.userExists(username))
			return Response.status(Status.NOT_FOUND).entity("No such user in database").build();
		return Response.ok().entity("Success").build();
	}
	
	public Response checkIfEmailExists(String email) {
		if(userRepository.emailExists(email))
			return Response.status(Status.FORBIDDEN).entity("Email already exists.").build();
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
	
	public Response checkIfUsernameExists(String username) {
		if(userRepository.userExists(username))
			return Response.status(Status.FORBIDDEN).entity("Username already exists.").build();
		return Response.ok().entity("Success").build();
	}
	
	public Response checkIfUserValid(RegisteredUserDTO userDTO, String type) {
		//User valid if both username and password are valid
		Response response=Response.ok().entity("Success").build();
		if(type=="username")
			response=checkIfUsernameValid(userDTO.getUsername());
		if(type=="email") {
			if(!userRepository.emailExists(userDTO.getEmail()))
				response=Response.status(Status.NOT_FOUND).entity("No such email in database").build();
			//our checks use username, so if e-mail exists in database the username is retrieved via the corresponding e-mail
			else
				userDTO.setUsername(userRepository.getUsernameByEmail(userDTO.getEmail()));
		}
		if(response.getStatus()!=Response.Status.OK.getStatusCode())
			return response;
		return checkIfPasswordValid(userDTO);
	}
	
	public Response checkIfUserValid(RegisteredUserDTO userDTO) {
		return checkIfUserValid(userDTO, "username");
	}
	
	public Response checkParameters(RegisteredUserDTO userDTO, boolean needPassword, boolean needID) {
		if(needPassword==true && userDTO.getPassword()==null)
			return Response.status(Status.FORBIDDEN).entity("Invalid User parameters. A password is needed to continue operation.").build();
		if(needPassword==false && userDTO.getPassword()!=null)
			return Response.status(Status.FORBIDDEN).entity("Invalid User parameters. A password was inserted.").build();
		//Note: password should not be sent when editing - there is a special function to do so
		if(userDTO.getEmail()==null || userDTO.getUsername()==null ||userDTO.getAccessType()==null)
			return Response.status(Status.FORBIDDEN).entity("Invalid User parameters. Check if all parameters were inserted").build();
		if(needID==true && !userRepository.userExists(userDTO.getId()))
			return Response.status(Status.FORBIDDEN).entity("Invalid ID").build();
		return Response.ok().entity("Success").build();
	}
	
	public Response checkIfChangesValid(RegisteredUserDTO newUser) {
		RegisteredUser oldUser=userRepository.getEntity(newUser.getId());
		if(!newUser.getUsername().equals(oldUser.getUsername()))
			return checkIfUsernameExists(newUser.getUsername());
		if(!newUser.getEmail().equals(oldUser.getEmail()))
			return checkIfEmailExists(newUser.getEmail());
		return Response.ok().entity("Success").build();
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
		userDTO.setAccessType(user.getAccessType());
		userDTO.setId(user.getId());
		userDTO.setUsername(user.getUsername());
		return userDTO;
	}
	
	public RegisteredUser convertDTOToEntity(RegisteredUserDTO userDTO) {
		RegisteredUser user=userRepository.getEntity(userDTO.getId());
		user.setAccessType(userDTO.getAccessType());
		user.setEmail(userDTO.getEmail());
		user.setUsername(userDTO.getUsername());
		return user;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////AUXILIARY METHODS/////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////~
	
	public String isUsernameOrEmail(String usernameOrEmail) {
		return usernameOrEmail.indexOf('@')!=-1 ? "email" : "username";
	}
}
