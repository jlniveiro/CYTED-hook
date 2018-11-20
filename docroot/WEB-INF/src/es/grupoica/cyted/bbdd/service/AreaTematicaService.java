package es.grupoica.cyted.bbdd.service;

import es.grupoica.cyted.bbdd.mapper.AreaTematicaMapper;
import es.grupoica.cyted.bbdd.model.AreaTematica;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
public class AreaTematicaService extends AbstractService {

	public AreaTematicaService() {
		super();
	}

	/**
	 * Obtiene el �rea para el idArea determinado
	 * @return
	 * @throws Exception
	 */
	public List<AreaTematica> obtenerArea(Integer idArea) throws Exception {

		List<AreaTematica> areas = null;

		SqlSession session = this.getSqlSessionFactory().openSession();
		try {
			AreaTematicaMapper mapper = session.getMapper(AreaTematicaMapper.class);
			areas = mapper.obtenerArea(idArea);

		}catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}finally {
			session.close();
		}

		return areas;
	}

	/**
	 *
	 * @return
	 * @throws Exception
	 */
	public List<AreaTematica> obtenerAreasTematicas() throws Exception {

		List<AreaTematica> areas = null;

		SqlSession session = this.getSqlSessionFactory().openSession();
		try {
			AreaTematicaMapper mapper = session.getMapper(AreaTematicaMapper.class);
			areas = mapper.obtenerAreas();

		}catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}finally {
			session.close();
		}

		return areas;
	}

}