package pt.aubay.testesproject.models;

public class Answer extends Models{
	private long questionId;
	private String givenAnswer;
	
	public Answer() {
		
	}

	public long getQuestionId() {
		return questionId;
	}

	public void setQuestionId(long questionId) {
		this.questionId = questionId;
	}

	public String getGivenAnswer() {
		return givenAnswer;
	}

	public void setGivenAnswer(String givenAnswer) {
		this.givenAnswer = givenAnswer;
	}
}
