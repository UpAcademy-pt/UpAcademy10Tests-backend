package pt.aubay.testesproject.repositories;

import java.util.List;

import javax.faces.bean.RequestScoped;
import javax.persistence.Query;

@RequestScoped
public class UserRepositories extends Repositories<User> {

	@Override
	protected Class<User> getEntityClass() {
		// TODO Auto-generated method stub
		return User.class;
	}
	
	public User getUser(String username) {
		return em.find(getEntityClass(),username);	
	}
	
	
//Metodos que podem ser uteis - falta fazer a query correspondente 	
	
//	public List<User> getAll() {
//
//		Query query = em.createNamedQuery("User.getAll", getEntityClass());
//		return  query.getResultList();
//	}
//	
//	public long count() {
//		Query query = em.createNamedQuery("User.count");
//		return (long)query.getSingleResult();
//	}
//	
//	public boolean productExists(long id) {	
//		Query query = em.createNamedQuery("User.checkIfExists");
//		query.setParameter("id", id);
//		return (long)query.getSingleResult() == 1;
//	}

}
