package pt.aubay.testesproject.models;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({
	 @NamedQuery(name="User.getAll",query="SELECT u FROM User u"),
	 @NamedQuery(name = "User.count", query = "SELECT COUNT(u.id) FROM User u"),
	 @NamedQuery(name="User.checkIfExists", query = "SELECT COUNT(u.id) FROM User u WHERE u.id =:id"),
	 @NamedQuery(name="User.getUserByUsername", query="SELECT u FROM User u WHERE u.username=:username"),
	 @NamedQuery(name="User.checkIfExistsByUsername", query = "SELECT COUNT(u.username) FROM User u WHERE u.username =:username"),
})
public class User extends Models{
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	public String getHashPass() {
		return hashPass;
	}
	public void setHashPass(String hashPass) {
		this.hashPass = hashPass;
	}
	public String getSalt() {
		return salt;
	}
	public void setSalt(String salt) {
		this.salt = salt;
	}

	protected String username;
	protected String hashPass; /*vai conter o hash-code e a password*/
	protected String salt;
	
	
}
