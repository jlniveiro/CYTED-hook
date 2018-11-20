package es.grupoica.cyted.bbdd.service;

import es.grupoica.cyted.bbdd.mapper.ICATareasMapper;
import es.grupoica.cyted.bbdd.model.ICATarea;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class ICATareaService extends AbstractService {

	private static final Logger logger = LogManager.getLogger(ICATareaService.class.getName());

	public ICATareaService() {
		super();

		// TODO Auto-generated constructor stub

	}

	/**
	 * Obtiene la lista de tareas para una acci�n determinada
	 * @return
	 * @throws Exception
	 */
	public List<ICATarea> obtenerTarea(Integer idTarea) throws Exception {

		List<ICATarea> tareas = null;

		SqlSession session = this.getSqlSessionFactory().openSession();
		try {
			ICATareasMapper mapper = session.getMapper(ICATareasMapper.class);
			tareas = mapper.obtenerTarea(idTarea);

		}catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}finally {
			session.close();
		}

		return tareas;
	}

}