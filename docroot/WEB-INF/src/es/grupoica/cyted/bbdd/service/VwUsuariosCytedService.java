package es.grupoica.cyted.bbdd.service;

import es.grupoica.cyted.bbdd.mapper.VwUsuariosCytedMapper;
import es.grupoica.cyted.bbdd.model.VwUsuariosCyted;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
public class VwUsuariosCytedService extends AbstractService {

	/**
	 * Obtiene el usuarios para un Id de usuario dado.
	 * @return
	 * @throws Exception
	 */
	public List<VwUsuariosCyted> obtenerUsuarioById(Long idUsuario) throws Exception {

		List<VwUsuariosCyted> usuarios = null;

		SqlSession session = this.getSqlSessionFactory().openSession();
		try {
			VwUsuariosCytedMapper mapper = session.getMapper(VwUsuariosCytedMapper.class);
			usuarios = mapper.obtenerUsuarioById(idUsuario);

		}catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}finally {
			session.close();
		}

		return usuarios;
	}

}