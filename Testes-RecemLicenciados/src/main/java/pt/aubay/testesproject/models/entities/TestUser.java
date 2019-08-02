package pt.aubay.testesproject.models.entities;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import pt.aubay.testesproject.auxiliary.PersonalData;

@Entity
@NamedQueries({
	 @NamedQuery(name="TestUser.getTestUser", query="SELECT v FROM TestUser v WHERE v.id=:id"),
	 @NamedQuery(name="TestUser.getAll",query="SELECT v FROM Test v"),
	 @NamedQuery(name="TestUser.count", query = "SELECT COUNT(v.id) FROM TestUser v"),
	 @NamedQuery(name="TestUser.checkIfExists", query = "SELECT COUNT(v.id) FROM TestUser v WHERE v.id =:id"),
})


public class TestUser extends Models{
	private PersonalData personalData;//extender para dados pessoais - eventual classe futura
	private String email;//email do recrutador
	
	
	public PersonalData getPersonalData() {
		return personalData;
	}

	public void setPersonalData(PersonalData personalData) {
		this.personalData = personalData;
	}

	public TestUser() {
		
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;	
	}
	
}
