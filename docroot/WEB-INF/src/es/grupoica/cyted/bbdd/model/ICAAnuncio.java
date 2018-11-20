package es.grupoica.cyted.bbdd.model;

import java.util.Date;
public class ICAAnuncio {

	public String getContenido() {
		return contenido;
	}

	public Date getFechaExpiracion() {
		return fechaExpiracion;
	}

	public Integer getIdAnuncio() {
		return idAnuncio;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setContenido(String contenido) {
		this.contenido = contenido;
	}

	public void setFechaExpiracion(Date fechaExpiracion) {
		this.fechaExpiracion = fechaExpiracion;
	}

	public void setIdAnuncio(Integer idAnuncio) {
		this.idAnuncio = idAnuncio;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	private String contenido;
	private Date fechaExpiracion;
	private Integer idAnuncio;
	private String titulo;

}