package pt.aubay.testesproject.credentials;

public class AddUserCredentials{
	
	private String username;
	private String password;
	private String email;
	private String accessType;
	
	public AddUserCredentials(){
	}
	
	
	public AddUserCredentials(String username, String password, String email, String accessType){
		this.username=username;
		this.password=password;
		this.accessType=accessType;
		this.email=email;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
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
	
	
}
