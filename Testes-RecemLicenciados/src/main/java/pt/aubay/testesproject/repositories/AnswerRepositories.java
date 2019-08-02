package pt.aubay.testesproject.repositories;

import java.util.List;

import javax.faces.bean.RequestScoped;
import javax.persistence.Query;

import pt.aubay.testesproject.models.entities.Answer;

@RequestScoped
public class AnswerRepositories extends Repositories<Answer>{
	
	@Override
	protected Class<Answer> getEntityClass() {
		// TODO Auto-generated method stub
		return Answer.class;
	}
	
	public long getAnswer(long id) {
		Query query = em.createNamedQuery("Answer.getAnswer", getEntityClass());
		query.setParameter("id", id);
		return (long) query.getSingleResult();
	}
	
	public List<Answer> getAll() {
		Query query = em.createNamedQuery("Answer.getAll", getEntityClass());
		return query.getResultList();
	}	
	
	public long count() {
		Query query = em.createNamedQuery("Answer.count");
		return (long) query.getSingleResult();
	}
	
	public boolean answerExists(long id) {
		Query query = em.createNamedQuery("Answer.checkIfExists");
		query.setParameter("id", id);
		return (long) query.getSingleResult() == 1;
	}
	
}
