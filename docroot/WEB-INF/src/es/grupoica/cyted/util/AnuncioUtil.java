package es.grupoica.cyted.util;

import com.liferay.counter.service.CounterLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.RoleConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portlet.announcements.model.AnnouncementsEntry;
import com.liferay.portlet.announcements.service.persistence.AnnouncementsEntryPersistence;
import com.liferay.portlet.announcements.service.persistence.AnnouncementsEntryUtil;
import com.liferay.portlet.asset.model.AssetEntry;
import com.liferay.portlet.asset.service.AssetEntryLocalServiceUtil;
import com.liferay.portlet.dynamicdatamapping.model.DDMStructure;
import com.liferay.portlet.dynamicdatamapping.service.DDMStructureLocalServiceUtil;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;

import es.grupoica.cyted.bbdd.model.VistaAnuncios;
import es.grupoica.cyted.contenido.ContentGeneratorImpl;
import es.grupoica.cyted.contenido.FieldValidationException;
import es.grupoica.cyted.contenido.campos.JournalValueGetter;
import es.grupoica.cyted.contenido.campos.ValueGetter;
import es.grupoica.cyted.contenido.campos.ValueGetterFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.portlet.PortletException;
import javax.portlet.PortletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class AnuncioUtil {
	
	
	private ServiceContext serviceContext = null;
	
	
	public VistaAnuncios componerAnuncio(JournalArticle journalArticle, VistaAnuncios anuncioBase) {

		VistaAnuncios anuncioAEnviar = anuncioBase;

		//En primer lugar obtenemos la lista de campos a cumplimentar en el anuncio
		List<String> campos = getListaCamposAnuncio(anuncioBase.getContenido());
		//Reemplazamos los valores de las variables sobre el contenido actual
		String contenidoFormateado = anuncioBase.getContenido();

		for (String campo : campos) {
			String valorCampo = JournalUtil.getRootParseValue(campo, journalArticle, LocaleUtil.getDefault().toString());
			if (valorCampo != null){
				contenidoFormateado = contenidoFormateado.replace("${" + campo + "}", valorCampo);
			}
		}

		//El valor formateado lo incorporamos al anuncio a enviar
		anuncioAEnviar.setContenido(contenidoFormateado);

		return anuncioAEnviar;
	}

	public void enviarAnuncio(JournalArticle journalArticle, VistaAnuncios anuncioEnviar, Role role, User user) throws SystemException {

		//Recogemos los datos necesarios para crear el anuncio
		//Usuario actual (el que ha publicado el artï¿½culo).
		long userId = user.getUserId();

		//dia actual
		Calendar calHoy = Calendar.getInstance();
		//dia de expiracion
		Calendar calExpire = Calendar.getInstance();
		calExpire.add(Calendar.MONTH, 2);

		//ClassNameId del anuncio a crear
		//EJEMPLO DE ROL
		long classNameId = role.getClassNameId();
		long classPK = role.getClassPK();

		String title = anuncioEnviar.getTitulo();

		/*
		 * CREAMOS EL ANUNCIO (FORMA 1) (ALARMA)
		 */
		AnnouncementsEntry nuevoAnuncio = null;
		AnnouncementsEntryPersistence anunPers = AnnouncementsEntryUtil.getPersistence();
		//Obtenemos el ID del artï¿½culo a crear
		long id = CounterLocalServiceUtil.getService().increment();
		logger.info("Id obtenido=" + id);
		nuevoAnuncio = anunPers.create(id);
		//Vamos aï¿½adiendo datos al nuevo anuncio
		nuevoAnuncio.setUserId(userId);
		nuevoAnuncio.setUserName(user.getFirstName());
		nuevoAnuncio.setCreateDate(calHoy.getTime());
		nuevoAnuncio.setModifiedDate(calHoy.getTime());
		nuevoAnuncio.setClassNameId(classNameId);
		nuevoAnuncio.setClassPK(classPK);
		nuevoAnuncio.setCompanyId(journalArticle.getCompanyId());
		nuevoAnuncio.setTitle(title);
		nuevoAnuncio.setContent(anuncioEnviar.getContenido());
		//nuevoAnuncio.setUrl(url);
		nuevoAnuncio.setType("general");
		nuevoAnuncio.setDisplayDate(calHoy.getTime());
		//Fecha expiraciï¿½n

		//if (anuncioEnviar.getFechaExpiracion() != null)
		//{
			//nuevoAnuncio.setExpirationDate(anuncioEnviar.getFechaExpiracion());
		//}
		//else {
			//nuevoAnuncio.setExpirationDate(calExpire.getTime());
		//}

		nuevoAnuncio.setPriority(1);
		nuevoAnuncio.setAlert(false);

		//Creamos el anuncio
		anunPers.update(nuevoAnuncio);
		anunPers.clearCache();
	}
	
	
	/**
	 * Genera una notificación automática
	 * @param journalArticle
	 * @param anuncioEnviar
	 * @param role
	 * @param user
	 * @throws SystemException
	 * @throws PortalException
	 * @throws FieldValidationException
	 * @throws PortletException
	 */
	public void enviarNotificacion (JournalArticle journalArticle, VistaAnuncios anuncioEnviar, Long role, User user, ServiceContext serviceContext) throws SystemException, PortalException, FieldValidationException, PortletException {

		//Recogemos los datos necesarios para crear el anuncio
		//Usuario actual (el que ha publicado el artï¿½culo).
		//long userId = user.getUserId();

		//dia actual
		//Calendar calHoy = Calendar.getInstance();
		//dia de expiraciï¿½n
		Calendar calExpire = Calendar.getInstance();
		calExpire.add(Calendar.MONTH, 2);

		//ClassNameId del anuncio a crear
		//EJEMPLO DE ROL
		//long classNameId = role.getClassNameId();
		//long classPK = role.getClassPK();

		String title = anuncioEnviar.getTitulo();

		/*
		 * CREAMOS LA NOTIFICACION
		 */
		DDMStructure estructuraNotif = DDMStructureLocalServiceUtil.getDDMStructure(Constantes.ESTRUCTUREID_NOTIFICACION);
		
		// Primero validamos que los datos enviados 
		// son correctos.
		
		Map<String,Object> journalData = getDatosJournal(journalArticle, anuncioEnviar, role, user, estructuraNotif);
		
		
			
		String content = new ContentGeneratorImpl(estructuraNotif, journalData)
					.generateContent();	
		//logger.info("CONTENT generado: " + content);
			
		String ddmStructureKey = estructuraNotif.getStructureKey();			
		String ddmTemplateKey = StringPool.BLANK;		
			
		//ServiceContext serviceContext =
		//		ServiceContextFactory.getInstance(AssetCategory.class.getName(), request);		
			
		long groupId = estructuraNotif.getGroupId();
		long folderId = 0;
			
		if(!estructuraNotif.getTemplates().isEmpty()){
				ddmTemplateKey = estructuraNotif.getTemplates().get(0).getTemplateKey();
		}
			
		Map<Locale, String> titleMap = new HashMap<Locale, String>();
		titleMap.put(LocaleUtil.getSiteDefault(), generateTitle(journalData));
				
		Map<Locale, String> descriptionMap = new HashMap<Locale, String>();
			
		JournalArticle newJournalArticle = JournalArticleLocalServiceUtil.addArticle(journalArticle.getUserId(),
			groupId,
			folderId, 
			titleMap, 
			descriptionMap, 
			content, 
			ddmStructureKey, 
			ddmTemplateKey, 
			serviceContext);
				
	}
	
	
	
	private String generateTitle (Map<String,Object> journalData){
		String titulo = (String) journalData.get("Fecha_envio");
		return titulo;
	}
		
		
	protected JournalUtil journalUtil = new JournalUtil();

	private List<String> getListaCamposAnuncio(String content) {

		List<String> lista = new ArrayList<String>();
		String contenidoFormateado = "";
		String contenidoPendiente = content;
		int longContenidoOrigen = content.length();
		int actual = 0;

		while (actual < longContenidoOrigen && actual != -1) {
			//Obtenemos la variable a sustituir
			actual = contenidoPendiente.indexOf("${");

			if (actual != -1) {
				//Hay valores. Extraemos el texto hasta la variable encontrada
				String variable = "";
				int fin = contenidoPendiente.indexOf("}");

				if (fin != -1) {
					variable = contenidoPendiente.substring(actual + 2, fin);
					//Aï¿½adimos la variable a la lista
					lista.add(variable);
					contenidoPendiente = contenidoPendiente.substring(fin + 1);
				}
			}//fin if (actual != -1)
		}//fin while

		return lista;
	}
	
	
	/**
	 * Obtiene los datos de la estructura enviados por la request y los devuelve en un map
	 * en el que las claves son los nombres de campo. 
	 * 
	 * @param request
	 * @return
	 * @throws PortalException
	 * @throws SystemException
	 * @throws PortletException
	 */
	public static Map<String,Object> getDatosJournal (JournalArticle articleOrigen,  VistaAnuncios anuncioEnviar, Long role, User user,
			DDMStructure ddmStructure) throws PortalException, 
		SystemException, PortletException{
		
		//DATOS ORIGINALES
		Map<String, Object> datosOrigen = new HashMap<String, Object>();	
		
		//Construimos la NOTIFICACION a partir de los datos recibidos
		//Fecha
		datosOrigen.put("Fecha_envio", Calendar.getInstance().getTimeInMillis());
		//AssetEntry_Relacionado
		AssetEntry assetEntry = AssetEntryLocalServiceUtil.getEntry("com.liferay.portlet.journal.model.JournalArticle", articleOrigen.getResourcePrimKey());
		datosOrigen.put("AssetEntry_Relacionado", assetEntry.getEntryId());
		//Resuelto
		datosOrigen.put("Resuelto", false);
		//OriginadorPersona
		datosOrigen.put("OriginadorPersona", articleOrigen.getUserId());
		//OriginadorRol
		List<Role> roles = RoleLocalServiceUtil.getUserGroupRoles(articleOrigen.getUserId(), articleOrigen.getGroupId());
		for (Role rol : roles){
			if (rol.getType() == RoleConstants.TYPE_SITE){
				datosOrigen.put("OriginadorRol", String.valueOf(rol.getRoleId()));
			}
		}
		//DestinatarioPersona
		if (user != null){
			datosOrigen.put("DestinatarioPersona", user.getUserId());
		}
		else {
			datosOrigen.put("DestinatarioPersona", "");
		}
		//DestinatarioRol
		if (role != null){
			datosOrigen.put("DestinatarioRol", role);
		}
		else {
			datosOrigen.put("DestinatarioRol", "");
		}
		//Asunto
		datosOrigen.put("Asunto", anuncioEnviar.getTitulo());
		//Mensaje
		datosOrigen.put("Mensaje", anuncioEnviar.getContenido());
		//Area Temática
		String areaTematica =  JournalUtil.getRootParseValue("areaTematica", articleOrigen, LocaleUtil.getDefault().toString());
		if (areaTematica != null && !areaTematica.trim().isEmpty()){
			datosOrigen.put("idArea", JournalUtil.getRootParseValue("areaTematica", articleOrigen, LocaleUtil.getDefault().toString()));
		}
		
		//Realizamos el tratamiento y conversión de los datos para la generación del content
		Map<String, Object> datos = new HashMap<String, Object>();	
        List<String> rootFieldNames = new ArrayList<String>(ddmStructure.getRootFieldNames());
		
		rootFieldNames.remove("_fieldsDisplay");
		
		rellenaDatosFromMapa(datosOrigen, ddmStructure, rootFieldNames, datos, null);
		
		
		return datos;
	}
	
	
	/**
	 * Rellena los campos de la estructura que vienen en la request
	 * @param request
	 * @param ddmStructure
	 * @param childrenNames
	 * @param datos
	 * @throws PortalException
	 * @throws SystemException
	 * @throws PortletException
	 */
	private static void rellenaDatosFromMapa(Map<String, Object> datosOrigen, DDMStructure ddmStructure, List<String> childrenNames, 
			Map<String,Object> datos, String parentName ) throws PortalException, SystemException, PortletException{

		for(String fieldName : childrenNames){
			
			rellenaDatosFromMapa(datosOrigen, ddmStructure, ddmStructure.getChildrenFieldNames(fieldName), datos, fieldName);
			
			ValueGetter getter = ValueGetterFactory.getValueGetter(ddmStructure.getFieldType(fieldName));
			
			if(getter == null){
				continue;
			}
			
			JournalValueGetter journalGetter= new JournalValueGetter(getter);
			if(journalGetter.isParamInMap(datosOrigen, ddmStructure, fieldName, parentName)){
				datos.put(fieldName, 
						journalGetter.getValue(datosOrigen, ddmStructure, fieldName, parentName));	
			}			
		}
	}
	
	
	/**
	 * Obtiene los datos de la estructura enviados por la request y los devuelve en un map
	 * en el que las claves son los nombres de campo. 
	 * 
	 * @param request
	 * @return
	 * @throws PortalException
	 * @throws SystemException
	 * @throws PortletException
	 */
	public static Map<String,Object> getDatosRequest(PortletRequest request, DDMStructure ddmStructure) throws PortalException, 
		SystemException, PortletException{
		
		Map<String, Object> datos = new HashMap<String, Object>();		
		List<String> rootFieldNames = new ArrayList<String>(ddmStructure.getRootFieldNames());
		
		rootFieldNames.remove("_fieldsDisplay");
		
		
		
		return datos;
	}
	
	
	

	private static final Logger logger = LogManager.getLogger(AnuncioUtil.class.getName());

}