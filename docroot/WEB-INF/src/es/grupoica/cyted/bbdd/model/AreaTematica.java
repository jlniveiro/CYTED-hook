package es.grupoica.cyted.bbdd.model;

import java.util.List;
public class AreaTematica {
	/*IdArea]
	, [NombreArea]
	, [Orden*/

	public Integer getIdArea() {
		return idArea;
	}

	public List<LineaInvestigacion> getLineasInvestigacion() {
		return lineasInvestigacion;
	}

	public String getNombreArea() {
		return nombreArea;
	}

	public Integer getOrden() {
		return orden;
	}

	public void setIdArea(Integer idArea) {
		this.idArea = idArea;
	}

	public void setLineasInvestigacion(List<LineaInvestigacion> lineasInvestigacion) {
		this.lineasInvestigacion = lineasInvestigacion;
	}

	public void setNombreArea(String nombreArea) {
		this.nombreArea = nombreArea;
	}

	public void setOrden(Integer orden) {
		this.orden = orden;
	}

	private Integer idArea;
	private List<LineaInvestigacion> lineasInvestigacion;
	private String nombreArea;
	private Integer orden;

}