package es.grupoica.cyted.procesos;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portlet.asset.model.AssetCategory;
import com.liferay.portlet.asset.model.AssetVocabulary;
import com.liferay.portlet.asset.service.AssetCategoryLocalServiceUtil;
import com.liferay.portlet.asset.service.AssetVocabularyLocalServiceUtil;
import com.liferay.portlet.journal.model.JournalArticle;

import es.grupoica.cyted.bbdd.model.ICAFlujoEstados;
import es.grupoica.cyted.bbdd.service.ICAAnuncioService;
import es.grupoica.cyted.bbdd.service.ICAFlujoEstadosService;
import es.grupoica.cyted.bbdd.service.ICAFlujoRolService;
import es.grupoica.cyted.util.AnuncioUtil;
import es.grupoica.cyted.util.Constantes;
import es.grupoica.cyted.util.JournalUtil;
import es.grupoica.cyted.util.RolUtil;
import es.grupoica.cyted.util.UsuarioUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class SolicitudRT extends ProcesoBase {
	
	protected AnuncioUtil anuncioUtil = new AnuncioUtil();
	protected String contadorProyecto = "com.liferay.portlet.journal.service.RedTematicaJournalArticleCounter";
	protected JournalUtil journalUtil = new JournalUtil();
	protected RolUtil rolUtil = new RolUtil();
	protected ICAAnuncioService serviceAnuncio = new ICAAnuncioService();
	protected ICAFlujoRolService serviceFlujoRol = new ICAFlujoRolService();
	protected ICAFlujoEstadosService serviceFlujos = new ICAFlujoEstadosService();
	private static final Logger logger = LogManager.getLogger(SolicitudRT.class.getName());
	
	
	
	
	
	public SolicitudRT(ServiceContext serviceContext) {
		super(serviceContext);
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
	 * Trata la ediciï¿½n del BORRADOR de una solicitud
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
	 * Tratamiento para la ediciï¿½n de una solicitud existente.
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
	 * Tratamiento para la creaciï¿½n de una nueva solicitud
	 * @param article
	 * @param structureId
	 * @return
	 * @throws Exception
	 */
	public JournalArticle tratarNuevaSolicitud(JournalArticle article, Long structureId, String pagina) throws Exception {

		//Cï¿½digo del Journal.
		//Dependiendo del tipo de Journal, lo creamos o no.

		if (structureId.equals(Constantes.ESTRUCTUREID_SOLICITUD_RT)) {
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
	 * Trata la revisiï¿½n administrativa de la solicitud
	 * @param oldArticle
	 * @param newArticle
	 * @param structureId
	 * @return
	 * @throws Exception
	 */
	public JournalArticle registrarPuntuacionONCYT(JournalArticle article) throws Exception {
		
		//Obtenemos la puntuación registrada por el usuario ONCYT
		String puntuacionONCYT = JournalUtil.getRootParseValue("puntuacionONCYT", article, LocaleUtil.getDefault().toString());
		//Obtenemos los datos del usuario autenticado
		UsuarioUtil userUtil = new UsuarioUtil();
		//VwUsuariosCyted usuarioCyted = userUtil.getDatosUsuarioAutenticado(this.getServiceContext());
		
		
		//En función del pais del usuario ONCYT, registramos su puntuación.
		String paisOncyt = userUtil.getPaisUsuarioAutenticado(this.getServiceContext());
		
		//Obtenemos el pais a partir de su vocabulario
		AssetVocabulary vocabulary = AssetVocabularyLocalServiceUtil.getGroupVocabulary(article.getGroupId(), "Paises");
		List<AssetCategory> paises =  AssetCategoryLocalServiceUtil.getVocabularyCategories(vocabulary.getVocabularyId(), 
						QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
				
		//Categoria De PAISES actual
		AssetCategory categActual = null;
		for (AssetCategory categoria : paises){
			Long paisActual = categoria.getCategoryId();
			if (paisActual.compareTo(new Long(paisOncyt)) == 0){
				//Es el pais
				//A partir de su nombre, adjudicamos la puntuación
				String campo = dameCampoONCYT(categoria.getName());
				//Actualizamos el valor
				JournalUtil.setParseValue(campo, article, LocaleUtil.getDefault().toString(), puntuacionONCYT);
				article = recalcular(article, puntuacionONCYT);
				break;
			}
		}
		
		
		return article;
	}
	
	
	private JournalArticle recalcular (JournalArticle article, String puntuacionONCYT){
		
		Integer total = 0;
		
		if (puntuacionONCYT.equals("1")){
			String totales1 = JournalUtil.getRootParseValue("Totales1", article, LocaleUtil.getDefault().toString());
			if (totales1 == null || totales1.isEmpty()){
				total = 1;
				JournalUtil.setParseValue("Totales1", article, LocaleUtil.getDefault().toString(), total.toString());
			}
			else {
				total = Integer.parseInt(totales1) + 1;
				JournalUtil.setParseValue("Totales1", article, LocaleUtil.getDefault().toString(), total.toString());
			}
		}
		else if (puntuacionONCYT.equals("2")){
			String totales2 = JournalUtil.getRootParseValue("Totales2", article, LocaleUtil.getDefault().toString());
			if (totales2 == null || totales2.isEmpty()){
				total = 1;
				JournalUtil.setParseValue("Totales2", article, LocaleUtil.getDefault().toString(), total.toString());
			}
			else {
				total = Integer.parseInt(totales2) + 1;
				JournalUtil.setParseValue("Totales2", article, LocaleUtil.getDefault().toString(), total.toString());
			}
		}
		else if (puntuacionONCYT.equals("3")){
			String totales3 = JournalUtil.getRootParseValue("Totales3", article, LocaleUtil.getDefault().toString());
			if (totales3 == null || totales3.isEmpty()){
				total = 1;
				JournalUtil.setParseValue("Totales3", article, LocaleUtil.getDefault().toString(), total.toString());
			}
			else {
				total = Integer.parseInt(totales3) + 1;
				JournalUtil.setParseValue("Totales3", article, LocaleUtil.getDefault().toString(), total.toString());
			}
		}
		
		return article;
	}
	
	
	
    public JournalArticle registrarPuntuacionOncytSG (JournalArticle article) throws Exception {
		//Extraemos los valores de los campos de ONCYT
    	List<String> puntuaciones = new ArrayList<String>();
    	//Recogemos las puntuaciones de la página
    	puntuaciones.add(JournalUtil.getRootParseValue("oportunidadArgentina", article, LocaleUtil.getDefault().toString()));
    	puntuaciones.add(JournalUtil.getRootParseValue("oportunidadBolivia", article, LocaleUtil.getDefault().toString()));
    	puntuaciones.add(JournalUtil.getRootParseValue("oportunidadBrasil", article, LocaleUtil.getDefault().toString()));
    	puntuaciones.add(JournalUtil.getRootParseValue("oportunidadChile", article, LocaleUtil.getDefault().toString()));
    	puntuaciones.add(JournalUtil.getRootParseValue("oportunidadColombia", article, LocaleUtil.getDefault().toString()));
    	puntuaciones.add(JournalUtil.getRootParseValue("oportunidadCostaRica", article, LocaleUtil.getDefault().toString()));
    	puntuaciones.add(JournalUtil.getRootParseValue("oportunidadCuba", article, LocaleUtil.getDefault().toString()));
    	puntuaciones.add(JournalUtil.getRootParseValue("oportunidadRepublicaDominicana", article, LocaleUtil.getDefault().toString()));
    	puntuaciones.add(JournalUtil.getRootParseValue("oportunidadEcuador", article, LocaleUtil.getDefault().toString()));
    	puntuaciones.add(JournalUtil.getRootParseValue("oportunidadEspania", article, LocaleUtil.getDefault().toString()));
    	puntuaciones.add(JournalUtil.getRootParseValue("oportunidadGuatemala", article, LocaleUtil.getDefault().toString()));
    	puntuaciones.add(JournalUtil.getRootParseValue("oportunidadHonduras", article, LocaleUtil.getDefault().toString()));
    	puntuaciones.add(JournalUtil.getRootParseValue("oportunidadMexico", article, LocaleUtil.getDefault().toString()));
    	puntuaciones.add(JournalUtil.getRootParseValue("oportunidadNicaragua", article, LocaleUtil.getDefault().toString()));
    	puntuaciones.add(JournalUtil.getRootParseValue("oportunidadPanama", article, LocaleUtil.getDefault().toString()));
    	puntuaciones.add(JournalUtil.getRootParseValue("oportunidadPeru", article, LocaleUtil.getDefault().toString()));
    	puntuaciones.add(JournalUtil.getRootParseValue("oportunidadPortugal", article, LocaleUtil.getDefault().toString()));
    	puntuaciones.add(JournalUtil.getRootParseValue("oportunidadParaguay", article, LocaleUtil.getDefault().toString()));
    	puntuaciones.add(JournalUtil.getRootParseValue("oportunidadElSalvador", article, LocaleUtil.getDefault().toString()));
    	puntuaciones.add(JournalUtil.getRootParseValue("oportunidadUruguay", article, LocaleUtil.getDefault().toString()));
    	puntuaciones.add(JournalUtil.getRootParseValue("oportunidadVenezuela", article, LocaleUtil.getDefault().toString()));
    	
    	//Persistimos las puntuaciones en la Solicitud
    	article = recogerPuntuacion(article, puntuaciones);
    	
		return article;
	}
    
    
    /**
     * Obtiene los totales para las puntuaciones recogidas en el ONCYT de Secretaría
     * @param article
     * @param puntuaciones
     * @return
     */
    private JournalArticle recogerPuntuacion (JournalArticle article, List<String> puntuaciones){
    	
    	//Recorremos las puntuaciones y totalizamos
    	Integer totales1= 0;
    	Integer totales2 = 0;
    	Integer totales3 = 0;
    	
    	for (String puntuacion : puntuaciones ){
    		Integer valor = 0;
    		try
    		{
    			valor = Integer.parseInt(puntuacion);
    		}
    		catch (NumberFormatException nex) {
    			valor = 0;
    		}
    		
    		switch(valor){
	    		case 1: {totales1 += 1; break;}
	    		case 2: {totales2 += 1; break;}
	    		case 3: {totales3 += 1; break;}
	    		default: break;
    		}
    	}
    	
    	//Finalizado el recuento, grabamos los totales
    	JournalUtil.setParseValue("Totales1", article, LocaleUtil.getDefault().toString(), totales1.toString());
    	JournalUtil.setParseValue("Totales2", article, LocaleUtil.getDefault().toString(), totales2.toString());
    	JournalUtil.setParseValue("Totales3", article, LocaleUtil.getDefault().toString(), totales3.toString());
    	
    	return article;
    }
    
    
    

	

	/**
	 * Obtiene el siguiente valor disponible del contador autonumï¿½rico.
	 * @return
	 */
	private String getSiguienteCodigosolicitud(JournalArticle article)
	{
		StringBuffer codigoRed = new StringBuffer("");

		//Obtenemos el valor siguiente del contador
		Long siguiente = journalUtil.getSiguiente(this.contadorProyecto);
		//Componemos el cï¿½digo de la propuesta
		String area = JournalUtil.getRootParseValue("areaTematica", article, LocaleUtil.getDefault().toString());
		String ejercicio = JournalUtil.getRootParseValue("ejercicio", article, LocaleUtil.getDefault().toString());

		if (ejercicio == null || ejercicio.isEmpty()) {
			Calendar cal = Calendar.getInstance();
			Integer year = cal.get(Calendar.YEAR);

			if (year != null) {
				ejercicio = String.valueOf(year);
			}
		}

		codigoRed.append("P")
		.append(area)
		.append(ejercicio.substring(2))
		.append("RT")
		.append(journalUtil.rellenarCadenaIzq(siguiente.toString(), "0", 4));
		logger.info("Nuevo cï¿½digo = " + codigoRed.toString());

		//ACTUALIZAMOS EL CONTADOR
		journalUtil.incrementaContador(this.contadorProyecto);
		//comprobamos cual es el siguiente valor a incluir
		logger.info("SIGUIENTE: " + journalUtil.getSiguiente(this.contadorProyecto));

		return codigoRed.toString();
	}
	
	
	private String dameCampoONCYT (String paisUsuario){
		String campo = null;
		
		if (paisUsuario.contains("Argentina")){
			campo = "oportunidadArgentina";
		}
		else if (paisUsuario.contains("Bolivia")){
			campo = "oportunidadBolivia";
		}
		else if (paisUsuario.contains("Brasil")){
			campo = "oportunidadBrasil";
		}
		else if (paisUsuario.contains("Chile")){
			campo = "oportunidadChile";
		}
		else if (paisUsuario.contains("Colombia")){
			campo = "oportunidadColombia";
		}
		else if (paisUsuario.contains("Costa Rica")){
			campo = "oportunidadCostaRica";
		}
		else if (paisUsuario.contains("Cuba")){
			campo = "oportunidadCuba";
		}
		else if (paisUsuario.contains("República Dominicana")){
			campo = "oportunidadRepublicaDominicana";
		}
		else if (paisUsuario.contains("Ecuador")){
			campo = "oportunidadEcuador";
		}
		else if (paisUsuario.contains("España")){
			campo = "oportunidadEspania";
		}
		else if (paisUsuario.contains("Guatemala")){
			campo = "oportunidadGuatemala";
		}
		else if (paisUsuario.contains("Honduras")){
			campo = "oportunidadHonduras";
		}
		else if (paisUsuario.contains("Mexico")){
			campo = "oportunidadMexico";
		}
		else if (paisUsuario.contains("Nicaragua")){
			campo = "oportunidadNicaragua";
		}
		else if (paisUsuario.contains("Panama")){
			campo = "oportunidadPanama";
		}
		else if (paisUsuario.contains("Peru")){
			campo = "oportunidadPeru";
		}
		else if (paisUsuario.contains("Portugal")){
			campo = "oportunidadPortugal";
		}
		else if (paisUsuario.contains("Paraguay")){
			campo = "oportunidadParaguay";
		}
		else if (paisUsuario.contains("El Salvador")){
			campo = "oportunidadElSalvador";
		}
		else if (paisUsuario.contains("Uruguay")){
			campo = "oportunidadUruguay";
		}
		else if (paisUsuario.contains("Venezuela")){
			campo = "oportunidadVenezuela";
		}
		
			
		return campo;
	}

	

}