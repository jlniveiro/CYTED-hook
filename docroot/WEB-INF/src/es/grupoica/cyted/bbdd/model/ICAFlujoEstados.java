package es.grupoica.cyted.bbdd.model;

/**
 * Recoge los datos de la entidad ICA_FlujoEstados
 * @author joseluis.niveiro
 *
 */
public class ICAFlujoEstados {

	public ICAFlujoEstados() {
		super();
	}

	public String getCondicion() {
		if (this.condicion == null) {
			this.condicion = "";
		}

		return condicion;
	}

	public String getEstadoDestino() {
		return estadoDestino;
	}

	public String getEstadoOrigen() {
		return estadoOrigen;
	}

	public Integer getIdAccion() {
		return idAccion;
	}

	public Integer getIdEstadoDestino() {
		return idEstadoDestino;
	}

	public Integer getIdEstadoOrigen() {
		return idEstadoOrigen;
	}

	public Long getIdEstructura() {
		return IdEstructura;
	}

	public Integer getIdFlujo() {
		return idFlujo;
	}

	public String getUrlOrigen() {
		if (this.urlOrigen == null) {
			this.urlOrigen = "";
		}

		return urlOrigen;
	}

	public void setCondicion(String condicion) {
		this.condicion = condicion;
	}

	public void setEstadoDestino(String estadoDestino) {
		this.estadoDestino = estadoDestino;
	}

	public void setEstadoOrigen(String estadoOrigen) {
		this.estadoOrigen = estadoOrigen;
	}

	public void setIdAccion(Integer idAccion) {
		this.idAccion = idAccion;
	}

	public void setIdEstadoDestino(Integer idEstadoDestino) {
		this.idEstadoDestino = idEstadoDestino;
	}

	public void setIdEstadoOrigen(Integer idEstadoOrigen) {
		this.idEstadoOrigen = idEstadoOrigen;
	}

	public void setIdEstructura(Long idEstructura) {
		IdEstructura = idEstructura;
	}

	public void setIdFlujo(Integer idFlujo) {
		this.idFlujo = idFlujo;
	}

	public void setUrlOrigen(String urlOrigen) {
		this.urlOrigen = urlOrigen;
	}

	private String condicion;
	private String estadoDestino;
	private String estadoOrigen;
	//FK
	private Integer idAccion;
	private Integer idEstadoDestino;
	private Integer idEstadoOrigen;
	private Long IdEstructura;
	private Integer idFlujo;
	private String urlOrigen;

}