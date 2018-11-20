package es.grupoica.cyted.procesos;

import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portlet.journal.model.JournalArticle;

import es.grupoica.cyted.bbdd.model.ICAFlujoEstados;
import es.grupoica.cyted.bbdd.service.ICAAnuncioService;
import es.grupoica.cyted.bbdd.service.ICAFlujoEstadosService;
import es.grupoica.cyted.bbdd.service.ICAFlujoRolService;
import es.grupoica.cyted.util.AnuncioUtil;
import es.grupoica.cyted.util.Constantes;
import es.grupoica.cyted.util.JournalUtil;
import es.grupoica.cyted.util.RolUtil;

import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SolicitudLanding extends ProcesoBase {
	
	public SolicitudLanding(ServiceContext serviceContext) {
		super(serviceContext);
		// TODO Auto-generated constructor stub
	}

	public JournalArticle procesarSolicitud(Long structureId, String pagina, JournalArticle article) {

		/*
		SolicitudRT solicitudRT = new SolicitudRT();

		if (pagina.equals(Constantes.NUEVA_SOLICITUD_RT)) {
			article = solicitudRT.tratarNuevaSolicitud(article, structureId);
		}
		else if (pagina.equals(Constantes.EDICION_BORRADOR_SOLICITUD_RT)) {
			return article;
		}
		else if (pagina.equals(Constantes.REV_ADM_SOLICITUD_RT)) {
		}
		else if (pagina.equals(Constantes.REV_PERTINENCIA_SOLICITUD_RT)) {
		}
		*/

		return article;
	}

	/**
	 * Trata la edicionn del BORRADOR de una solicitud
	 * @param oldArticle
	 * @param newArticle
	 * @param structureId
	 * @return
	 * @throws Exception
	 */
	public JournalArticle tratarEdicionBorradorSolicitud(JournalArticle oldArticle, JournalArticle newArticle, Long structureId) throws Exception {
		return newArticle;
	}

	/**
	 * Tratamiento para la edicion de una solicitud existente.
	 * @param article
	 * @param oldArticle
	 * @param structureId
	 * @param pagina
	 * @return
	 * @throws Exception
	 */
	public JournalArticle tratarEdicionSolicitud(JournalArticle article, Long structureId, String pagina) throws Exception {

		//Construimos el objeto para la consulta
		ICAFlujoEstados estadoInicial = new ICAFlujoEstados();
		//Vemos si tiene ya un estado asignado
		String estado = JournalUtil.getRootParseValue("estado", article, LocaleUtil.getDefault().toString());
		
		if (estado != null) {
			estadoInicial.setIdEstadoOrigen(Integer.parseInt(estado));
			estadoInicial.setIdEstructura(structureId);
			estadoInicial.setUrlOrigen(pagina);
		}

		article = super.procesarEdicion(article, structureId, estadoInicial);

		return article;
	}

	/**
	 * Tratamiento para la creacion de una nueva solicitud
	 * @param article
	 * @param structureId
	 * @return
	 * @throws Exception
	 */
	public JournalArticle tratarNuevaSolicitud(JournalArticle article, Long structureId, String pagina) throws Exception {

		//C�digo del Journal.
		//Dependiendo del tipo de Journal, lo creamos o no.

		if (structureId.equals(Constantes.ESTRUCTUREID_SOLICITUD_LANDING)) {
			JournalUtil.setParseValue("Codigo", article, LocaleUtil.getDefault().toString(), getSiguienteCodigosolicitud(article));
		}

		//Construimos el objeto para la consulta
		ICAFlujoEstados estadoInicial = new ICAFlujoEstados();
		//Vemos si tiene ya un estado asignado
		String estado = JournalUtil.getRootParseValue("estado", article, LocaleUtil.getDefault().toString());

		if (estado != null && !estado.trim().isEmpty()) {
			//El estado inicial es Nuevo (0)
			estadoInicial.setIdEstadoOrigen(Integer.parseInt(estado));
			estadoInicial.setIdEstructura(structureId);
			estadoInicial.setUrlOrigen(pagina);
		}
		else {
			//El estado inicial es Nuevo (0)
			estadoInicial.setIdEstadoOrigen(0);
			estadoInicial.setIdEstructura(structureId);
			estadoInicial.setUrlOrigen(pagina);
		}

		article = super.procesarNuevo(article, structureId, estadoInicial);

		return article;
	}

	/**
	 * Trata la revision administrativa de la solicitud
	 * @param oldArticle
	 * @param newArticle
	 * @param structureId
	 * @return
	 * @throws Exception
	 */
	public JournalArticle tratarRevisionAdministrativa(JournalArticle oldArticle, JournalArticle newArticle, Long structureId) throws Exception {

		//Construimos el objeto para la consulta
		ICAFlujoEstados estadoInicial = new ICAFlujoEstados();
		//Vemos si tiene ya un estado asignado
		String estado = JournalUtil.getRootParseValue("estado", newArticle, LocaleUtil.getDefault().toString());

		if (estado != null) {
			estadoInicial.setIdEstadoOrigen(Integer.parseInt(estado));
			estadoInicial.setIdEstructura(structureId);
		}

		//Obtenemos el/los flujos correspondientes
		ICAFlujoEstadosService serviceFlujos = new ICAFlujoEstadosService();
		List<ICAFlujoEstados> flujos = serviceFlujos.obtenerFlujosEstadoActual(estadoInicial);

		//Obtenemos el flujo para el estado inicial NUEVO.
		ICAFlujoEstados estadoSiguiente = null;
		//Verificamos que existe un flujo para ambos estados

		if ( ! flujos.isEmpty()) {
			//Recorremos los flujos obtenidos buscando el nuevo, si lo hubiera
			//Obtenemos el valor de revisionAdministrativa
			String revisionAdministrativa = JournalUtil.getRootParseValue("revisionAdministrativa", newArticle, LocaleUtil.getDefault().toString());

			estadoSiguiente = flujos.get(0);
		}

		newArticle = super.procesarEdicion(newArticle, structureId, estadoSiguiente);

		return newArticle;
	}

	protected AnuncioUtil anuncioUtil = new AnuncioUtil();
	protected String contadorProyecto = "com.liferay.portlet.journal.service.LandingJournalArticleCounter";
	protected JournalUtil journalUtil = new JournalUtil();
	protected RolUtil rolUtil = new RolUtil();
	protected ICAAnuncioService serviceAnuncio = new ICAAnuncioService();
	protected ICAFlujoRolService serviceFlujoRol = new ICAFlujoRolService();
	protected ICAFlujoEstadosService serviceFlujos = new ICAFlujoEstadosService();

	/**
	 * Obtiene el siguiente valor disponible del contador autonum�rico.
	 * @return
	 */
	private String getSiguienteCodigosolicitud(JournalArticle article)
	{
		StringBuffer codigoProyecto = new StringBuffer("");

		//Obtenemos el valor siguiente del contador
		Long siguiente = journalUtil.getSiguiente(this.contadorProyecto);
		//Componemos el c�digo de la propuesta
		String area = JournalUtil.getRootParseValue("areaTematica", article, LocaleUtil.getDefault().toString());
		String ejercicio = JournalUtil.getRootParseValue("ejercicio", article, LocaleUtil.getDefault().toString());

		if (ejercicio == null || ejercicio.isEmpty()) {
			Calendar cal = Calendar.getInstance();
			Integer year = cal.get(Calendar.YEAR);

			if (year != null) {
				ejercicio = String.valueOf(year);
			}
		}

		codigoProyecto.append("P")
		.append(area)
		.append(ejercicio.substring(2))
		.append("LD")
		.append(journalUtil.rellenarCadenaIzq(siguiente.toString(), "0", 4));
		logger.info("Nuevo codigo = " + codigoProyecto.toString());

		//ACTUALIZAMOS EL CONTADOR
		journalUtil.incrementaContador(this.contadorProyecto);
		//comprobamos cual es el siguiente valor a incluir
		logger.info("SIGUIENTE: " + journalUtil.getSiguiente(this.contadorProyecto));

		return codigoProyecto.toString();
	}

	private static final Logger logger = LogManager.getLogger(SolicitudLanding.class.getName());

}
