package es.grupoica.cyted.bbdd.model;

public class LineaInvestigacion {

	public String getAnio() {
		return anio;
	}

	public Boolean getEstado() {
		return estado;
	}

	public Long getIdArea() {
		return idArea;
	}

	public Long getIdLinea() {
		return idLinea;
	}

	public String getNombre() {
		return nombre;
	}

	public Integer getOrden() {
		return orden;
	}

	public void setAnio(String anio) {
		this.anio = anio;
	}

	public void setEstado(Boolean estado) {
		this.estado = estado;
	}

	public void setIdArea(Long idArea) {
		this.idArea = idArea;
	}

	public void setIdLinea(Long idLinea) {
		this.idLinea = idLinea;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public void setOrden(Integer orden) {
		this.orden = orden;
	}

	private String anio;
	private Boolean estado;
	private Long idArea;
	private Long idLinea;
	private String nombre;
	private Integer orden;

}