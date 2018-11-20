package es.grupoica.cyted.bbdd.service;

import es.grupoica.cyted.bbdd.mapper.ICAFlujoEstadosMapper;
import es.grupoica.cyted.bbdd.model.ICAFlujoEstados;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/*
 * https://www.concretepage.com/mybatis-3/getting-started-with-mybatis-3-crud-operations-example-with-xml-mapper
 */
public class ICAFlujoEstadosService extends AbstractService {
	
	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger(ICAFlujoEstadosService.class.getName());

	public ICAFlujoEstadosService() {
		super();
	}

	/**
	 * Obtiene los flujos para el estado actual del Journal
	 * @return
	 * @throws Exception
	 */
	public List<ICAFlujoEstados> obtenerFlujosEstadoActual(ICAFlujoEstados flujoEstados) throws Exception {

		List<ICAFlujoEstados> flujos = null;

		SqlSession session = this.getSqlSessionFactory().openSession();
		try {
			ICAFlujoEstadosMapper mapper = session.getMapper(ICAFlujoEstadosMapper.class);
			flujos = mapper.obtenerFlujosEstadoActual(flujoEstados);

		}catch (Exception ex) {
			ex.printStackTrace();
			logger.error("ERROR: " + ex.fillInStackTrace().toString());
			throw ex;
		}finally {
			session.close();
		}

		return flujos;
	}

	/**
	 * Obtiene la lista de flujos de la estructura especificada
	 * @return
	 * @throws Exception
	 */
	public List<ICAFlujoEstados> obtenerICAFlujosEstructura(Long idEstructura) throws Exception {

		List<ICAFlujoEstados> flujos = null;

		SqlSession session = this.getSqlSessionFactory().openSession();
		try {
			ICAFlujoEstadosMapper mapper = session.getMapper(ICAFlujoEstadosMapper.class);
			flujos = mapper.obtenerFlujosEstructura(idEstructura);

		}catch (Exception ex) {
			ex.printStackTrace();
			logger.error("ERROR: " + ex.fillInStackTrace().toString());
			throw ex;
		}finally {
			session.close();
		}

		return flujos;
	}

	

}