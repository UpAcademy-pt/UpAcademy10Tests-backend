package pt.aubay.testesproject.business;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.NotFoundException;

import pt.aubay.testesproject.auxiliary.MyEmail;
import pt.aubay.testesproject.models.dto.RegisteredUserDTO;
import pt.aubay.testesproject.models.entities.RegisteredUser;
import pt.aubay.testesproject.repositories.RegisteredUserRepository;
import pt.aubay.testesproject.services.EmailServices;
import pt.aubay.testesproject.utils.PasswordUtils;

@Transactional
public class RegisteredUserBusiness {
	@Inject
	RegisteredUserRepository userRepository;
	
	@Inject
	EmailServices emailService;
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////CRUD-Methods//////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void add(RegisteredUserDTO userDTO){
		
		//First we need to check if parameters there
		checkParameters(userDTO, false);

		//Then, we need to check if e-mail exists already (must be unique)
		checkIfEmailExists(userDTO.getEmail());
		
		String username=userDTO.getUsername();
		String email=userDTO.getEmail();
		String accessType=userDTO.getAccessType();
		RegisteredUser user=new RegisteredUser();
		if(!userRepository.userExists(username)) {
			//password->(hash, salt)
			String password;
			
			try {
				password = generatePassword(userDTO.getEmail(),false);
			} catch (IOException e) {
				throw new BadRequestException("Unknown problem with e-mail");
			}
			String[] hashCode=passwordToHashcode(password);
			
			//set fields to Entity
			user.setUsername(username); user.setHashcode(hashCode[0]);
			user.setSalt(hashCode[1]); user.setEmail(email);
			user.setAccessType(accessType);
			setLastLogin(user);
			user.setAvailable(true);
			
			//Adicionar entity ao repositório
			userRepository.addEntity(user);
			//return Response.ok(password, MediaType.TEXT_PLAIN).build();
		}
		else 
			throw new BadRequestException("This username exists already");
	}
	
	public List<RegisteredUserDTO> getAllUsers() {
		List<RegisteredUserDTO> allUsers=new ArrayList<RegisteredUserDTO>();
		for(RegisteredUser elem:userRepository.getAll())
			allUsers.add(convertEntityToDTO(elem));
		return allUsers;
	}
	
	//to get all Users except for currentUser
	public List<RegisteredUserDTO> getAllUsers(long id) {
		//check if ID exists
		if(!userRepository.userExists(id))
			throw new NotFoundException("Invalid ID");
		List<RegisteredUserDTO> allUsers=new ArrayList<RegisteredUserDTO>();
		for(RegisteredUser elem:userRepository.getAll())
			if(id!=elem.getId())
				allUsers.add(convertEntityToDTO(elem));
		return allUsers;
	}
	
	public RegisteredUserDTO get(String usernameOrEmail, String password){
		RegisteredUserDTO userDTO=new RegisteredUserDTO();
		//type checks if input is username or email - login might be achieved by both username and email
		String type=isUsernameOrEmail(usernameOrEmail);
		if(type.equals("email"))
			userDTO.setEmail(usernameOrEmail);
		else
			userDTO.setUsername(usernameOrEmail);
		//Checks if both username/email and password are valid
		checkIfUserValid(userDTO,password,type);
		//Sets username corresponding to email given
		RegisteredUser user = userRepository.getUser(userDTO.getUsername());
		if(!user.isAvailable())
			throw new NotAcceptableException("No user found");
		setLastLogin(user);
		userRepository.editEntity(user);
		return convertEntityToDTO(user);
	}
	
	public void changePassword(String username, String oldPassword, String newPassword){
		//Creates DTO with pass e username
		RegisteredUserDTO userDTO= new RegisteredUserDTO();
		userDTO.setUsername(username);
		//userDTO.setPassword(oldPassword);
		
		//We must check if User is valid (username and old password)
		checkIfUserValid(userDTO, oldPassword);

		//Changes password
		String[] newHash;
		newHash=passwordToHashcode(newPassword);
		userRepository.changePassword(username, newHash);
	}
	
	public RegisteredUserDTO edit(RegisteredUserDTO userDTO){
		
		checkParameters(userDTO,true);
		
		checkIfChangesValid(userDTO);

		
		RegisteredUser updatedUser=convertDTOToEntity(userDTO);
		userRepository.editEntity(updatedUser);
		return convertEntityToDTO(updatedUser);
	}
	

	public void remove(long id) {
		
		///To do afterwards: when session is achieved -> check if admin is deleting own account (must be avoided)
		
		if(id==0 || !(userRepository.userExists(id)))
			throw new BadRequestException("Invalid ID");
		userRepository.deleteEntity(id);
	}
	
	//Temporary
	public void resetPassword(RegisteredUserDTO userDTO) throws IOException {
		//Check if ID and e-mail there
		if(userDTO.getEmail()==null || userDTO.getId()==0 || userDTO.getUsername()==null)
			throw new NotAcceptableException("ID and e-mail must be present");
		RegisteredUser myUser=userRepository.getEntity(userDTO.getId());
		//Check if emails match
		if(!userDTO.getEmail().equals(myUser.getEmail()) || !userDTO.getUsername().equals(myUser.getUsername()))
			throw new NotAcceptableException("Mismatch between sent data and database");
		//Generate new random string
		String newPassword=generatePassword(userDTO.getEmail(),true);
		String[] hashPass;
		
		hashPass=passwordToHashcode(newPassword);
		userRepository.changePassword(myUser.getUsername(), hashPass);
	}
	
/*	public Response getFilterRegisteredUsers(int page, int pageSize) {
		
		
		int fromIndex = page * pageSize;
		int toIndex = fromIndex + pageSize;
		int resultSize = result.size();
		
		if(fromIndex >= resultSize) {
			result = Collections.emptyList();
		}
		else if(toIndex > resultSize) {
			result = result.subList(fromIndex, resultSize);
		}
		else {
			result = result.subList(fromIndex, toIndex);
		}
	}
*/	

	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////Checking-Methods//////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void checkIfUsernameValid(String username) {
		if(!userRepository.userExists(username))
			throw new NotFoundException("No such user in database");
	}
	
	public void checkIfEmailExists(String email) {
		if(userRepository.emailExists(email))
			throw new BadRequestException("Email already exists.");
	}
	
	public void checkIfPasswordValid(RegisteredUserDTO userDTO, String password) {
		RegisteredUser myUser=userRepository.getUser(userDTO.getUsername());
		String key=myUser.getHashcode();
		String salt=myUser.getSalt();
		
		if(!PasswordUtils.verifyPassword(password, key, salt))
			throw new BadRequestException("Invalid Password");
	}
	
	public void checkIfUsernameExists(String username){
		if(userRepository.userExists(username))
			throw new BadRequestException("Username already exists.");
	}
	
	public void checkIfUserValid(RegisteredUserDTO userDTO, String password, String type){
		//User valid if both username and password are valid
		if(type=="username")
			checkIfUsernameValid(userDTO.getUsername());
		if(type=="email") {
			if(!userRepository.emailExists(userDTO.getEmail()))
				throw new NotFoundException("No such email in database");
			//our checks use username, so if e-mail exists in database the username is retrieved via the corresponding e-mail
			else
				userDTO.setUsername(userRepository.getUsernameByEmail(userDTO.getEmail()));
		}
		checkIfPasswordValid(userDTO, password);
	}
	
	public void checkIfUserValid(RegisteredUserDTO userDTO, String password) {
		checkIfUserValid(userDTO, password, "username");
	}
	
	public void checkParameters(RegisteredUserDTO userDTO, boolean needID) {
		if(userDTO.getEmail()==null || userDTO.getUsername()==null ||userDTO.getAccessType()==null)
			throw new BadRequestException("Invalid User parameters. Check if all parameters were inserted");
		if(needID==true && !userRepository.userExists(userDTO.getId()))
			throw new BadRequestException("Invalid ID");
	}
	
	public void checkIfChangesValid(RegisteredUserDTO updatedUser){
		RegisteredUser newUser=convertDTOToEntity(updatedUser);
		RegisteredUser oldUser=userRepository.getEntity(newUser.getId());
		if(!newUser.getUsername().equals(oldUser.getUsername()))
			checkIfUsernameExists(newUser.getUsername());
		if(!newUser.getEmail().equals(oldUser.getEmail()))
			checkIfEmailExists(newUser.getEmail());
		if(!newUser.getLastLogin().equals(oldUser.getLastLogin()))
			throw new BadRequestException("Last Login must not be changed in edit");
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
		myEmail.setSubject("Password da sua conta - Tester Aubay");
		if(reset)
			myEmail.setBody("A nova password é: <strong>"+password+"</strong>.<br> Não se esqueça de mudá-la.<br><br>");
		else
			myEmail.setBody("A sua password é:  <strong>"+password+"</strong>.<br> Não se esqueça de mudá-la.<br><br>");
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
		
		
		DateTimeFormatter formatter =DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
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
