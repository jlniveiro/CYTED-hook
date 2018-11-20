package es.grupoica.cyted.util;

import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portlet.journal.model.JournalArticle;

import es.grupoica.cyted.bbdd.model.VwUsuariosCyted;
import es.grupoica.cyted.bbdd.service.VwUsuariosCytedService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class UsuarioUtil {

	/**
	 * Obtiene los datos del Journal de Usuario referentes al usuario autenticado
	 * @param serviceContext
	 * @return
	 * @throws Exception
	 */
	public VwUsuariosCyted getDatosUsuarioAutenticado(ServiceContext serviceContext) throws Exception {

		VwUsuariosCyted usuarioConectado = null;

		//Obtenemos el usuario autenticado
		User usuario = getUsuarioSession(serviceContext);
		
		//Obtenemos sus datos
		VwUsuariosCytedService service = new VwUsuariosCytedService();
		List<VwUsuariosCyted> usuarios = service.obtenerUsuarioById(usuario.getUserId());

		if (usuarios != null && !usuarios.isEmpty()) {
			usuarioConectado = usuarios.get(0);
		}

		return usuarioConectado;
	}
	
	
	/**
	 * Obtiene el User autenticado.
	 * @param serviceContext
	 * @return
	 * @throws Exception
	 */
	public User getUsuarioSession(ServiceContext serviceContext) throws Exception {
		User usuario = null;
		Long userId = serviceContext.getUserId();
		usuario = UserLocalServiceUtil.getUserById(userId);

		return usuario;
	}
	
	/**
	 * Obtiene el User con el ID especificado
	 * @param idUsuario
	 * @return
	 * @throws Exception
	 */
	public static User getUsuario(Long idUsuario) throws Exception {
		User usuario = null;
		usuario = UserLocalServiceUtil.getUserById(idUsuario);

		return usuario;
	}
	
	/**
	 * Obtiene el id del pais del usuario
	 * @param serviceContext
	 * @return
	 * @throws Exception
	 */
	public String getPaisUsuarioAutenticado(ServiceContext serviceContext) throws Exception {

		String pais = "";
		//Obtenemos el usuario autenticado
		User usuario = getUsuarioSession(serviceContext);
		Map<String, String> mapaCondiciones = new HashMap<String, String>();
		
		String datosAdicionales = usuario.getComments();
		
		String[] condiciones = datosAdicionales.split(",");
		//Obtenemos los valores y cargamos el mapa

		for (int c = 0; c < condiciones.length; c++) {
			String[] cond = condiciones[c].split("=");

			if (cond.length == 2) {
				mapaCondiciones.put(cond[0], cond[1]);
			}
		}
		
		for (Map.Entry<String, String> entry : mapaCondiciones.entrySet()) {
			if (entry.getKey().equals("nacionalidad")){
				pais = entry.getValue();
			}
			
		}

				
		return pais;
	}
	
	
	/**
	 * Obtiene el id del área del usuario
	 * @param serviceContext
	 * @return
	 * @throws Exception
	 */
	public static String getAreaUsuario (User usuario) throws Exception {

		String area = "0";
		Map<String, String> mapaCondiciones = new HashMap<String, String>();
		
		String datosAdicionales = usuario.getComments();
		
		String[] condiciones = datosAdicionales.split(",");
		//Obtenemos los valores y cargamos el mapa

		for (int c = 0; c < condiciones.length; c++) {
			String[] cond = condiciones[c].split("=");

			if (cond.length == 2) {
				mapaCondiciones.put(cond[0], cond[1]);
			}
		}
		
		for (Map.Entry<String, String> entry : mapaCondiciones.entrySet()) {
			if (entry.getKey().equals("idArea")){
				area = entry.getValue();
			}
			
		}

				
		return area;
	}
	
	/**
	 * Obtiene los evaluadores de una propuesta
	 * @param article
	 * @return
	 */
	public static List<User> getEvaluadoresPropuesta (JournalArticle article){
		List<User> evaluadores = new ArrayList<User>();
		
		try
		{
			List<Node> nodos = JournalUtil.getNode("Evaluaciones", article, LocaleUtil.getDefault().toString());

			for (Node nodo : nodos) {
				Node evaluador = nodo.selectSingleNode("dynamic-element[@name=\"evaluador\"]/dynamic-content");
				String value = evaluador.getText();

				if (value != null && !value.isEmpty()) {
					User usuario = UsuarioUtil.getUsuario(Long.valueOf(value));
					evaluadores.add(usuario);
				}
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return evaluadores;
	}

}