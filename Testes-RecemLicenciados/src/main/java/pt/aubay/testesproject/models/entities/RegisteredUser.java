package pt.aubay.testesproject.models.entities;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({
	 @NamedQuery(name="RegisteredUser.getAll",query="SELECT u FROM RegisteredUser u"),
	 @NamedQuery(name="RegisteredUser.count", query = "SELECT COUNT(u.id) FROM RegisteredUser u"),
	 @NamedQuery(name="RegisteredUser.checkIfExists", query = "SELECT COUNT(u.id) FROM RegisteredUser u WHERE u.id =:id"),
	 @NamedQuery(name="RegisteredUser.getUserByUsername", query="SELECT u FROM RegisteredUser u WHERE u.username=:username"),
	 @NamedQuery(name="RegisteredUser.checkIfExistsByUsername", query = "SELECT COUNT(u.username) FROM RegisteredUser u WHERE u.username =:username"),
	 @NamedQuery(name="RegisteredUser.getIDByUsername", query="SELECT u.id FROM RegisteredUser u WHERE u.username=:username"),
	 @NamedQuery(name="RegisteredUser.checkIfUsername", query="SELECT COUNT(u.id) FROM RegisteredUser u WHERE u.username=:username"),
	 @NamedQuery(name="RegisteredUser.checkIfEmail", query="SELECT COUNT(u.id) FROM RegisteredUser u WHERE u.email=:email"),
	 @NamedQuery(name="RegisteredUser.getUsernameByEmail", query="SELECT u.username FROM RegisteredUser u WHERE u.email=:email")
})

public class RegisteredUser extends Models{
	private static final long serialVersionUID = 1L;
	private String username;
	private String email;
	private String hashcode;
	private String salt;
	private String accessType;

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