package pt.aubay.testesproject.repositories;

import java.util.List;

import javax.persistence.Query;

import pt.aubay.testesproject.models.entities.RegisteredUser;


public class RegisteredUserRepository extends Repositories<RegisteredUser>{
	
	@Override
	protected Class<RegisteredUser> getEntityClass() {
		// TODO Auto-generated method stub
		return RegisteredUser.class;
	}

	public long getIDByUsername(String username) {
		Query query = em.createNamedQuery("RegisteredUser.getIDByUsername", Long.class);
		query.setParameter("username", username);
		return (long) query.getSingleResult();
	}
	
	public RegisteredUser getUser(String username) {
		Query query = em.createNamedQuery("RegisteredUser.getUserByUsername", getEntityClass());
		query.setParameter("username", username);
		return (RegisteredUser) query.getSingleResult();
	}
public List<RegisteredUser> getAll() {
		Query query = em.createNamedQuery("RegisteredUser.getAll", getEntityClass());
		return query.getResultList();
	}

	public void changePassword(String username, String[] hashCode) {
		long userID=getIDByUsername(username);
		RegisteredUser Entity = getEntity(userID);
		Entity.setHashcode(hashCode[0]);
		Entity.setSalt(hashCode[1]);
		editEntity(Entity);
	}
	
	public long count() {
		Query query = em.createNamedQuery("RegisteredUser.count");
		return (long) query.getSingleResult();
	}

	public boolean userExists(long id) {
		Query query = em.createNamedQuery("RegisteredUser.checkIfExists");
		query.setParameter("id", id);
		return (long) query.getSingleResult() == 1;
	}
	
	public boolean userExists(String username) {
		Query query = em.createNamedQuery("RegisteredUser.checkIfExistsByUsername");
		query.setParameter("username", username);
		return (long) query.getSingleResult() == 1;
	}
	
	public String isUsernameOrEmail(String usernameOrEmail) {
		Query query=em.createNamedQuery("RegisteredUser.checkIfUsername");
		query.setParameter("username", usernameOrEmail);
		if((long) query.getSingleResult() == 1) return "username";
		//return "email"; //porque a verificacao complementar faz-se a seguir.
		Query query2=em.createNamedQuery("RegisteredUser.checkIfEmail");
		query2.setParameter("email", usernameOrEmail);
		if((long) query2.getSingleResult() == 1) return "email";
		return "none";
	}
	
	public String getUsernameByEmail(String email) {
		Query query = em.createNamedQuery("RegisteredUser.getUsernameByEmail",String.class);
		query.setParameter("email", email);
		return (String) query.getSingleResult();
	}
}
