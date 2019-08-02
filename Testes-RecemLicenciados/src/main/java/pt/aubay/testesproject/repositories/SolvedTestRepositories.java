package pt.aubay.testesproject.repositories;

import java.util.List;

import javax.faces.bean.RequestScoped;
import javax.persistence.Query;

import pt.aubay.testesproject.models.entities.SolvedTest;


@RequestScoped
public class SolvedTestRepositories extends Repositories<SolvedTest>{

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
	
	public boolean solvedTestExists(long id) {
		Query query = em.createNamedQuery("SolvedTest.checkIfExists");
		query.setParameter("id", id);
		return (long) query.getSingleResult() == 1;
	}
	
}
