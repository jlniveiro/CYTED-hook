package es.grupoica.cyted.bbdd.mapper;

import es.grupoica.cyted.bbdd.model.AreaTematica;
import es.grupoica.cyted.bbdd.model.LineaInvestigacion;

import java.util.List;
public interface AreaTematicaMapper {

	public List<AreaTematica> obtenerArea(Integer idArea);

	/**
	 *
	 * @return lista de �reas tem�ticas
	 */
	public List<AreaTematica> obtenerAreas();

	public List<LineaInvestigacion> selectLineasForArea(Integer idArea);

}