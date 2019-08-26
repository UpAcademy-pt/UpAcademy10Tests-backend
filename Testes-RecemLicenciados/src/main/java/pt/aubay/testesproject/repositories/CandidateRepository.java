package pt.aubay.testesproject.repositories;

import java.util.List;

import javax.faces.bean.RequestScoped;
import javax.persistence.TypedQuery;

import pt.aubay.testesproject.models.entities.Candidate;

@RequestScoped
public class CandidateRepository extends Repositories<Candidate> {
	
	@Override
	protected Class<Candidate> getEntityClass() {
		return Candidate.class;
	}
	
	public Candidate getCandidate(long id) {
		TypedQuery<Candidate> query = em.createNamedQuery("Candidate.getCandidate", getEntityClass());
		query.setParameter("id", id);
		return query.getSingleResult();
	}
	
	public Candidate getCandidate(String email) {
		TypedQuery<Candidate> query = em.createNamedQuery("Candidate.getCandidateByEmail", getEntityClass());
		query.setParameter("email", email);
		return query.getSingleResult();
	}
	
	public List<Candidate> getAll() {
		TypedQuery<Candidate> query = em.createNamedQuery("Candidate.getAll", getEntityClass());
		return query.getResultList();
	}
	
	public long count() {
		TypedQuery<Long> query = em.createNamedQuery("Candidate.count", Long.class);
		return query.getSingleResult();
	}
	
	public boolean CandidateExists(long id) {
		TypedQuery<Long> query = em.createNamedQuery("Candidate.checkIfItExists", Long.class);
		query.setParameter("id", id);
		return query.getSingleResult() == 1;
	}
	
	public boolean CandidateExists(String email) {
		TypedQuery<Long> query = em.createNamedQuery("Candidate.checkIfExistsByEmail", Long.class);
		query.setParameter("email", email);
		return query.getSingleResult() == 1;
	}
}
