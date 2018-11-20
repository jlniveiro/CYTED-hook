package es.grupoica.cyted.bbdd.mapper;

import es.grupoica.cyted.bbdd.model.ICATarea;

import java.util.List;
public interface ICATareasMapper {

	/**
	 *
	 * @return Lista de tareas para un idTarea concreto (Una �nica tarea)
	 */
	public List<ICATarea> obtenerTarea(Integer idTarea);

}