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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class ProyectoIberoeka extends ProcesoBase {
	
	

	public ProyectoIberoeka(ServiceContext serviceContext) {
		super(serviceContext);
		// TODO Auto-generated constructor stub
	}

	public JournalArticle tratarEdicionProyecto(JournalArticle oldArticle, JournalArticle newArticle, Long structureId) throws Exception {
		//newArticle = super.procesarEdicion(oldArticle, newArticle, structureId);

		return newArticle;
	}

	public JournalArticle tratarNuevoProyecto(JournalArticle article, Long structureId, String pagina) throws Exception {

		//Construimos el objeto para la consulta
		ICAFlujoEstados estadoInicial = new ICAFlujoEstados();
		
		if (structureId.equals(Constantes.ESTRUCTUREID_PROYECTO_IBEROEKA)) {
			JournalUtil.setParseValue("CodigoProyectoIberoeka", article, LocaleUtil.getDefault().toString(), getSiguienteCodigoProyecto(article));
		}
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

	protected AnuncioUtil anuncioUtil = new AnuncioUtil();
	protected String contadorProyecto = "com.liferay.portlet.journal.service.ProyectoJournalArticleCounter";
	protected JournalUtil journalUtil = new JournalUtil();
	protected RolUtil rolUtil = new RolUtil();
	protected ICAAnuncioService serviceAnuncio = new ICAAnuncioService();
	protected ICAFlujoRolService serviceFlujoRol = new ICAFlujoRolService();
	protected ICAFlujoEstadosService serviceFlujos = new ICAFlujoEstadosService();

	/**
	 * Obtiene el siguiente valor disponible del contador autonumï¿½rico.
	 * @return
	 */
	private String getSiguienteCodigoProyecto(JournalArticle article)
	{
		StringBuffer codigoProyecto = new StringBuffer("");

		//Obtenemos el valor siguiente del contador
		Long siguiente = journalUtil.getSiguiente(this.contadorProyecto);
		
		String ejercicio = JournalUtil.getRootParseValue("ejercicio", article, LocaleUtil.getDefault().toString());

		if (ejercicio == null || ejercicio.isEmpty()) {
			Calendar cal = Calendar.getInstance();
			Integer year = cal.get(Calendar.YEAR);

			if (year != null) {
				ejercicio = String.valueOf(year);
			}
		}
		
		codigoProyecto.append("IBK-")
		.append(ejercicio.substring(2))
		.append("-")
		.append(journalUtil.rellenarCadenaIzq(siguiente.toString(), "0", 4));

		//codigoProyecto = "IBK-2018-" + siguiente.toString();
		logger.info("Nuevo código Proyecto Iberoeka = " + codigoProyecto.toString());

		//ACTUALIZAMOS EL CONTADOR
		journalUtil.incrementaContador(this.contadorProyecto);
		//comprobamos cual es el siguiente valor a incluir
		//logger.info("SIGUIENTE: " + journalUtil.getSiguiente(this.contadorProyecto));

		return codigoProyecto.toString();
	}

	private static final Logger logger = LogManager.getLogger(ProyectoIberoeka.class.getName());

}