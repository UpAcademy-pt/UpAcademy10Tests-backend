package pt.aubay.testesproject.models.sessions;

import java.time.LocalDateTime;

import pt.aubay.testesproject.models.dto.TestDTO;

public class TestSessionDTO {
	private TestDTO test;
	private String recruiterEmail;
	private String candidateEmail;
	private long sessionID;
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
	public String getCandidateEmail() {
		return candidateEmail;
	}
	public void setCandidateEmail(String candidateEmail) {
		this.candidateEmail = candidateEmail;
	}
	public long getSessionID() {
		return sessionID;
	}
	public void setSessionID(long sessionID) {
		this.sessionID = sessionID;
	}
	
}
