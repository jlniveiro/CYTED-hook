package es.grupoica.cyted.bbdd.model;

public class ICAFlujoRol {

	public Integer getIdAnuncio() {
		return idAnuncio;
	}

	public Integer getIdFlujo() {
		return idFlujo;
	}

	public Integer getIdRolDestino() {
		return idRolDestino;
	}

	public Integer getIdTarea() {
		return idTarea;
	}

	public void setIdAnuncio(Integer idAnuncio) {
		this.idAnuncio = idAnuncio;
	}

	public void setIdFlujo(Integer idFlujo) {
		this.idFlujo = idFlujo;
	}

	public void setIdRolDestino(Integer idRolDestino) {
		this.idRolDestino = idRolDestino;
	}

	public void setIdTarea(Integer idTarea) {
		this.idTarea = idTarea;
	}
	
	public String getCondicion() {
		return condicion;
	}

	public void setCondicion(String condicion) {
		this.condicion = condicion;
	}



	private Integer idAnuncio;
	private Integer idFlujo;
	private Integer idRolDestino;
	private Integer idTarea;
	private String condicion;

}