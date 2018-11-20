package es.grupoica.cyted.bbdd.model;

import java.util.Date;
public class ICATarea {

	public String getDescripcion() {
		return descripcion;
	}

	public Date getFechaFinalizacion() {
		return fechaFinalizacion;
	}

	public Integer getIdTarea() {
		return idTarea;
	}

	public Long getPersonaAsignada() {
		return personaAsignada;
	}

	public Integer getPrioridad() {
		return prioridad;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public void setFechaFinalizacion(Date fechaFinalizacion) {
		this.fechaFinalizacion = fechaFinalizacion;
	}

	public void setIdTarea(Integer idTarea) {
		this.idTarea = idTarea;
	}

	public void setPersonaAsignada(Long personaAsignada) {
		this.personaAsignada = personaAsignada;
	}

	public void setPrioridad(Integer prioridad) {
		this.prioridad = prioridad;
	}

	private String descripcion;
	private Date fechaFinalizacion;
	private Integer idTarea;
	private Long personaAsignada;
	private Integer prioridad;

}