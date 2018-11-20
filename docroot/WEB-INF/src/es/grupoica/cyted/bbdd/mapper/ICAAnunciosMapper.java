package es.grupoica.cyted.bbdd.mapper;

import es.grupoica.cyted.bbdd.model.ICAAnuncio;

import java.util.List;
public interface ICAAnunciosMapper {

	/**
	 *
	 * @return Lista de anuncios para un idAnuncio concreto (Un �nico anuncio)
	 */
	public List<ICAAnuncio> obtenerAnuncio(Integer idAnuncio);

}