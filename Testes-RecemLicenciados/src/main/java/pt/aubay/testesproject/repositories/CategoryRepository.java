package pt.aubay.testesproject.repositories;

import java.util.List;

import javax.faces.bean.RequestScoped;
import javax.persistence.TypedQuery;

import pt.aubay.testesproject.models.entities.Category;

@RequestScoped
public class CategoryRepository extends Repositories<Category> {

	@Override
	protected Class<Category> getEntityClass() {
		return Category.class;
	}
	
	public Category getCategory(String category) {
		TypedQuery<Category> query = em.createNamedQuery("Category.getCategory", getEntityClass());
		query.setParameter("category", category);
		return (Category) query.getSingleResult();
	}
	
	public List<Category> getAll() {
		TypedQuery<Category> query = em.createNamedQuery("Category.getAll", getEntityClass());
		return query.getResultList();
	}	
	public long count() {
		TypedQuery<Long> query = em.createNamedQuery("Category.count", Long.class);
		return query.getSingleResult();
	}
	
	public boolean categoryExists(Category category) {
		TypedQuery<Long> query = em.createNamedQuery("Category.checkIfExists", Long.class);
		query.setParameter("category", category.getCategory());
		return query.getSingleResult() != 0;
	}
	
	public boolean idExists(Category category) {
		TypedQuery<Long> query = em.createNamedQuery("Category.checkIfIdExists", Long.class);
		query.setParameter("id", category.getId());
		return query.getSingleResult() == 1;
	}

	public boolean idExists(long id) {
		TypedQuery<Long> query = em.createNamedQuery("Category.checkIfIdExists", Long.class);
		query.setParameter("id",id);
		return query.getSingleResult() == 1;
	}
}
