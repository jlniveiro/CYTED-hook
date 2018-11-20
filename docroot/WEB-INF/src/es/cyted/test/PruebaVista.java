package es.cyted.test;

import java.util.List;

import es.grupoica.cyted.bbdd.model.VwUsuariosCyted;
import es.grupoica.cyted.bbdd.service.VwUsuariosCytedService;

public class PruebaVista {

	public static void main(String[] args) throws Exception {
		
		VwUsuariosCyted usuarioConectado = null;
		//Obtenemos sus datos
		VwUsuariosCytedService service = new VwUsuariosCytedService();
		List<VwUsuariosCyted> usuarios = service.obtenerUsuarioById(new Long(40206));

		if (usuarios != null && !usuarios.isEmpty()) {
			usuarioConectado = usuarios.get(0);
		}
		
		System.out.println("Nacionalidad: "  + usuarioConectado.getNacionalidad());

	}

}
