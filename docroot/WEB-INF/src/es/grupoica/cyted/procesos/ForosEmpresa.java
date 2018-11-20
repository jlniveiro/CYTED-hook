package es.grupoica.cyted.procesos;

import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portlet.journal.model.JournalArticle;

import es.grupoica.cyted.bbdd.model.ICAFlujoEstados;
import es.grupoica.cyted.util.Constantes;
import es.grupoica.cyted.util.JournalUtil;

public class ForosEmpresa extends ProcesoBase {
	
	private static final Logger logger = LogManager.getLogger(ForosEmpresa.class.getName());
	protected String contadorProyecto = "com.liferay.portlet.journal.service.ForosEmpresaJournalArticleCounter";

	public ForosEmpresa(ServiceContext serviceContext) {
		super(serviceContext);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Tratamiento para la creaci�n de una nueva solicitud
	 * @param article
	 * @param structureId
	 * @return
	 * @throws Exception
	 */
	public JournalArticle tratarNuevoForo(JournalArticle article, Long structureId, String pagina) throws Exception {

		//C�digo del Journal.
		//Dependiendo del tipo de Journal, lo creamos o no.

		if (structureId.equals(Constantes.ESTRUCTUREID_FOROS_EMPRESA)) {
			String siguienteCodigo = getSiguienteCodigosolicitud(article);
			JournalUtil.setParseValue("Codigo", article, LocaleUtil.getDefault().toString(), siguienteCodigo);
			JournalUtil.setParseValue("referenciaSolicitud", article, LocaleUtil.getDefault().toString(), siguienteCodigo);
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
	 * Tratamiento para la edici�n de una solicitud existente.
	 * @param article
	 * @param oldArticle
	 * @param structureId
	 * @param pagina
	 * @return
	 * @throws Exception
	 */
	public JournalArticle tratarEdicionForo(JournalArticle article, Long structureId, String pagina) throws Exception {

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
	 * Obtiene el siguiente valor disponible del contador autonum�rico.
	 * @return
	 */
	private String getSiguienteCodigosolicitud(JournalArticle article)
	{
		StringBuffer codigoSolicitud = new StringBuffer("");

		//Obtenemos el valor siguiente del contador
		Long siguiente = journalUtil.getSiguiente(this.contadorProyecto);
		if (siguiente.equals(new Long(0))) {
			siguiente = journalUtil.getSiguiente(this.contadorProyecto);
		}
		//Componemos el codigo de la propuesta
		String convocatoria = JournalUtil.getRootParseValue("convocatoria", article, LocaleUtil.getDefault().toString());

		if (convocatoria == null || convocatoria.isEmpty()) {
			Calendar cal = Calendar.getInstance();
			Integer year = cal.get(Calendar.YEAR);

			if (year != null) {
				convocatoria = String.valueOf(year);
			}
		}

		codigoSolicitud.append("CYTED-")
		.append(convocatoria.substring(2)).append("-")
		.append(journalUtil.rellenarCadenaIzq(siguiente.toString(), "0", 3));
		logger.info("Nuevo codigo = " + codigoSolicitud.toString());

		//ACTUALIZAMOS EL CONTADOR
		journalUtil.incrementaContador(this.contadorProyecto);
		//comprobamos cual es el siguiente valor a incluir
		logger.info("SIGUIENTE: " + journalUtil.getSiguiente(this.contadorProyecto));

		return codigoSolicitud.toString();
	}

}
