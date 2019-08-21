package pt.aubay.testesproject.repositories;

import java.util.List;

import javax.faces.bean.RequestScoped;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import pt.aubay.testesproject.models.entities.Test;

@RequestScoped
public class TestRepository extends Repositories<Test> {
	
	@Override
	protected Class<Test> getEntityClass() {
		// TODO Auto-generated method stub
		return Test.class;
	}
	
	public long getTest(long id) {
		Query query = em.createNamedQuery("Test.getTest", getEntityClass());
		query.setParameter("id", id);
		return (long) query.getSingleResult();
		}
	
//	public Test getTestByAuthor(String author) {
//		Query query = em.createNamedQuery("Test.getTestByAuthor", getEntityClass());
//		query.setParameter("author", author);
//		return (Test) query.getSingleResult();
//	}
//	
//	public Test getTestByDate(Date date) {
//		Query query = em.createNamedQuery("Test.getTestByDate", getEntityClass());
//		query.setParameter("date", date);
//		return (Test) query.getSingleResult();
//	}
//	
//	public Test getTestByTimer(int timer) {
//		Query query = em.createNamedQuery("Test.getTestByTimer", getEntityClass());
//		query.setParameter("timer", timer);
//		return (Test) query.getSingleResult();//ou ResultList();
//	}
//	
//	public Test getTestByAverageScore(int averageScore) {
//		Query query = em.createNamedQuery("Test.getTestByAverageScore", getEntityClass());
//		query.setParameter("averageScore", averageScore);
//		return (Test) query.getSingleResult();//ou ResultList();
//	}
//	
//	public Test getTestByAvailability(String availability) {
//		Query query = em.createNamedQuery("Test.getTestByAverageScore", getEntityClass());
//		query.setParameter("availability", availability);
//		return (Test) query.getSingleResult();//ou ResultList();
//	}
	
	public List<Test> getAll() {
		TypedQuery<Test> query = em.createNamedQuery("Test.getAll", getEntityClass());
		return query.getResultList();
	}	
	public long count() {
		Query query = em.createNamedQuery("Test.count");
		return (long) query.getSingleResult();
	}
	
	public boolean idExists(long id) {
		Query query = em.createNamedQuery("Test.checkIfExists");
		query.setParameter("id", id);
		return (long) query.getSingleResult() == 1;
	}
	
	public boolean testExists(String testName) {
		Query query = em.createNamedQuery("Test.checkIfTestNameExists");
		query.setParameter("testName", testName);
		return (long) query.getSingleResult() == 1;
	}
	
	public boolean questionExists(long questionID) {
		Query query = em.createNamedQuery("Test.checkQuestion");
		query.setParameter("questionID", questionID);
		return (long) query.getSingleResult() != 0;
	}

	@Override
	public void deleteEntity(long id) {
		em.createQuery("DELETE FROM Test t WHERE t.id = :id")
			.setParameter("id", id)
			.executeUpdate();
	}
}
