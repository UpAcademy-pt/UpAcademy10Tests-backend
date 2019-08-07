package pt.aubay.testesproject.models.dto;



public class AnswerDTO extends ModelsDTO{

	private int[] givenAnswer;
	long questionID;
	//long solvedTestID;
	public int[] getGivenAnswer() {
		return givenAnswer;
	}
	public void setGivenAnswer(int[] givenAnswer) {
		this.givenAnswer = givenAnswer;
	}
	public long getQuestionID() {
		return questionID;
	}
	public void setQuestionID(long questionID) {
		this.questionID = questionID;
	}
//	public long getSolvedTestID() {
//		return solvedTestID;
//	}
//	public void setSolvedTestID(long solvedTestID) {
//		this.solvedTestID = solvedTestID;
//	}
	
}
