package pt.aubay.testesproject.repositories;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import pt.aubay.testesproject.models.Models;

public abstract class Repositories<T extends Models> {
	
	@PersistenceContext(unitName = "database")
	protected EntityManager em;
	
	protected abstract Class<T> getEntityClass();
	
	
	public void addEntity(T entity) {
		em.merge(entity);
	}
	
	public T getEntity(long id) {
		return em.find(getEntityClass(),id);	
	}
	
	
	public void updateEntity(long id, T entity) {
		entity.setId(id);
		em.merge(entity);
	}
	
	public void deleteEntity(long id) {
		T temp = em.find(getEntityClass(), id);
		em.remove(temp);
	}
	

}
