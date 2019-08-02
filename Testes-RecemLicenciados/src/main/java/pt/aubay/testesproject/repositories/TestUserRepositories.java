package pt.aubay.testesproject.repositories;

import java.util.List;

import javax.faces.bean.RequestScoped;
import javax.persistence.Query;

import pt.aubay.testesproject.models.entities.TestUser;

@RequestScoped
public class TestUserRepositories extends Repositories<TestUser>{
	
	@Override
	protected Class<TestUser> getEntityClass() {
		// TODO Auto-generated method stub
		return TestUser.class;
	}
	
	public long getTestUser(long id) {
		Query query = em.createNamedQuery("TestUser.getTestUser", getEntityClass());
		query.setParameter("id", id);
		return (long) query.getSingleResult();
	}
	
	public List<TestUser> getAll() {
		Query query = em.createNamedQuery("TestUser.getAll");
		return query.getResultList();
	}
	
	public long count() {
		Query query = em.createNamedQuery("TestUser.count");
		return (long) query.getSingleResult();
	}
	
	public boolean testUserExists(long id) {
		Query query = em.createNamedQuery("TestUser.checkIfItExists");
		query.setParameter("id", id);
		return (long) query.getSingleResult() == 1;
	}
}
