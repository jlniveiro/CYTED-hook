package es.grupoica.cyted.bbdd.mapper;

import es.grupoica.cyted.bbdd.model.VwUsuariosCyted;

import java.util.List;
public interface VwUsuariosCytedMapper {

	public List<VwUsuariosCyted> obtenerUsuarioById(Long idUsuario);

}