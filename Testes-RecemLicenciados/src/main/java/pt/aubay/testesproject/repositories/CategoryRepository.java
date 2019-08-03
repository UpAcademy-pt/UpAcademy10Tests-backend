package pt.aubay.testesproject.repositories;

import java.util.List;

import javax.faces.bean.RequestScoped;
import javax.persistence.Query;

import pt.aubay.testesproject.models.entities.Category;

@RequestScoped
public class CategoryRepository extends Repositories<Category>{

	@Override
	protected Class<Category> getEntityClass() {
		return Category.class;
	}
	
	public long getCategory(long id) {
		Query query = em.createNamedQuery("Category.getCategory", getEntityClass());
		query.setParameter("id", id);
		return (long) query.getSingleResult();
	}
	
	public List<Category> getAll() {
		Query query = em.createNamedQuery("Category.getAll", getEntityClass());
		return query.getResultList();
	}	
	public long count() {
		Query query = em.createNamedQuery("Category.count");
		return (long) query.getSingleResult();
	}
	
	public boolean categoryExists(Category category) {
		Query query = em.createNamedQuery("Category.checkIfExists");
		query.setParameter("category", category.getCategory());
		return (long) query.getSingleResult() == 1;
	}
	
	public boolean idExists(Category category) {
		Query query = em.createNamedQuery("Category.checkIfIdExists");
		query.setParameter("id", category.getId());
		return (long) query.getSingleResult() == 1;
	}

}
