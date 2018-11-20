package es.grupoica.cyted.bbdd.mapper;

import es.grupoica.cyted.bbdd.model.ICAFlujoRol;

import java.util.List;
public interface ICAFlujoRolMapper {

	/**
	 *
	 * @return Lista de acciones para un idAccion concreto (Una �nica acci�n)
	 */
	public List<ICAFlujoRol> obtenerRolesFlujo(Integer idFlujo);

}