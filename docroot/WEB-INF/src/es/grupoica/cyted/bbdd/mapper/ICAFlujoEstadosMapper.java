package es.grupoica.cyted.bbdd.mapper;

import es.grupoica.cyted.bbdd.model.ICAFlujoEstados;

import java.util.List;
public interface ICAFlujoEstadosMapper {

	/**
	 *
	 * @return Obtiene los flujos para la estructura y estado actual.
	 */
	public List<ICAFlujoEstados> obtenerFlujosEstadoActual(ICAFlujoEstados flujoEstados);

	/**
	 *
	 * @return Lista de flujos de una estructura (lista completa)
	 */
	public List<ICAFlujoEstados> obtenerFlujosEstructura(Long idEstructura);

}