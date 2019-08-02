package pt.aubay.testesproject.repositories;

import java.util.List;

import javax.faces.bean.RequestScoped;
import javax.persistence.Query;

import pt.aubay.testesproject.models.entities.Candidate;

@RequestScoped
public class CandidateRepositories extends Repositories<Candidate>{
	
	@Override
	protected Class<Candidate> getEntityClass() {
		// TODO Auto-generated method stub
		return Candidate.class;
	}
	
	public long getCandidate(long id) {
		Query query = em.createNamedQuery("Candidate.getCandidate", getEntityClass());
		query.setParameter("id", id);
		return (long) query.getSingleResult();
	}
	
	public List<Candidate> getAll() {
		Query query = em.createNamedQuery("Candidate.getAll");
		return query.getResultList();
	}
	
	public long count() {
		Query query = em.createNamedQuery("Candidate.count");
		return (long) query.getSingleResult();
	}
	
	public boolean CandidateExists(long id) {
		Query query = em.createNamedQuery("Candidate.checkIfItExists");
		query.setParameter("id", id);
		return (long) query.getSingleResult() == 1;
	}
}
