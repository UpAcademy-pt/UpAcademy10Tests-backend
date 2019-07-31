package pt.aubay.testesproject.repositories;

import java.util.List;

import javax.faces.bean.RequestScoped;
import javax.persistence.Query;

import pt.aubay.testesproject.models.User;

@RequestScoped
public class UserRepositories extends Repositories<User> {

	@Override
	protected Class<User> getEntityClass() {
		// TODO Auto-generated method stub
		return User.class;
	}

	public User getUser(String username) {
		Query query = em.createNamedQuery("User.getUserByUsername", getEntityClass());
		query.setParameter("username", username);
		return (User) query.getSingleResult();
	}

	public List<User> getAll() {
		Query query = em.createNamedQuery("User.getAll", getEntityClass());
		return query.getResultList();
	}

	public long count() {
		Query query = em.createNamedQuery("User.count");
		return (long) query.getSingleResult();
	}

	public boolean userExists(long id) {
		Query query = em.createNamedQuery("User.checkIfExists");
		query.setParameter("id", id);
		return (long) query.getSingleResult() == 1;
	}
	
	public boolean userExists(String username) {
		Query query = em.createNamedQuery("User.checkIfExistsByUsername");
		query.setParameter("username", username);
		return (long) query.getSingleResult() == 1;
	}

}
