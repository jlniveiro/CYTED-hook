package es.grupoica.cyted.bbdd.mapper;

import java.util.List;

import es.grupoica.cyted.bbdd.model.VistaAnuncios;

public interface VistaAnunciosMapper {

	/**
	 *
	 * @return Lista de anuncios para un idAnuncio concreto 
	 */
	public List<VistaAnuncios> obtenerAnuncio(Integer idAnuncio);

}
