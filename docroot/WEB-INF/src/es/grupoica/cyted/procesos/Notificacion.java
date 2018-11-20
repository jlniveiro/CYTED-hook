package es.grupoica.cyted.procesos;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.RoleConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.security.auth.PrincipalThreadLocal;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.security.permission.PermissionThreadLocal;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.RoleServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portlet.asset.model.AssetEntry;
import com.liferay.portlet.asset.service.AssetEntryLocalServiceUtil;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;

import es.grupoica.cyted.bbdd.model.ICAFlujoEstados;
import es.grupoica.cyted.bbdd.model.ICAFlujoRol;
import es.grupoica.cyted.bbdd.model.ICATarea;
import es.grupoica.cyted.bbdd.model.VistaAnuncios;
import es.grupoica.cyted.bbdd.service.ICAFlujoEstadosService;
import es.grupoica.cyted.bbdd.service.ICAFlujoRolService;
import es.grupoica.cyted.bbdd.service.ICATareaService;
import es.grupoica.cyted.bbdd.service.VistaAnunciosService;
import es.grupoica.cyted.util.AnuncioUtil;
import es.grupoica.cyted.util.Constantes;
import es.grupoica.cyted.util.JournalUtil;
import es.grupoica.cyted.util.RolUtil;
import es.grupoica.cyted.util.TareaUtil;
import es.grupoica.cyted.util.UsuarioUtil;


public class Notificacion extends ProcesoBase {
	
	protected JournalUtil journalUtil = new JournalUtil();
	private static final Logger logger = LogManager.getLogger(Notificacion.class.getName());
	protected AnuncioUtil anuncioUtil = new AnuncioUtil();
	protected RolUtil rolUtil = new RolUtil();
	protected VistaAnunciosService serviceAnuncio = new VistaAnunciosService();
	protected ICAFlujoRolService serviceFlujoRol = new ICAFlujoRolService();
	protected ICAFlujoEstadosService serviceFlujos = new ICAFlujoEstadosService();
	protected ICATareaService serviceTarea = new ICATareaService();
	protected TareaUtil tareaUtil = new TareaUtil();
	
	
	public Notificacion(ServiceContext serviceContext) {
		super(serviceContext);
	}
	
	/**
	 * Tratamiento para la creaciï¿½n de una nueva solicitud
	 * @param article
	 * @param structureId
	 * @return
	 * @throws Exception
	 */
	public JournalArticle procesarNuevaNotificacion(JournalArticle article) throws Exception {

		//En la nueva notificación incoporamos los cambios necesarios.
		
		//FECHA DE ENVÍO
		//Obtenemos el valor actual de la fecha de envío
		String fechaEnvio = JournalUtil.getRootParseValue("Fecha_envio", article, LocaleUtil.getDefault().toString());
		
		if (fechaEnvio == null || fechaEnvio.trim().isEmpty()){
			JournalUtil.setParseValue("Fecha_envio", article, LocaleUtil.getDefault().toString(), String.valueOf(Calendar.getInstance().getTimeInMillis()) );
		}
		
				
		//ASSETENTRY_RELACIONADO
		Object assetRelacionado = this.getServiceContext().getRequest().getSession().getAttribute("ICA_assetEntryId");
				
		//Obtenemos el valor actual del AssetEntry_Relacionado.
		String assetEntryRelacionadoActual = JournalUtil.getRootParseValue("AssetEntry_Relacionado", article, LocaleUtil.getDefault().toString());
		
		//Si ya contiene un valor de Asset Entry Relacionado, no lo actualizamos, pues dicho valor será el generado por una 
		//notificación automática.
		if (assetEntryRelacionadoActual == null || assetEntryRelacionadoActual.trim().isEmpty()){
			JournalUtil.setParseValue("AssetEntry_Relacionado", article, LocaleUtil.getDefault().toString(), (assetRelacionado == null)? "" : assetRelacionado.toString());
		}
		
		//ICA_idArea de Session
		Object ICAidArea = this.getServiceContext().getRequest().getSession().getAttribute("ICA_idArea");
						
		//Obtenemos el valor actual del idArea.
		//String idAreaActual = JournalUtil.getRootParseValue("idArea", article, LocaleUtil.getDefault().toString());
				
		//Si tenemos un valor de área en Session, actualizamos la notificación con dicho valor. 
		if (ICAidArea != null && !ICAidArea.toString().isEmpty()){
			JournalUtil.setParseValue("idArea", article, LocaleUtil.getDefault().toString(), (ICAidArea == null)? "" : ICAidArea.toString());
		}
		
			
		//OriginadorPersona
		JournalUtil.setParseValue("OriginadorPersona", article, LocaleUtil.getDefault().toString(), String.valueOf(article.getUserId()));
				
		//Identificador
		JournalUtil.setParseValue("Identificador", article, LocaleUtil.getDefault().toString(), String.valueOf(article.getResourcePrimKey()));
		
		//Título del articulo
		Map<Locale,String> titleMap = new HashMap<Locale,String>();
		String newTitle = JournalUtil.getRootParseValue("Identificador", article, LocaleUtil.getDefault().toString()) + "-" + 
						  JournalUtil.getRootParseValue("Asunto", article, LocaleUtil.getDefault().toString());
		//Añadimos
		titleMap.put(LocaleUtil.getDefault(), newTitle);
		article.setTitleMap(titleMap);
		article.setUrlTitle(newTitle);
				
		//OriginadorRol
		String originadorRol = JournalUtil.getRootParseValue("OriginadorRol", article, LocaleUtil.getDefault().toString());
		if (originadorRol == null || originadorRol.trim().isEmpty()){
			List<Role> roles = RoleLocalServiceUtil.getUserGroupRoles(article.getUserId(), article.getGroupId());
			for (Role rol : roles){
				if (rol.getType() == RoleConstants.TYPE_SITE){
					JournalUtil.setParseValue("OriginadorRol", article, LocaleUtil.getDefault().toString(), String.valueOf(rol.getRoleId()));
					originadorRol = String.valueOf(rol.getRoleId());
				}
			}
			if (originadorRol == null){
				JournalUtil.setParseValue("OriginadorRol", article, LocaleUtil.getDefault().toString(), "" );
			}
		}
		
		//Actualizamos
		JournalArticleLocalServiceUtil.updateJournalArticle(article);
	
		return article;
	}
	
	
	/**
	 * Envía las notificaciones correspondientes al flujo obtenido
	 * @param article
	 * @param structureId
	 * @param flujoSiguiente
	 * @return
	 * @throws Exception 
	 */
	public JournalArticle procesarNotificacionesAutomaticas(JournalArticle article, Long structureId, ICAFlujoEstados flujoSiguiente) throws Exception{
		
		//OBTENEMOS LOS ROLES A LOS QUE AFECTA EL FLUJO OBTENIDO
		List<ICAFlujoRol> rolesFlujo = serviceFlujoRol.obtenerRolesFlujo(flujoSiguiente.getIdFlujo());

		//Creamos un mapa de objetos anuncio a enviar, para contener los distintos anuncios a enviar y reutilizarlos
		//en los sucesivos envios y no crearlos siempre
		Map<Integer, VistaAnuncios> mapaAnuncios = new HashMap<Integer, VistaAnuncios>();
		Map<Integer, ICATarea> mapaTareas = new HashMap<Integer, ICATarea>();
		VistaAnuncios anuncioEnviar = null;
		//ICATarea tareaEnviar = null;

		//Recorremos la lista de ROLES y para cada uno de ellos enviamos la notificación que corresponda
		for (ICAFlujoRol rol : rolesFlujo) {
			//PARA CADA UNO DE ELLOS OBTENEMOS LOS ANUNCIOS A ENVIAR.
			//Verificamos si ya ha sido tratado anteriormente para este Journal
			anuncioEnviar = mapaAnuncios.get(rol.getIdAnuncio());

			if (anuncioEnviar == null) {
				//NO SE HA TRATADO ESTE ANUNCIO PARA EL JOURNAL
				//Obtenemos los datos del anuncio a componer
				List<VistaAnuncios> anuncios = serviceAnuncio.obtenerAnuncio(rol.getIdAnuncio());
				//Si hemos recibido anuncio
				if ( ! anuncios.isEmpty()) {
					//Componemos el anuncio
					anuncioEnviar = anuncioUtil.componerAnuncio(article, anuncios.get(0));
					//Lo añadimos al mapa
					mapaAnuncios.put(rol.getIdAnuncio(), anuncioEnviar);
				}
			}
			
			//Establecemos permisos
			PrincipalThreadLocal.setName(article.getUserId());
			PermissionChecker permissionChecker = PermissionCheckerFactoryUtil.create(UserLocalServiceUtil.getUser(article.getUserId()));
			PermissionThreadLocal.setPermissionChecker(permissionChecker);

			//Obtenemos los datos del Rol destinatario
			Long rolDestino = Long.valueOf(rol.getIdRolDestino());
			//Obtenemos los usuarios con el rol destino que cumplan las condiciones
			if (rolDestino != null){
				//En caso de GESTOR y VOCAL de ÁREA
				Long rolId = rolDestino;
				List<User> usuarios = new ArrayList<User>();
				//Obtenemos los usuarios del área afectada
				if ( (rolId.equals(Constantes.ROL_GESTOR_AREA) || rolId.equals(Constantes.ROL_VOCAL_AREA)) 
						&& rol.getCondicion().equals("areaTematica") ){
					//Obtenemos los usuarios que sean gestores o vocales del área 
					List<User> usuariosRol = rolUtil.getUsersByRole(rol.getIdRolDestino(), article.getGroupId());
					
					//Recorremos los usuarios
					for (User usuario : usuariosRol){
						//De los usuarios del ROL, obtenemos los del área del Journal a tratar
						if (structureId.equals(Constantes.ESTRUCTUREID_SOLICITUD_RT) || structureId.equals(Constantes.ESTRUCTUREID_SOLICITUD_PE) ||
								structureId.equals(Constantes.ESTRUCTUREID_SOLICITUD_LANDING) ){
							String areaTematica = JournalUtil.getRootParseValue("areaTematica", article, LocaleUtil.getDefault().toString());
							if (areaTematica != null){
								//Obtenemos el área del usuario.
								String areaUsuario = UsuarioUtil.getAreaUsuario(usuario);
								//Si el área del usuario coincide con la del Journal a tratar.
								//añadimos el usuario a la lista de usuarios a notificar.
								if ( areaUsuario.trim().equals(areaTematica.trim()) ){
									usuarios.add(usuario);
								}//fin if
							}//fin if (areaTematica != null)
						}
					}//fin for 
				}//fin if (rolId.equals(Constantes.ROL_GESTOR_AREA) || rolId.equals(Constantes.ROL_VOCAL_AREA))
				
				else if ( rolId.equals(Constantes.ROL_SOLICITANTE) && rol.getCondicion().equals("solicitante") ) {
					//Obtenemos el User del solicitante
					//Obtenemos el propietario de la solicitud
					AssetEntry assetEntry = AssetEntryLocalServiceUtil.getEntry("com.liferay.portlet.journal.model.JournalArticle", article.getResourcePrimKey());
					User usuario = UsuarioUtil.getUsuario(assetEntry.getUserId());
					//Añadimos a lista de Usuarios
					usuarios.add(usuario);
				}
				else if ( rolId.equals(Constantes.ROL_EVALUADOR)) {
					//Obtenemos los usuarios que deben evaluar la propuesta para notificarles el acceso
					usuarios = UsuarioUtil.getEvaluadoresPropuesta(article);
				}
				
				
				//si no hay usuarios, la enviamos la notificación al ROL
				if (usuarios.isEmpty()){
					//Enviamos la notificación al rol destino pues no hay notificaciones automáticas para usuarios.
					anuncioUtil.enviarNotificacion(article, anuncioEnviar, rolDestino, null, this.getServiceContext());
				}
				else {
					//Para cada usuario, enviamos el anuncio
					for (User usuario : usuarios) {
						//Enviamos el anuncio
						anuncioUtil.enviarNotificacion(article, anuncioEnviar, null, usuario, this.getServiceContext());
						//Si tiene tarea, la enviamos
						//tareaUtil.enviarTarea(newArticle, tareaEnviar, usuario);
					}
				}//fin if (usuarios.isEmpty())
			}//fin if (rolDestino != null)
			
			
		}//fin for (ICAFlujoRol rol : rolesFlujo)
		
		
		
		return article;
	}

}
