package pt.aubay.testesproject.models.dto;

public class CandidateDTO extends ModelsDTO{
	private String name;
	private String email;
	private String emailRecruiter;//email do recrutador recruiter
	private String countryIP;

	public CandidateDTO() {
		
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
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;	
	}

	@Override
	public String toString() {
		return "CandidateDTO [name=" + name + ", email=" + email + ", emailRecruiter=" + emailRecruiter + "]";
	}

	public String getCountryIP() {
		return countryIP;
	}

	public void setCountryIP(String countryIP) {
		this.countryIP = countryIP;
	}
	
	
}
