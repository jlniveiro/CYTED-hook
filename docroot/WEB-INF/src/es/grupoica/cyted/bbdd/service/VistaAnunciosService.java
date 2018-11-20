package es.grupoica.cyted.bbdd.service;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import es.grupoica.cyted.bbdd.mapper.VistaAnunciosMapper;
import es.grupoica.cyted.bbdd.model.VistaAnuncios;

public class VistaAnunciosService extends AbstractService {
	
	private static final Logger logger = LogManager.getLogger(VistaAnunciosService.class.getName());

	public VistaAnunciosService() {
		super();

		// TODO Auto-generated constructor stub

	}

	/**
	 * Obtiene la lista de anuncios para una acci�n determinada
	 * @return
	 * @throws Exception
	 */
	public List<VistaAnuncios> obtenerAnuncio(Integer idAnuncio) throws Exception {

		List<VistaAnuncios> anuncios = null;

		SqlSession session = this.getSqlSessionFactory().openSession();
		try {
			VistaAnunciosMapper mapper = session.getMapper(VistaAnunciosMapper.class);
			anuncios = mapper.obtenerAnuncio(idAnuncio);

		}catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}finally {
			session.close();
		}

		return anuncios;
	}


}
