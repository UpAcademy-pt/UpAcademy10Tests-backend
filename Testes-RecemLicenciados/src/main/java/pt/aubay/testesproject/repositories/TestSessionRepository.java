package pt.aubay.testesproject.repositories;

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
		Query query = em.createNamedQuery("TestSession.checkIfItExists");
		query.setParameter("id", id);
		return (long) query.getSingleResult() == 1;
	}
	
	public TestSession addSession(TestSession session) {
		return em.merge(session);
	}

}
