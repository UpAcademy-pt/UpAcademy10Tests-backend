package pt.aubay.testesproject.repositories;

import java.util.List;

import javax.faces.bean.RequestScoped;
import javax.persistence.Query;

import pt.aubay.testesproject.models.sessions.TestSession;

@RequestScoped
public class TestSessionRepository extends Repositories<TestSession>{

	@Override
	protected Class<TestSession> getEntityClass() {
		// TODO Auto-generated method stub
		return TestSession.class;
	}
	
	public boolean IDExists(long id) {
		Query query = em.createNamedQuery("TestSession.checkIfExists");
		query.setParameter("id", id);
		return (long) query.getSingleResult() == 1;
	}
	
	public boolean checkIfTestExists(long testID) {
		Query query = em.createNamedQuery("TestSession.checkIfTestExists");
		query.setParameter("testID", testID);
		return (long) query.getSingleResult() == 1;
	}
	
	public boolean checkIfSessionExistsWithTest(long sessionID, long testID) {
		Query query = em.createNamedQuery("TestSession.checkIfSessionExistsWithTest");
		query.setParameter("id", sessionID);
		query.setParameter("testID", testID);
		return (long) query.getSingleResult() == 1;
	}
	
	public List<Long> getSessionIDsOfTest(long testID){
		Query query = em.createNamedQuery("TestSession.getTestIDs", Long.class);
		query.setParameter("testID", testID);
		return query.getResultList();
	}
	
	public TestSession addSession(TestSession session) {
		return em.merge(session);
	}

}
