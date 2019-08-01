package pt.aubay.testesproject.models.entities;

public class Questions extends Models{
	private String enunciado;
	private String resposta;
	private int id;
	public String getEnunciado() {
		return enunciado;
	}
	public void setEnunciado(String enunciado) {
		this.enunciado = enunciado;
	}
	public String getResposta() {
		return resposta;
	}
	public void setResposta(String resposta) {
		this.resposta = resposta;
	}
}
