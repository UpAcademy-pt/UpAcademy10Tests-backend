package pt.aubay.testesproject.models.entities;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass//Classe abstract...anotação para distinguir este tipo de classe das outras
public class Models implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected long id;
	
//	public Models() {
//		//super();
//	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
