package pt.aubay.testesproject.models.dto;

public class CandidateDTO {
	private String name;
	private String email;
	private String emailRecruiter;//email do recrutador recruiter

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
		return emailRecruiter;
	}
	
	public void setEmail(String email) {
		this.emailRecruiter = email;	
	}
}
