package pt.aubay.testesproject.models;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({
	 @NamedQuery(name="User.getAll",query="SELECT u FROM User u"),
	 @NamedQuery(name = "User.count", query = "SELECT COUNT(u.id) FROM User u"),
	 @NamedQuery(name="User.checkIfExists", query = "SELECT COUNT(u.id) FROM User u WHERE u.id =:id"),
	 @NamedQuery(name="User.getUserByUsername", query="SELECT u FROM User u WHERE u.username=:username")
})
public class User extends Models{
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String[] getPassword() {
		return password;
	}
	public void setPassword(String[] password) {
		this.password = password;
	}
	protected String username;
	protected String [] password; /*vai conter o hash-code e a password*/
}
