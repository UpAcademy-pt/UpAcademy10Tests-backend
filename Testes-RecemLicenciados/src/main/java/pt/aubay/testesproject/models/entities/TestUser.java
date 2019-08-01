package pt.aubay.testesproject.models.entities;

import pt.aubay.testesproject.auxiliary.PersonalData;

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
