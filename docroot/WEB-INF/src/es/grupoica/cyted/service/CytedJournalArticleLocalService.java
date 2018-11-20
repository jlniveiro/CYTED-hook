package es.grupoica.cyted.service;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.security.auth.PrincipalThreadLocal;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.security.permission.PermissionThreadLocal;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portlet.announcements.service.persistence.AnnouncementsEntryPersistence;
import com.liferay.portlet.asset.model.AssetCategory;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.service.JournalArticleLocalService;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceWrapper;
import com.liferay.portlet.journal.service.persistence.JournalArticlePersistence;

import es.grupoica.cyted.bbdd.service.ICAFlujoEstadosService;
import es.grupoica.cyted.procesos.Accion;
import es.grupoica.cyted.procesos.ForosEmpresa;
import es.grupoica.cyted.procesos.IdeaIberoeka;
import es.grupoica.cyted.procesos.Notificacion;
import es.grupoica.cyted.procesos.ProyectoIberoeka;
import es.grupoica.cyted.procesos.SolicitudLanding;
import es.grupoica.cyted.procesos.SolicitudPE;
import es.grupoica.cyted.procesos.SolicitudRT;
import es.grupoica.cyted.procesos.UsuarioCyted;
import es.grupoica.cyted.util.Constantes;
import es.grupoica.cyted.util.EstructuraUtil;
import es.grupoica.cyted.util.JournalUtil;

import java.io.File;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class CytedJournalArticleLocalService extends JournalArticleLocalServiceWrapper {

	protected com.liferay.counter.service.CounterLocalService counterLocalService;
	protected com.liferay.portal.service.UserLocalService userLocalService;
	protected JournalArticlePersistence journalArticlePersistence;
	protected AnnouncementsEntryPersistence announcementsEntryPersistence;
	protected EstructuraUtil estructUtil = new EstructuraUtil();
	protected JournalUtil journalUtil = new JournalUtil();
	protected ICAFlujoEstadosService serviceFlujos = new ICAFlujoEstadosService();

	private String contadorArticulo = "com.liferay.portlet.journal.service.JournalArticleCounter";

	private static final Logger logger = LogManager.getLogger(CytedJournalArticleLocalService.class.getName());

	public CytedJournalArticleLocalService(JournalArticleLocalService journalArticleLocalService) {
		super(journalArticleLocalService);

		// TODO Auto-generated constructor stub

	}

	@Override
	public JournalArticle addArticle(long userId, long groupId, long folderId, long classNameId, long classPK,
			String articleId, boolean autoArticleId, double version, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, String content, String type, String ddmStructureKey,
			String ddmTemplateKey, String layoutUuid, int displayDateMonth, int displayDateDay, int displayDateYear,
			int displayDateHour, int displayDateMinute, int expirationDateMonth, int expirationDateDay,
			int expirationDateYear, int expirationDateHour, int expirationDateMinute, boolean neverExpire,
			int reviewDateMonth, int reviewDateDay, int reviewDateYear, int reviewDateHour, int reviewDateMinute,
			boolean neverReview, boolean indexable, boolean smallImage, String smallImageURL, File smallImageFile,
			Map<String, byte[]> images, String articleURL, ServiceContext serviceContext)
			throws PortalException, SystemException {

		// TODO Auto-generated method stub

		return super.addArticle(userId, groupId, folderId, classNameId, classPK, articleId, autoArticleId, version, titleMap,
				descriptionMap, content, type, ddmStructureKey, ddmTemplateKey, layoutUuid, displayDateMonth, displayDateDay,
				displayDateYear, displayDateHour, displayDateMinute, expirationDateMonth, expirationDateDay, expirationDateYear,
				expirationDateHour, expirationDateMinute, neverExpire, reviewDateMonth, reviewDateDay, reviewDateYear,
				reviewDateHour, reviewDateMinute, neverReview, indexable, smallImage, smallImageURL, smallImageFile, images,
				articleURL, serviceContext);
	}

	@Override
	public JournalArticle addArticle(long userId, long groupId, long folderId, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, String content, String ddmStructureKey, String ddmTemplateKey,
			ServiceContext serviceContext) throws PortalException, SystemException {

		// TODO Auto-generated method stub

		logger.info("INFO: Iniciando la creación de un nuevo Journal");
		long startTime = System.currentTimeMillis();
		JournalArticle journalArticle = super.addArticle(userId, groupId, folderId, titleMap, descriptionMap, content, ddmStructureKey, ddmTemplateKey,
				serviceContext);
		logger.info("INFO: Proceso padre instanciado. Comenzamos con el tratamiento personalizado del Journal");

		try
		{
			//Establecemos permisos
			PrincipalThreadLocal.setName(userId);
			PermissionChecker permissionChecker = PermissionCheckerFactoryUtil.create(UserLocalServiceUtil.getUser(userId));
			PermissionThreadLocal.setPermissionChecker(permissionChecker); 
			//Obtenemos la estructura a la que pertenece el mismo
			logger.info("INFO: Obtenemos la estructura del contenido");
			Long structureId = estructUtil.getIdStructureJournal(ddmStructureKey, journalArticle);
			
			String url = serviceContext.getLayoutURL();
			//Obtenemos la pï¿½gina desde donde nos llaman para determinar la acciï¿½n a realizar
			logger.info("INFO: Obtenemos la URL de llamada");
			String pagina = journalUtil.getPaginaLlamada(url);
			
			//En funciï¿½n de el structureId recibido realizamos el tratamiento para el nuevo Journal a crear
			logger.info("INFO: Iniciamos el tratamiento del nuevo Journal");
			journalArticle = this.tratarNuevoJournal(structureId, pagina, journalArticle, serviceContext);

		}
		catch (Exception ex) {
			ex.printStackTrace();
			logger.error("ERROR: " + ex.fillInStackTrace().toString());
			return null;
		}
		finally
		{
			long endTime = System.currentTimeMillis() - startTime;
			logger.info("INFO: Tratamiento de nuevo Journal finalizado en " + endTime + " milisegundos");
		}

		return journalArticle;

	}

	@Override
	public JournalArticle updateArticle(long userId, long groupId, long folderId, String articleId, double version,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap, String content, String layoutUuid,
			ServiceContext serviceContext) throws PortalException, SystemException {

		// TODO Auto-generated method stub

		JournalArticle journalArticle = super.updateArticle(userId, groupId, folderId, articleId, version, titleMap, descriptionMap, content, layoutUuid,
				serviceContext);

		return journalArticle;
	}

	@Override
	public JournalArticle updateArticle(long userId, long groupId, long folderId, String articleId, double version,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap, String content, String type,
			String ddmStructureKey, String ddmTemplateKey, String layoutUuid, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear, int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, int reviewDateMonth, int reviewDateDay, int reviewDateYear, int reviewDateHour,
			int reviewDateMinute, boolean neverReview, boolean indexable, boolean smallImage, String smallImageURL,
			File smallImageFile, Map<String, byte[]> images, String articleURL, ServiceContext serviceContext)
			throws PortalException, SystemException {

		// TODO Auto-generated method stub

		return super.updateArticle(userId, groupId, folderId, articleId, version, titleMap, descriptionMap, content, type,
				ddmStructureKey, ddmTemplateKey, layoutUuid, displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
				displayDateMinute, expirationDateMonth, expirationDateDay, expirationDateYear, expirationDateHour,
				expirationDateMinute, neverExpire, reviewDateMonth, reviewDateDay, reviewDateYear, reviewDateHour,
				reviewDateMinute, neverReview, indexable, smallImage, smallImageURL, smallImageFile, images, articleURL,
				serviceContext);
	}

	@Override
	public JournalArticle updateArticle(long userId, long groupId, long folderId, String articleId, double version,
			String content, ServiceContext serviceContext) throws PortalException, SystemException {

		//JournalArticle articuloAnterior = this.getLatestArticle(groupId, articleId, 0);
		
		//Artïculo a modificar
		logger.info("INFO: Iniciando la modificación de un Journal existente");
		long startTime = System.currentTimeMillis();
		JournalArticle journalArticle = super.updateArticle(userId, groupId, folderId, articleId, version, content, serviceContext);
		logger.info("INFO: Proceso padre instanciado. Comenzamos con el tratamiento personalizado del Journal");
		
		try {

			//Establecemos permisos
			PrincipalThreadLocal.setName(userId);
			PermissionChecker permissionChecker = PermissionCheckerFactoryUtil.create(UserLocalServiceUtil.getUser(userId));
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
			HttpServletRequest req = serviceContext.getRequest();
			//Obtenemos la estructura a la que pertenece el mismo
			//El StructureId del Journal es el valor de StructureKey de la estructura
			logger.info("INFO: Obtenemos la estructura del contenido");
			Long structureId = estructUtil.getIdStructureJournal(journalArticle.getStructureId(), journalArticle);
			String url = serviceContext.getLayoutURL();
			//Obtenemos la pagina desde donde nos llaman para determinar la acciï¿½n a realizar
			logger.info("INFO: Obtenemos la URL de llamada");
			String pagina = journalUtil.getPaginaLlamada(url);

			//En funciï¿½n de el structureId recibido realizamos el tratamiento para el nuevo Journal a crear
			logger.info("INFO: Iniciamos la modificación del Journal");
			journalArticle = this.tratarEdicionJournal(structureId, pagina, journalArticle, serviceContext);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("ERROR: " + e.fillInStackTrace().toString());
		}
		finally
		{
			long endTime = System.currentTimeMillis() - startTime;
			logger.info("INFO: Modificación del Journal finalizado en " + endTime + " milisegundos");
		}

		return journalArticle;

	}

	@Override
	public JournalArticle updateArticleTranslation(long groupId, String articleId, double version, Locale locale,
			String title, String description, String content, Map<String, byte[]> images, ServiceContext serviceContext)
			throws PortalException, SystemException {

		// TODO Auto-generated method stub

		JournalArticle journalArticle = super.updateArticleTranslation(groupId, articleId, version, locale, title, description, content, images,
				serviceContext);
		logger.info("updateArticleTranslation------------------------------");
		logger.info("IdArticle=" + articleId);

		return journalArticle;
	}

	@Override
	public JournalArticle getArticle(long groupId, String articleId, double version)
			throws PortalException, SystemException {

		// TODO Auto-generated method stub

		JournalArticle article = super.getArticle(groupId, articleId, version);
		return article;
	}

	@Override
	public JournalArticle getArticle(long groupId, String className, long classPK)
			throws PortalException, SystemException {

		// TODO Auto-generated method stub

		JournalArticle article = null;

		article = super.getArticle(groupId, className, classPK);
		//Actualizamos los valores que deseemos al crear el contenido
		//Actualizamos en la Estructura//Entra por este ADD Article.
		//Obtenemos el valor siguiente del contador
		Long siguiente = journalUtil.getSiguiente(contadorArticulo);

		String nuevoCodigo = "IBK-2018-" + siguiente.toString();
		logger.info("Nuevo codigo = " + nuevoCodigo);

		//CounterLocalServiceUtil.increment("com.liferay.portlet.journal.service.JournalArticleCounter", 1);
		//CounterLocalServiceUtil.updateCounter(contador);
		JournalUtil.setParseValue("Codigo", article, LocaleUtil.getDefault().toString(), "IBK-2018-" + nuevoCodigo);
		//ACTUALIZAMOS EL CONTADOR
		journalUtil.incrementaContador(contadorArticulo);
		//comprobamos cual es el siguiente valor a incluir
		System.out.println("SIGUIENTE: " + journalUtil.getSiguiente(contadorArticulo));

		return article;
	}

	@Override
	public JournalArticle getArticle(long groupId, String articleId) throws PortalException, SystemException {

		// TODO Auto-generated method stub

		JournalArticle article = super.getArticle(groupId, articleId);
		return article;
	}

	@Override
	public JournalArticle getArticleByUrlTitle(long groupId, String urlTitle) throws PortalException, SystemException {

		// TODO Auto-generated method stub

		JournalArticle article = super.getArticleByUrlTitle(groupId, urlTitle);
		return article;
	}

	@Override
	public JournalArticle getArticle(long id) throws PortalException, SystemException {

		// TODO Auto-generated method stub

		JournalArticle article = super.getArticle(id);
		return article;
	}

	@Override
	public JournalArticle getDisplayArticleByUrlTitle(long groupId, String urlTitle)
			throws PortalException, SystemException {

		// TODO Auto-generated method stub

		return super.getDisplayArticleByUrlTitle(groupId, urlTitle);
	}

	/**
	 * Returns the user local service.
	 *
	 * @return the user local service
	 */
	public com.liferay.portal.service.UserLocalService getUserLocalService() {
		return userLocalService;
	}

	/**
	 * Sets the user local service.
	 *
	 * @param userLocalService the user local service
	 */
	public void setUserLocalService(
		com.liferay.portal.service.UserLocalService userLocalService) {
		this.userLocalService = userLocalService;
	}

	/**
	 * Returns the journal article persistence.
	 *
	 * @return the journal article persistence
	 */
	public JournalArticlePersistence getJournalArticlePersistence() {
		return journalArticlePersistence;
	}

	/**
	 * Sets the AnnouncementsEntry persistence.
	 *
	 * @param AnnouncementsEntryPersistence the announcements entry persistence
	 */
	public void setAnnouncementsEntryPersistence(
			AnnouncementsEntryPersistence announcementsEntryPersistence) {
		this.announcementsEntryPersistence = announcementsEntryPersistence;
	}

	/**
	 * Returns the AnnouncementsEntry persistence.
	 *
	 * @return the AnnouncementsEntry persistence
	 */
	public AnnouncementsEntryPersistence getAnnouncementsEntryPersistence() {
		return announcementsEntryPersistence;
	}

	/**
	 * Sets the journal article persistence.
	 *
	 * @param journalArticlePersistence the journal article persistence
	 */
	public void setJournalArticlePersistence(
		JournalArticlePersistence journalArticlePersistence) {
		this.journalArticlePersistence = journalArticlePersistence;
	}

	

	/**
	 * Realiza la gestiï¿½n de un nuevo journal
	 * @param structureId
	 * @param pagina
	 * @param article
	 * @return
	 * @throws Exception
	 */
	private JournalArticle tratarNuevoJournal(Long structureId, String pagina, JournalArticle article, ServiceContext serviceContext) throws Exception {

		//Segï¿½n el structureId, invocamos a la clase que realizarï¿½ la gestiï¿½n del nuevo contenido
		/*
		 *  Solicitud Red Temï¿½tica --> 21363
		 *  Solicitud Proyecto Estratï¿½gico --> 26832
		 *  Solicitud Landing Internacional --> 26910
		 *  Proyecto Iberoeka --> 23423
		 *  Idea Proyecto Iberoeka --> 23401
		 *  Acciï¿½n --> 24866
		 */

		if (structureId.equals(Constantes.ESTRUCTUREID_PROYECTO_IBEROEKA)) {
			ProyectoIberoeka proyectoIbk = new ProyectoIberoeka(serviceContext);
			article = proyectoIbk.tratarNuevoProyecto(article, structureId, pagina);
		}
		else if (structureId.equals(Constantes.ESTRUCTUREID_IDEA_IBEROEKA)) {
			IdeaIberoeka ideaIbk = new IdeaIberoeka(serviceContext);
			article = ideaIbk.tratarNuevaIdea(article, structureId, pagina);
		}
		else if (structureId.equals(Constantes.ESTRUCTUREID_USUARIO)) {
			UsuarioCyted usuario = new UsuarioCyted();
			article = usuario.crearUsuario(article, serviceContext);
		}
		else if (structureId.equals(Constantes.ESTRUCTUREID_SOLICITUD_RT))
		{
			SolicitudRT solicitudRT = new SolicitudRT(serviceContext);
			article = solicitudRT.tratarNuevaSolicitud(article, structureId, pagina);
		}
		else if (structureId.equals(Constantes.ESTRUCTUREID_SOLICITUD_PE))
		{
			SolicitudPE solicitudPE = new SolicitudPE(serviceContext);
			article = solicitudPE.tratarNuevaSolicitud(article, structureId, pagina);
		}
		else if (structureId.equals(Constantes.ESTRUCTUREID_SOLICITUD_LANDING))
		{
			SolicitudLanding solicitudLanding = new SolicitudLanding(serviceContext);
			article = solicitudLanding.tratarNuevaSolicitud(article, structureId, pagina);
		}
		else if (structureId.equals(Constantes.ESTRUCTUREID_FOROS_EMPRESA)){
			ForosEmpresa foros = new ForosEmpresa(serviceContext);
			article = foros.tratarNuevoForo(article, structureId, pagina);
		}
		else if (structureId.equals(Constantes.ESTRUCTUREID_NOTIFICACION)){
			Notificacion notificacion = new Notificacion(serviceContext);
			article = notificacion.procesarNuevaNotificacion(article);
		}

		return article;
	}

	/**
	 * Realiza la gestiï¿½n de un nuevo journal
	 * @param structureId
	 * @param pagina
	 * @param article
	 * @return
	 * @throws Exception
	 */
	private JournalArticle tratarEdicionJournal(Long structureId, String pagina, JournalArticle article, ServiceContext serviceContext) throws Exception {

		//Segï¿½n el structureId, invocamos a la clase que realizarï¿½ la gestiï¿½n del nuevo contenido
		/*
		 *  Solicitud Red Temï¿½tica --> 21363
		 *  Solicitud Proyecto Estratï¿½gico --> 26832
		 *  Solicitud Landing Internacional --> 26910
		 *  Proyecto Iberoeka --> 23423
		 *  Idea Proyecto Iberoeka --> 23401
		 *  Acciï¿½n --> 24866
		 *  Foros de Empresa --> 38497
		 */

		if (structureId.equals(Constantes.ESTRUCTUREID_PROYECTO_IBEROEKA)) {
			ProyectoIberoeka proyectoIbk = new ProyectoIberoeka(serviceContext);
			//article = proyectoIbk.tratarNuevoProyecto(article, structureId);
		}
		else if (structureId.equals(Constantes.ESTRUCTUREID_IDEA_IBEROEKA)) {
			IdeaIberoeka ideaIbk = new IdeaIberoeka(serviceContext);
			//article = ideaIbk.tratarNuevaIdea(article, structureId);
		}
		else if (structureId.equals(Constantes.ESTRUCTUREID_USUARIO)) {
			UsuarioCyted usuario = new UsuarioCyted();
			article = usuario.modificarUsuario(article, serviceContext);
		}
		else if (structureId.equals(Constantes.ESTRUCTUREID_SOLICITUD_RT))
		{
			SolicitudRT solicitudRT = new SolicitudRT(serviceContext);
			article = solicitudRT.tratarEdicionSolicitud(article, structureId, pagina);
		}
		else if (structureId.equals(Constantes.ESTRUCTUREID_SOLICITUD_PE))
		{
			SolicitudPE solicitudPE = new SolicitudPE(serviceContext);
			article = solicitudPE.tratarEdicionSolicitud(article, structureId, pagina);
		}
		else if (structureId.equals(Constantes.ESTRUCTUREID_SOLICITUD_LANDING))
		{
			SolicitudLanding solicitudLanding = new SolicitudLanding(serviceContext);
			article = solicitudLanding.tratarEdicionSolicitud(article, structureId, pagina);
		}
		else if (structureId.equals(Constantes.ESTRUCTUREID_ACCION))
		{
			Accion accion = new Accion(serviceContext);
			article = accion.tratarEdicionAccion(article, structureId, pagina);
		}
		else if (structureId.equals(Constantes.ESTRUCTUREID_FOROS_EMPRESA)){
			ForosEmpresa foro = new ForosEmpresa(serviceContext);
			article = foro.tratarEdicionForo(article,  structureId, pagina);
		}

		return article;
	}

	/**
	 * Obtiene la clase que va a gestionar el proceso recibido
	 * @param nombre
	 * @return
	 * /
	public ProcesoBase getProcesoAEjecutar(Long structureId) {

		ProcesoBase proceso = null;
		try {
			Class clase = Class.forName(nombre);
			proceso = (ProcesoBase)clase.newInstance();
			return proceso;

		} catch (NullPointerException e) {
			TxUtil.log(logger, Level.SEVERE,"No se ha especificado la clase encargada de procesar el proceso. Proceso: " + nombre);
		} catch (ClassNotFoundException e) {
			TxUtil.log(logger, Level.SEVERE,"No se encuentra la clase encargada de procesar el proceso. Proceso: " + nombre);
		} catch (InstantiationException e) {
			TxUtil.log(logger, Level.SEVERE,"Error al instanciar la clase encargada de procesar el proceso. Proceso: " + nombre);
		} catch (IllegalAccessException e) {
			TxUtil.log(logger, Level.SEVERE,"Acceso ilegal al obtener el procesador del proceso");
		}

		return proceso;
	}

	*/

}