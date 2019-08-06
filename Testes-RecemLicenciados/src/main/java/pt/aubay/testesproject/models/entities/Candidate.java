package pt.aubay.testesproject.models.entities;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import pt.aubay.testesproject.auxiliary.PersonalData;

@Entity
@NamedQueries({
	 @NamedQuery(name="Candidate.getCandidate", query="SELECT v FROM Candidate v WHERE v.id=:id"),
	 @NamedQuery(name="Candidate.getAll",query="SELECT v FROM Test v"),
	 @NamedQuery(name="Candidate.count", query = "SELECT COUNT(v.id) FROM Candidate v"),
	 @NamedQuery(name="Candidate.checkIfExists", query = "SELECT COUNT(v.id) FROM Candidate v WHERE v.id =:id"),
})


public class Candidate extends Models{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//private PersonalData personalData;//extender para dados pessoais - eventual classe futura
	private String name;
	private String email;
	private String emailRecruiter;//email do recrutador recruiter

	public Candidate() {
		
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmailRecruiter() {
		return emailRecruiter;
	}

	public void setEmailRecruiter(String emailRecruiter) {
		this.emailRecruiter = emailRecruiter;
	}
	
	public String getEmail() {
		return emailRecruiter;
	}
	
	public void setEmail(String email) {
		this.emailRecruiter = email;	
	}
	
}
