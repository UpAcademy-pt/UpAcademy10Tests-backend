package pt.aubay.testesproject.models.sessions;

import java.time.LocalDateTime;

import pt.aubay.testesproject.models.dto.TestDTO;

public class TestSessionDTO {
	private TestDTO test;
	private String recruiterEmail;
	public TestDTO getTest() {
		return test;
	}
	public void setTest(TestDTO test) {
		this.test = test;
	}
	public String getRecruiterEmail() {
		return recruiterEmail;
	}
	public void setRecruiterEmail(String recruiterEmail) {
		this.recruiterEmail = recruiterEmail;
	}
	
}
