package pt.aubay.testesproject.repositories;

import java.util.Date;
import java.util.List;

import javax.faces.bean.RequestScoped;
import javax.persistence.Query;

import pt.aubay.testesproject.models.entities.Questions;

@RequestScoped
public class QuestionsRepositories extends Repositories<Questions>{
	
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
	public long count() {
		Query query = em.createNamedQuery("Questions.count");
		return (long) query.getSingleResult();
	}
	
	public boolean questionsExists(long id) {
		Query query = em.createNamedQuery("Questions.checkIfExists");
		query.setParameter("id", id);
		return (long) query.getSingleResult() == 1;
	}
}
