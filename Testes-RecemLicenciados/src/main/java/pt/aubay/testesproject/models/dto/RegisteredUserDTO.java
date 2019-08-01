package pt.aubay.testesproject.models.dto;

public class RegisteredUserDTO extends ModelsDTO{
	private String username;
	private String email;
	private String accessType;
	private String password;
	
	public RegisteredUserDTO() {	
	}
	
	public RegisteredUserDTO(String email, String password) {	
		this.email=email;
		this.password=password;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public String getAccessType() {
		return accessType;
	}
	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
}
