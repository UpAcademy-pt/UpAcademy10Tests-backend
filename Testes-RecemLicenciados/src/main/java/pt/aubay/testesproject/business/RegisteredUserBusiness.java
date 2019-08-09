package pt.aubay.testesproject.business;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import pt.aubay.testesproject.auxiliary.MyEmail;
import pt.aubay.testesproject.models.dto.RegisteredUserDTO;
import pt.aubay.testesproject.models.entities.RegisteredUser;
import pt.aubay.testesproject.repositories.RegisteredUserRepository;
import pt.aubay.testesproject.services.EmailServices;
import pt.aubay.testesproject.utils.PasswordUtils;

public class RegisteredUserBusiness {
	@Inject
	RegisteredUserRepository userRepository;
	
	@Inject
	EmailServices emailService;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////CRUD-Methods//////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
	public Response add(RegisteredUserDTO userDTO){
		
		Response response=checkParameters(userDTO, false);
		if(response.getStatus()!=Response.Status.OK.getStatusCode())
			return response;
		response=checkIfEmailExists(userDTO.getEmail());
		if(response.getStatus()!=Response.Status.OK.getStatusCode())
			return response;
		
		String username=userDTO.getUsername();
		//String password=userDTO.getPassword();
		String email=userDTO.getEmail();
		String accessType=userDTO.getAccessType();
		RegisteredUser user=new RegisteredUser();
		
		if(!userRepository.userExists(username)) {
			//password->(hash, salt)
			String password;
			try {
				password = generatePassword(userDTO.getEmail(),false);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				password="admin";
				e.printStackTrace();
			}
			String[] hashCode=passwordToHashcode(password);
			
			//set Atributos para um Entity
			user.setUsername(username); user.setHashcode(hashCode[0]);
			user.setSalt(hashCode[1]); user.setEmail(email);
			user.setAccessType(accessType);
			setLastLogin(user);
			user.setAvailable(true);
			
			//Adicionar entity ao repositório
			userRepository.addEntity(user);
			return Response.ok(password, MediaType.APPLICATION_JSON).build();
			//return Response.ok().entity("Success").build();
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
		//Checks if both username/email and password are valid
		Response response=checkIfUserValid(userDTO,password,type);
		if(response.getStatus()!=Response.Status.OK.getStatusCode())
			return response;
		//Sets username corresponding to email given
		RegisteredUser user = userRepository.getUser(userDTO.getUsername());
		if(!user.isAvailable())
			return Response.status(Status.NOT_ACCEPTABLE).entity("No user found").build();
		setLastLogin(user);
		userRepository.editEntity(user);
		return Response.ok(convertEntityToDTO(user), MediaType.APPLICATION_JSON).build();
	}
	
	public Response changePassword(String username, String oldPassword, String newPassword) {
		//Creates DTO with pass e username
		RegisteredUserDTO userDTO= new RegisteredUserDTO();
		userDTO.setUsername(username);
		//userDTO.setPassword(oldPassword);
		
		//We must check if User is valid (username and old password)
		Response response=checkIfUserValid(userDTO, oldPassword);
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
		
		Response response=checkParameters(userDTO,true);
		if(response.getStatus()!=Response.Status.OK.getStatusCode())
			return response;
		
		response=checkIfChangesValid(userDTO);
		if(response.getStatus()!=Response.Status.OK.getStatusCode())
			return response;
		
		RegisteredUser updatedUser=convertDTOToEntity(userDTO);
		userRepository.editEntity(updatedUser);
		return Response.ok(convertEntityToDTO(updatedUser), MediaType.APPLICATION_JSON).build();
	}
	
	public Response remove(long id) {
		
		///To do afterwards: when session is achieved -> check if admin is deleting own account (must be avoided)
		
		if(id==0 || !(userRepository.userExists(id)))
			return Response.status(Status.FORBIDDEN).entity("Invalid ID").build();
		userRepository.deleteEntity(id);
		return Response.ok().entity("Success").build();
	}
	
	//Temporary
	public Response resetPassword(RegisteredUserDTO userDTO) throws IOException {
		//Check if ID and e-mail there
		if(userDTO.getEmail()==null || userDTO.getId()==0 || userDTO.getUsername()==null)
			return Response.status(Status.NOT_ACCEPTABLE).entity("ID and e-mail must be present").build();
		RegisteredUser myUser=userRepository.getEntity(userDTO.getId());
		//Check if emails match
		if(!userDTO.getEmail().equals(myUser.getEmail()) || !userDTO.getUsername().equals(myUser.getUsername()))
			return Response.status(Status.NOT_ACCEPTABLE).entity("Mismatch between sent data and database").build();
		//Generate new random string
		String newPassword=generatePassword(userDTO.getEmail(),true);
		String[] hashPass;
		
		hashPass=passwordToHashcode(newPassword);
		userRepository.changePassword(myUser.getUsername(), hashPass);
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
	
	public Response checkIfPasswordValid(RegisteredUserDTO userDTO, String password) {
		RegisteredUser myUser=userRepository.getUser(userDTO.getUsername());
		String key=myUser.getHashcode();
		String salt=myUser.getSalt();
		
		if(!PasswordUtils.verifyPassword(password, key, salt))
			return Response.status(Status.FORBIDDEN).entity("Invalid Password").build();
		return Response.ok().entity("Success").build();
	}
	
	public Response checkIfUsernameExists(String username) {
		if(userRepository.userExists(username))
			return Response.status(Status.FORBIDDEN).entity("Username already exists.").build();
		return Response.ok().entity("Success").build();
	}
	
	public Response checkIfUserValid(RegisteredUserDTO userDTO, String password, String type) {
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
		return checkIfPasswordValid(userDTO, password);
	}
	
	public Response checkIfUserValid(RegisteredUserDTO userDTO, String password) {
		return checkIfUserValid(userDTO, password, "username");
	}
	
	public Response checkParameters(RegisteredUserDTO userDTO, boolean needID) {
		if(userDTO.getEmail()==null || userDTO.getUsername()==null ||userDTO.getAccessType()==null)
			return Response.status(Status.FORBIDDEN).entity("Invalid User parameters. Check if all parameters were inserted").build();
		if(needID==true && !userRepository.userExists(userDTO.getId()))
			return Response.status(Status.FORBIDDEN).entity("Invalid ID").build();
		return Response.ok().entity("Success").build();
	}
	
	public Response checkIfChangesValid(RegisteredUserDTO updatedUser) {
		RegisteredUser newUser=convertDTOToEntity(updatedUser);
		RegisteredUser oldUser=userRepository.getEntity(newUser.getId());
		if(!newUser.getUsername().equals(oldUser.getUsername()))
			return checkIfUsernameExists(newUser.getUsername());
		if(!newUser.getEmail().equals(oldUser.getEmail()))
			return checkIfEmailExists(newUser.getEmail());
		if(!newUser.getLastLogin().equals(oldUser.getLastLogin()))
			return Response.status(Status.FORBIDDEN).entity("Last Login must not be changed in edit").build();
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
	
	//This function generates a password and sends an e-mail with said password
	//reset parameters checks if it adds user (reset=false) or resets password(reset=true)
	public String generatePassword(String sendTo, boolean reset) throws IOException {
		//String password=PasswordUtils.generateSalt(10).get();
		String password=PasswordUtils.generateRandomPassword(10);
		MyEmail myEmail=new MyEmail();
		if(reset)
			myEmail.setBody("A nova password é: "+password+"\n Não se esqueça de mudá-la.");
		else
			myEmail.setBody("A sua password é: "+password+"\n Não se esqueça de mudá-la.");
		myEmail.setEmailTo(sendTo);
		//emailService.sendEmail(myEmail);
		return password;
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
		userDTO.setAvailable(user.isAvailable());
		
		
		DateTimeFormatter formatter =DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String dateTimeString=user.getLastLogin().format(formatter);
		userDTO.setLastLogin(dateTimeString);
		
		return userDTO;
	}
	
	public RegisteredUser convertDTOToEntity(RegisteredUserDTO userDTO) {
		RegisteredUser user=userRepository.getEntity(userDTO.getId());
		user.setAccessType(userDTO.getAccessType());
		user.setEmail(userDTO.getEmail());
		user.setUsername(userDTO.getUsername());
		user.setAvailable(userDTO.isAvailable());
		return user;
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////AUXILIARY METHODS/////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////~
	
	public void setLastLogin(RegisteredUser user) {
		LocalDateTime date=LocalDateTime.now();
		user.setLastLogin(date);
	}
	
	public String isUsernameOrEmail(String usernameOrEmail) {
		return usernameOrEmail.indexOf('@')!=-1 ? "email" : "username";
	}
}
