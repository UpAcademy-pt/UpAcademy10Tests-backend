package pt.aubay.testesproject.models.entities;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

@Entity
@NamedQueries({
	 @NamedQuery(name="Candidate.getCandidate", query="SELECT v FROM Candidate v WHERE v.id=:id"),
	 @NamedQuery(name="Candidate.getCandidateByEmail", query="SELECT v FROM Candidate v WHERE v.email=:email"),
	 @NamedQuery(name="Candidate.getAll",query="SELECT v FROM Test v"),
	 @NamedQuery(name="Candidate.count", query = "SELECT COUNT(v.id) FROM Candidate v"),
	 @NamedQuery(name="Candidate.checkIfExists", query = "SELECT COUNT(v.id) FROM Candidate v WHERE v.id =:id"),
	 @NamedQuery(name="Candidate.checkIfExistsByEmail", query = "SELECT COUNT(v.id) FROM Candidate v WHERE v.email =:email")
})


public class Candidate extends Models{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//private PersonalData personalData;//extender para dados pessoais - eventual classe futura
	private String name;
	private String email;
	private String countryIP;
	
	@ManyToOne
	RegisteredUser recruiter;//email do recrutador recruiter
	
//	@OneToOne(fetch = FetchType.EAGER, mappedBy = "candidate", cascade = CascadeType.ALL)
//	SolvedTest solvedTest;

	public Candidate() {
		
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public RegisteredUser getRecruiter() {
		return recruiter;
	}

	public void setRecruiter(RegisteredUser recruiter) {
		this.recruiter = recruiter;
	}

	public String getCountryIP() {
		return countryIP;
	}

	public void setCountryIP(String countryIP) {
		this.countryIP = countryIP;
	}

}
