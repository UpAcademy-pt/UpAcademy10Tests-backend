package pt.aubay.testesproject.models.entities;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({
	 @NamedQuery(name="Category.getCategory", query="SELECT q FROM Category q WHERE q.id=:id"),
	 @NamedQuery(name="Category.getAll",query="SELECT q FROM Category q"),
	 @NamedQuery(name="Category.count", query = "SELECT COUNT(q.id) FROM Category q"),
	 @NamedQuery(name="Category.checkIfExists", query = "SELECT COUNT(q.id) FROM Category q WHERE q.category =:category"),
	 @NamedQuery(name="Category.checkIfIdExists", query = "SELECT COUNT(q.id) FROM Category q WHERE q.id =:id")
})
public class Category extends Models{
	private String category;

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
}
