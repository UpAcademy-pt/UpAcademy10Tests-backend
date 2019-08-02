package pt.aubay.testesproject.models.entities;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({
	 @NamedQuery(name="Questions.getQuestions", query="SELECT q FROM Questions q WHERE q.id=:id"),
	 @NamedQuery(name="Questions.getAll",query="SELECT q FROM Questions q"),
	 @NamedQuery(name="Questions.count", query = "SELECT COUNT(q.id) FROM Questions q"),
	 @NamedQuery(name="Questions.checkIfExists", query = "SELECT COUNT(q.id) FROM Questions q WHERE q.id =:id"),
})


public class Questions extends Models{
	private String enunciado;
	private String[][] resposta;
	public String getEnunciado() {
		return enunciado;
	}
	public void setEnunciado(String enunciado) {
		this.enunciado = enunciado;
	}
	public String[][] getResposta() {
		return resposta;
	}
	public void setResposta(String[][] resposta) {
		this.resposta = resposta;
	}
}
