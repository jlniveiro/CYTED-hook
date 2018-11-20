package es.grupoica.cyted.bbdd.service;

import es.grupoica.cyted.bbdd.mapper.ICAFlujoRolMapper;
import es.grupoica.cyted.bbdd.model.ICAFlujoRol;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class ICAFlujoRolService extends AbstractService {

	private static final Logger logger = LogManager.getLogger(ICAFlujoRolService.class.getName());

	public ICAFlujoRolService() {
		super();

		// TODO Auto-generated constructor stub

	}

	/**
	 * Obtiene la lista de acciones para una acci�n determinada
	 * @return
	 * @throws Exception
	 */
	public List<ICAFlujoRol> obtenerRolesFlujo(Integer idFlujo) throws Exception {

		List<ICAFlujoRol> acciones = null;

		SqlSession session = this.getSqlSessionFactory().openSession();
		try {
			ICAFlujoRolMapper mapper = session.getMapper(ICAFlujoRolMapper.class);
			acciones = mapper.obtenerRolesFlujo(idFlujo);

		}catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}finally {
			session.close();
		}

		return acciones;
	}

}