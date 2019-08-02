package pt.aubay.testesproject.repositories;

import java.util.List;

import javax.persistence.Query;

import pt.aubay.testesproject.models.entities.RegisteredUser;

public class RegisteredUserRepositories extends Repositories<RegisteredUser> {
	
	@Override
	protected Class<RegisteredUser> getEntityClass() {
		// TODO Auto-generated method stub
		return RegisteredUser.class;
	}
	
	public long getRegisteredUser(long id) {
		Query query = em.createNamedQuery("RegisteredUser.getRegisteredUser", getEntityClass());
		query.setParameter("id", id);
		return (long) query.getSingleResult();
	}
	
//	public Test getRegisteredUserByUsername(String username) {
//		Query query = em.createNamedQuery("RegisteredUser.getRegisteredUserByUsername", getEntityClass());
//		query.setParameter("username", username);
//		return (RegisteredUser) query.getSingleResult();
//	}
	
//	public Test getRegisteredUserByEmail(String email) {
//		Query query = em.createNamedQuery("RegisteredUser.getRegisteredUserByEmail", getEntityClass());
//		query.setParameter("email", email);
//		return (RegisteredUser) query.getSingleResult();
//	}
	
//	public Test getRegisteredUserByAccessType(String accesstype) {
//	Query query = em.createNamedQuery("RegisteredUser.getRegisteredUserByAccessType", getEntityClass());
//	query.setParameter("email", email);
//	return (RegisteredUser) query.getSingleResult();
//	}
	
	public List<RegisteredUser> getAll() {
		Query query = em.createNamedQuery("RegisteredUser.getAll", getEntityClass());
		return query.getResultList();
	}	
	public long count() {
		Query query = em.createNamedQuery("RegisteredUser.count");
		return (long) query.getSingleResult();
	}
	
	public boolean registeredUserExists(long id) {
		Query query = em.createNamedQuery("RegisteredUser.checkIfExists");
		query.setParameter("id", id);
		return (long) query.getSingleResult() == 1;
	}
}
