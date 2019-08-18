package pt.aubay.testesproject.repositories;

import java.util.List;

import javax.faces.bean.RequestScoped;
import javax.persistence.Query;

import pt.aubay.testesproject.models.entities.Category;
import pt.aubay.testesproject.models.entities.SolvedTest;


@RequestScoped
public class SolvedTestRepository extends Repositories<SolvedTest>{

	@Override
	protected Class<SolvedTest> getEntityClass() {
		// TODO Auto-generated method stub
		return SolvedTest.class;
	}
	
	public long getSolvedTest(long id) {
		Query query = em.createNamedQuery("SolvedTest.getSolvedTest", getEntityClass());
		query.setParameter("id", id);
		return (long) query.getSingleResult();
	}
	
	public List<SolvedTest> getAll() {
		Query query = em.createNamedQuery("SolvedTest.getAll", getEntityClass());
		return query.getResultList();
	}	
	public long count() {
		Query query = em.createNamedQuery("SolvedTest.count");
		return (long) query.getSingleResult();
	}
	
	public boolean idExists(long id) {
		Query query = em.createNamedQuery("SolvedTest.checkIfExists");
		query.setParameter("id", id);
		return (long) query.getSingleResult() == 1;
	}
	
	public boolean checkUniqueness(long candidateID, long testID) {
		Query query = em.createNamedQuery("SolvedTest.checkUniqueness");
		query.setParameter("candidateID", candidateID);
		query.setParameter("testID", testID);
		return (long) query.getSingleResult() == 1;
	}
	
}
