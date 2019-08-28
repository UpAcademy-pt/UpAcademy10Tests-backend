package pt.aubay.testesproject.repositories;

import java.util.Date;
import java.util.List;

import javax.faces.bean.RequestScoped;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import pt.aubay.testesproject.models.entities.Questions;

@RequestScoped
public class QuestionRepository extends Repositories<Questions>{
	
	@Override
	protected Class<Questions> getEntityClass() {
		// TODO Auto-generated method stub
		return Questions.class;
	}
	
	public Questions getQuestions(long id) {
		TypedQuery<Questions> query = em.createNamedQuery("Questions.getQuestions", getEntityClass());
		query.setParameter("id", id);
		return query.getSingleResult();
	}
	
	public List<Questions> getAll() {
		TypedQuery<Questions> query = em.createNamedQuery("Questions.getAll", getEntityClass());
		return query.getResultList();
	}
	
	public List<Questions> getRandomQuestions(List<Long> ids) {
		TypedQuery<Questions> query = em.createNamedQuery("Questions.getRandomQuestionOfCategory", getEntityClass());
		query.setParameter("ids", ids);
		return query.getResultList();
	}	
	
	public List<Long> getQuestionIDS(String category){
		TypedQuery<Long> query = em.createNamedQuery("Questions.getAllQuestionIDsOfCategory", Long.class);
		query.setParameter("category", category);
		return query.getResultList();
	}
	
	public List<Long> getQuestionIDS(long categoryID){
		TypedQuery<Long> query = em.createNamedQuery("Questions.getAllQuestionIDsOfCategoryID", Long.class);
		query.setParameter("categoryID", categoryID);
		return query.getResultList();
	}
	
	public long count() {
		Query query = em.createNamedQuery("Questions.count");
		return (long) query.getSingleResult();
	}
	
	public long count(String category) {
		Query query = em.createNamedQuery("Questions.getQuestionsNumberOfCategory");
		query.setParameter("category", category);
		return (long) query.getSingleResult();
	}
	
	public boolean idExists(long id) {
		Query query = em.createNamedQuery("Questions.checkIfIdExists");
		query.setParameter("id", id);
		return (long) query.getSingleResult() == 1;
	}
	
	public boolean questionExists(String question) {
		Query query = em.createNamedQuery("Questions.checkIfExists");
		query.setParameter("question", question);
		return (long) query.getSingleResult() == 1;
	}
	
	public boolean categoryExists(long categoryID) {
		Query query = em.createNamedQuery("Questions.checkCategory");
		query.setParameter("categoryID", categoryID);
		return (long) query.getSingleResult() != 0;
	}
	
	public boolean checkIfQuestionInTest(long questionID) {
		Query query = em.createNamedQuery("Questions.checkTest");
		query.setParameter("questionID", questionID);
		return (long) query.getSingleResult() != 0;
	}
}
