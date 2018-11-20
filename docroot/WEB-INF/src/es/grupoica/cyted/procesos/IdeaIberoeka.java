package es.grupoica.cyted.procesos;

import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portlet.journal.model.JournalArticle;

import es.grupoica.cyted.bbdd.model.ICAFlujoEstados;
import es.grupoica.cyted.util.Constantes;
import es.grupoica.cyted.util.JournalUtil;

import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class IdeaIberoeka extends ProcesoBase {
	
	

	public IdeaIberoeka(ServiceContext serviceContext) {
		super(serviceContext);
		// TODO Auto-generated constructor stub
	}

	public JournalArticle tratarEdicionIdea(JournalArticle oldArticle, JournalArticle newArticle, Long structureId) throws Exception {
		//newArticle = super.procesarEdicion(oldArticle, newArticle, structureId);

		return newArticle;
	}

	public JournalArticle tratarNuevaIdea(JournalArticle article, Long structureId, String pagina) throws Exception {
		
		//Construimos el objeto para la consulta
		ICAFlujoEstados estadoInicial = new ICAFlujoEstados();
		
		if (structureId.equals(Constantes.ESTRUCTUREID_IDEA_IBEROEKA)) {
			JournalUtil.setParseValue("codigoIdeaProyecto", article, LocaleUtil.getDefault().toString(), getSiguienteCodigoIdea(article));
		}
		
		//Vemos si tiene ya un estado asignado
		String estado = JournalUtil.getRootParseValue("estado", article, LocaleUtil.getDefault().toString());

		if (estado != null && !estado.trim().isEmpty()) {
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

	protected String contadorIdea = "com.liferay.portlet.journal.service.IdeaJournalArticleCounter";

	/**
	 * Obtiene el siguiente valor disponible del contador autonumï¿½rico.
	 * @return
	 */
	private String getSiguienteCodigoIdea(JournalArticle article)
	{
		StringBuffer codigoIdea = new StringBuffer("");

		//Obtenemos el valor siguiente del contador
		Long siguiente = journalUtil.getSiguiente(this.contadorIdea);
		String ejercicio = JournalUtil.getRootParseValue("Ejercicio", article, LocaleUtil.getDefault().toString());

		if (ejercicio == null || ejercicio.isEmpty()) {
			Calendar cal = Calendar.getInstance();
			Integer year = cal.get(Calendar.YEAR);

			if (year != null) {
				ejercicio = String.valueOf(year);
			}
		}
		
		codigoIdea.append("IP-")
		    .append(journalUtil.rellenarCadenaIzq(siguiente.toString(), "0", 4))
		    .append("-")
			.append(ejercicio.substring(2))
			
			;
		
		logger.info("Nuevo código de Idea Iberoeka= " + codigoIdea.toString());

		//ACTUALIZAMOS EL CONTADOR
		journalUtil.incrementaContador(this.contadorIdea);
		//comprobamos cual es el siguiente valor a incluir
		//logger.info("SIGUIENTE: " + journalUtil.getSiguiente(this.contadorIdea));

		return codigoIdea.toString();
	}

	private static final Logger logger = LogManager.getLogger(ProyectoIberoeka.class.getName());

}