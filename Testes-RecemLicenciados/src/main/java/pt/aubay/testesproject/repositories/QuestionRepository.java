package pt.aubay.testesproject.repositories;

import java.util.Date;
import java.util.List;

import javax.faces.bean.RequestScoped;
import javax.persistence.Query;

import pt.aubay.testesproject.models.entities.Questions;

@RequestScoped
public class QuestionRepository extends Repositories<Questions>{
	
	@Override
	protected Class<Questions> getEntityClass() {
		// TODO Auto-generated method stub
		return Questions.class;
	}
	
	public long getQuestions(long id) {
		Query query = em.createNamedQuery("Questions.getQuestions", getEntityClass());
		query.setParameter("id", id);
		return (long) query.getSingleResult();
	}
	
	public List<Questions> getAll() {
		Query query = em.createNamedQuery("Questions.getAll", getEntityClass());
		return query.getResultList();
	}
	
	public List<Questions> getRandomQuestions(List<Long> ids) {
		Query query = em.createNamedQuery("Questions.getRandomQuestionOfCategory", getEntityClass());
		query.setParameter("ids", ids);
		return query.getResultList();
	}	
	
	public List<Long> getQuestionIDS(String category){
		Query query = em.createNamedQuery("Questions.getAllQuestionIDsOfCategory", Long.class);
		query.setParameter("category", category);
		return query.getResultList();
	}
	
	public long count() {
		Query query = em.createNamedQuery("Questions.count");
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
		return (long) query.getSingleResult() == 1;
	}
}
