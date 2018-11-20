package es.grupoica.cyted.bbdd.service;

import es.grupoica.cyted.bbdd.mapper.ICAAnunciosMapper;
import es.grupoica.cyted.bbdd.model.ICAAnuncio;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class ICAAnuncioService extends AbstractService {

	private static final Logger logger = LogManager.getLogger(ICAFlujoRolService.class.getName());

	public ICAAnuncioService() {
		super();

		// TODO Auto-generated constructor stub

	}

	/**
	 * Obtiene la lista de anuncios para una acci�n determinada
	 * @return
	 * @throws Exception
	 */
	public List<ICAAnuncio> obtenerAnuncio(Integer idAnuncio) throws Exception {

		List<ICAAnuncio> acciones = null;

		SqlSession session = this.getSqlSessionFactory().openSession();
		try {
			ICAAnunciosMapper mapper = session.getMapper(ICAAnunciosMapper.class);
			acciones = mapper.obtenerAnuncio(idAnuncio);

		}catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}finally {
			session.close();
		}

		return acciones;
	}

}