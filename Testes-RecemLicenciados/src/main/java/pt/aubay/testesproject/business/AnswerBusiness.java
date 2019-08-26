package pt.aubay.testesproject.business;

import javax.inject.Inject;

import pt.aubay.testesproject.models.dto.AnswerDTO;
import pt.aubay.testesproject.models.entities.Answer;
import pt.aubay.testesproject.models.entities.Questions;
import pt.aubay.testesproject.repositories.QuestionRepository;


public class AnswerBusiness {
	
	@Inject
	private QuestionRepository questionRepository;
	
	public Answer convertDTOToEntity(AnswerDTO answerDTO) {
		Answer answer=new Answer();
		answer.setGivenAnswer(answerDTO.getGivenAnswer());
		
		Questions question=questionRepository.getEntity(answerDTO.getQuestionID());
		answer.setQuestion(question);
		
		//SolvedTest test=testRepository.getEntity(answerDTO.getSolvedTestID());
		//answer.setTest(test);
		return answer;
	}
	
	public AnswerDTO convertEntityToDTO(Answer answer) {
		AnswerDTO answerDTO=new AnswerDTO();
		answerDTO.setGivenAnswer(answer.getGivenAnswer());
		
		long questionID=answer.getQuestion().getId();
		//long testID=answer.getTest().getId();
		answerDTO.setId(answer.getId());
		answerDTO.setQuestionID(questionID);
		//answerDTO.setSolvedTestID(testID);
		return answerDTO;
	}
}
