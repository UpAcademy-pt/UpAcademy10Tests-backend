package pt.aubay.testesproject.models;

public class RegisteredUser extends Models{
	private String username;
	private String email;
	private String hashcode;
	private String salt;
	private String accesstype;

	public RegisteredUser() {
		
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;	
	}
	
	public String getHashcode() {
		return hashcode;
	}
	
	public void setHashcode(String hashcode) {
		this.hashcode = hashcode;	
	}
	
	public String getSalt() {
		return salt;
	}
	
	public void setSalt(String salt) {
		this.salt = salt;	
	}
	
	public String getAccessType() {
		return accesstype;
	}
	
	public void setAccessType(String accesstype) {
		this.accesstype = accesstype;	
	}
	
}