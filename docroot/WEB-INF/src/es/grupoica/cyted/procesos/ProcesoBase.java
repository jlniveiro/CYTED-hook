package es.grupoica.cyted.procesos;

import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.service.RoleServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portlet.asset.model.AssetEntry;
import com.liferay.portlet.asset.service.AssetEntryLocalServiceUtil;
import com.liferay.portlet.asset.service.persistence.AssetEntryUtil;
import com.liferay.portlet.journal.model.JournalArticle;

import es.grupoica.cyted.bbdd.model.AreaTematica;
import es.grupoica.cyted.bbdd.model.ICAFlujoEstados;
import es.grupoica.cyted.bbdd.model.ICAFlujoRol;
import es.grupoica.cyted.bbdd.model.ICATarea;
import es.grupoica.cyted.bbdd.model.VistaAnuncios;
import es.grupoica.cyted.bbdd.service.AreaTematicaService;
import es.grupoica.cyted.bbdd.service.VistaAnunciosService;
import es.grupoica.cyted.service.CytedJournalArticleLocalService;
import es.grupoica.cyted.bbdd.service.ICAFlujoEstadosService;
import es.grupoica.cyted.bbdd.service.ICAFlujoRolService;
import es.grupoica.cyted.bbdd.service.ICATareaService;
import es.grupoica.cyted.util.AnuncioUtil;
import es.grupoica.cyted.util.Constantes;
import es.grupoica.cyted.util.JournalUtil;
import es.grupoica.cyted.util.RolUtil;
import es.grupoica.cyted.util.TareaUtil;
import es.grupoica.cyted.util.UsuarioUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class ProcesoBase {
	
	
	
	private ServiceContext serviceContext = null;
	private Notificacion notificacion = null;
	
	private static final Logger logger = LogManager.getLogger(ProcesoBase.class.getName());
	
	
	public ProcesoBase(ServiceContext serviceContext) {
		super();
		this.serviceContext = serviceContext;
	}

	public ServiceContext getServiceContext() {
		return serviceContext;
	}

	public void setServiceContext(ServiceContext serviceContext) {
		this.serviceContext = serviceContext;
	}

	/**
	 * Realiza el proceso de modificaciï¿½n de un contenido determinado (segï¿½n la estructura)
	 * @param oldArticle
	 * @param newArticle
	 * @param structureId
	 * @return
	 * @throws Exception
	 */
	protected JournalArticle procesarEdicion(JournalArticle newArticle,
			Long structureId, ICAFlujoEstados estadoInicial) throws Exception {

		//Obtenemos el/los flujos correspondientes
		ICAFlujoEstadosService serviceFlujos = new ICAFlujoEstadosService();
		List<ICAFlujoEstados> flujos = serviceFlujos.obtenerFlujosEstadoActual(estadoInicial);

		//Obtenemos el flujo para el estado inicial NUEVO.
		ICAFlujoEstados flujoSiguiente = null;
		List<ICAFlujoEstados> flujosCandidatos = new ArrayList<ICAFlujoEstados>();
		//Verificamos que existe un flujo para ambos estados

		if ( ! flujos.isEmpty()) {
			//Recorremos los flujos que cumplan el estado  y pï¿½gina de origen.

			for (ICAFlujoEstados flujo : flujos) {
				if (flujo.getUrlOrigen().equals(estadoInicial.getUrlOrigen())
						&& flujo.getIdEstadoOrigen().equals(estadoInicial.getIdEstadoOrigen()))
				{
					flujosCandidatos.add(flujo);
				}
			}

			if (flujosCandidatos.size() == 1) {
				flujoSiguiente = flujosCandidatos.get(0);
			}
			else if (flujosCandidatos.size() > 1) {
				//Exminamos el array de flujos candidatos y obtenemos los campos de la condiciï¿½n.
				//Si cumple la condiciï¿½n, serï¿½ el estado siguiente del Journal

				for (ICAFlujoEstados flujo : flujosCandidatos) {
					//Extraemos la condiciï¿½n
					String condicion = flujo.getCondicion();
					Map<String, String> mapaCondiciones = new HashMap<String, String>();

					//Extraemos las condiciones
					String signoComparacion = "";

					if (condicion != null && !condicion.isEmpty()) {
						String[] condiciones = condicion.split(",");
						//Obtenemos los valores y cargamos el mapa

						for (int c = 0; c < condiciones.length; c++) {
							String[] cond = condiciones[c].split("=");

							if (cond.length == 2) {
								mapaCondiciones.put(cond[0], cond[1]);
								signoComparacion = "=";
							}
							else {
								String[] cond2 = condiciones[c].split("<");

								if (cond2.length == 2) {
									mapaCondiciones.put(cond2[0], cond2[1]);
									signoComparacion = "<";
								}
								else {
									String[] cond3 = condiciones[c].split(">");

									if (cond3.length == 2) {
										mapaCondiciones.put(cond3[0], cond3[1]);
										signoComparacion = ">";
									}
								}
							}
						}//fin for

						//Examinamos el mapa de condiciones y obtenemos las variables del mismo nombre
						//y comprobamos si coinciden o no las condiciones.
						boolean cumpleCondicion = false;

						for (Map.Entry<String, String> entry : mapaCondiciones.entrySet()) {
							String valorCondicion = JournalUtil.getRootParseValue(entry.getKey(), newArticle, LocaleUtil.getDefault().toString());

							if (valorCondicion != null && signoComparacion == "=") {
								if ( ! valorCondicion.trim().equals(entry.getValue().trim())) {
									//No coincide el valor necesario para este flujo siguiente con el real
									//Salimos de la comparaciï¿½n y pasamos al siguiente flujo
									cumpleCondicion = false;
									break;
								}
								else {
									cumpleCondicion = true;
									break;
								}
							}
							else if (valorCondicion != null && signoComparacion == ">") {
								Integer valorCond = Integer.parseInt(valorCondicion.trim());
								Integer value = Integer.parseInt(entry.getValue());
								if ( valorCond.compareTo(value) > 0) {
									//No coincide el valor necesario para este flujo siguiente con el real
									//Salimos de la comparaciï¿½n y pasamos al siguiente flujo
									cumpleCondicion = true;
									break;
								}
								else {
									cumpleCondicion = false;
									break;
								}
							}
							else if (valorCondicion != null && signoComparacion == "<") {
								Integer valorCond = Integer.parseInt(valorCondicion.trim());
								Integer value = Integer.parseInt(entry.getValue());
								if ( valorCond.compareTo(value) < 0) {
									//No coincide el valor necesario para este flujo siguiente con el real
									//Salimos de la comparaciï¿½n y pasamos al siguiente flujo
									cumpleCondicion = true;
									break;
								}
								else {
									cumpleCondicion = false;
									break;
								}
							}//fin if (valorCondicion != null ......
						}//fin for Map

						//Si cumpleCondicion

						if (cumpleCondicion) {
							flujoSiguiente = flujo;
							break;
						}
					}//fin if (condicion != null && !condicion.isEmpty())

				}//fin for (ICAFlujoEstados flujo : flujosCandidatos)
			}//if (flujosCandidatos.size() == 1)

		}//fin if ( ! flujos.isEmpty())

		//Asignamos el estado obtenido al Journal

		if (flujoSiguiente != null) {

			JournalUtil.setParseValue("estado", newArticle, LocaleUtil.getDefault().toString(),
					Integer.toString(flujoSiguiente.getIdEstadoDestino()));

			//Vemos si hay algun campo mas adicional a modificar
			this.tratarCamposEspecificos(newArticle, structureId, flujoSiguiente, estadoInicial);

			//Enviamos las notificaciones para el flujo siguiente
			notificacion = new Notificacion(getServiceContext());
			newArticle = notificacion.procesarNotificacionesAutomaticas(newArticle, structureId, flujoSiguiente);
			
		}
		else if ( flujoSiguiente == null && structureId.equals(Constantes.ESTRUCTUREID_ACCION) ) {
			//Tratamos el caso de los cambios de Estado en el flujo de informes técnicos - informes de seguimiento
			flujoSiguiente = tratarExcepciones (structureId, newArticle, flujosCandidatos);
			//En los casos que no hay cambio de flujo, verificamos si algun campo se ve afectado por al actualización
			this.tratarCamposEspecificos(newArticle, structureId, flujoSiguiente, estadoInicial);
		}
		else {
			//En los casos que no hay cambio de flujo, verificamos si algun campo se ve afectado por al actualización
			this.tratarCamposEspecificos(newArticle, structureId, flujoSiguiente, estadoInicial);
		}//fin if (flujoSiguiente != null)

		return newArticle;
	}
	
	/**
	 * Tratamiento de casos personalizados de flujo.
	 * @param structureId
	 * @param flujosCandidatos
	 * @return
	 */
	private ICAFlujoEstados tratarExcepciones (Long structureId, JournalArticle newArticle, List<ICAFlujoEstados> flujosCandidatos) {
		
		ICAFlujoEstados flujoSiguiente = null;
		
		//Exminamos el array de flujos candidatos y obtenemos los campos de la condición
		//Si cumple la condición, sería el estado siguiente del Journal
		
		//Si es de estructura de Accioón
		if (structureId.equals(Constantes.ESTRUCTUREID_ACCION)){

			Accion accion = new Accion(this.getServiceContext());
			flujoSiguiente = accion.tratarFlujosSeguimiento(newArticle, flujosCandidatos);
		
		}//fin if (structureId.equals(Constantes.ESTRUCTUREID_ACCION))	
	
		return flujoSiguiente;
	}
	
	
	

	/**
	 * Realiza la inserciï¿½n de un nuevo contenido y crea los anuncios y tareas correspondientes
	 * @param article
	 * @param structureId
	 * @return
	 * @throws Exception
	 */
	protected JournalArticle procesarNuevo(JournalArticle article, Long structureId, ICAFlujoEstados estadoInicial) throws Exception {

		//Obtenemos el/los flujos correspondientes
		ICAFlujoEstadosService serviceFlujos = new ICAFlujoEstadosService();
		List<ICAFlujoEstados> flujos = serviceFlujos.obtenerFlujosEstadoActual(estadoInicial);

		//Obtenemos el flujo para el estado inicial NUEVO.
		ICAFlujoEstados flujoSiguiente = null;
		//Verificamos que existe un flujo para ambos estados

		if ( ! flujos.isEmpty()) {
			//Recorremos los flujos que cumplan la condiciï¿½n y pï¿½gina de origen

			for (ICAFlujoEstados flujo : flujos) {
				if (flujo.getUrlOrigen().equals(estadoInicial.getUrlOrigen())
						&& flujo.getCondicion().equals(estadoInicial.getCondicion()))
				{
					flujoSiguiente = flujo;
					break;
				}
			}
		}

		//Asignamos el estado obtenido al Journal

		if (flujoSiguiente != null) {
			try
			{
				JournalUtil.setParseValue("estado", article, LocaleUtil.getDefault().toString(),
						Integer.toString(flujoSiguiente.getIdEstadoDestino()));
			}
			catch (Exception ex){
				logger.error("No ha sido posible actualizar el estado para el Journal ID= " + article.getId());
			}
			//Vemos si hay algun campo mï¿½s adicional a modificar
			this.tratarCamposEspecificos(article, structureId, flujoSiguiente, estadoInicial);
			
			//Enviamos las notificaciones para el flujo siguiente
			notificacion = new Notificacion(getServiceContext());
			article = notificacion.procesarNotificacionesAutomaticas(article, structureId, flujoSiguiente);
			
		}//fin if (flujoSiguiente != null)

		return article;
	}

	protected AnuncioUtil anuncioUtil = new AnuncioUtil();
	protected JournalUtil journalUtil = new JournalUtil();
	protected RolUtil rolUtil = new RolUtil();
	protected VistaAnunciosService serviceAnuncio = new VistaAnunciosService();
	protected ICAFlujoRolService serviceFlujoRol = new ICAFlujoRolService();
	protected ICAFlujoEstadosService serviceFlujos = new ICAFlujoEstadosService();
	protected ICATareaService serviceTarea = new ICATareaService();
	protected TareaUtil tareaUtil = new TareaUtil();

	/**
	 * Obtiene el nombre del ï¿½rea temï¿½tica para un idArea dado
	 * @param idArea
	 * @return
	 * @throws Exception
	 */
	private String getNombreArea(Integer idArea) throws Exception {

		String nombreArea = "";

		try {
			AreaTematicaService serviceArea = new AreaTematicaService();
			List<AreaTematica> areas = serviceArea.obtenerArea(idArea);

			if (areas != null && !areas.isEmpty()) {
				nombreArea = areas.get(0).getNombreArea();
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

		return nombreArea;
	}

	/**
	 * Procesamientoo de algunos campos en el proceso de alta/modificaciï¿½n, segun estructura
	 * @param article
	 * @param structureId
	 * @param estadoSiguiente
	 * @param estadoInicial
	 * @return
	 */
	private JournalArticle tratarCamposEspecificos (JournalArticle article, Long structureId, ICAFlujoEstados estadoSiguiente, ICAFlujoEstados estadoInicial) throws Exception {

		/**
		 * Tratamiento de campos de RED TEMATICA
		 */
		if (structureId.equals(Constantes.ESTRUCTUREID_SOLICITUD_RT) || structureId.equals(Constantes.ESTRUCTUREID_SOLICITUD_PE) || 
				structureId.equals(Constantes.ESTRUCTUREID_SOLICITUD_LANDING)) {
			//En el caso de Envï¿½o a Secretarï¿½a, modificar la fecha de envï¿½o a Secretaria por la del dia actual

			if (estadoSiguiente != null && estadoSiguiente.getEstadoDestino().startsWith("02.")) {
				//La condiciï¿½n serï¿½ el campo a modificar (fechaEnvioSecretaria)
				JournalUtil.setParseValue("fechaEnvioSecretaria", article, LocaleUtil.getDefault().toString(),
						String.valueOf(Calendar.getInstance().getTimeInMillis()) );
			}

			//Verificamos si hay que pasar al siguiente estado o no.

			if (estadoSiguiente != null && estadoSiguiente.getCondicion().startsWith("evaluacionFinalizada")) {
				boolean error = false;
				Boolean evalFinalizada = true;
				try
				{
					List<Node> nodos = JournalUtil.getNode("Evaluaciones", article, LocaleUtil.getDefault().toString());

					for (Node nodo : nodos) {
						Node evaluacionFinalizada = nodo.selectSingleNode("dynamic-element[@name=\"evaluacionFinalizada\"]/dynamic-content");
						String value = evaluacionFinalizada.getText();

						if (!value.equals("true") && !value.equals("1")) {
							evalFinalizada = false;
							break;
						}
					}
				}
				catch (Exception ex) {
					ex.printStackTrace();
					error = true;
				}

				finally {
					if (!evalFinalizada || error) {
						//Dejamos el estado al anterior, pues la evaluaciï¿½n  no ha finalizado aun
						JournalUtil.setParseValue("estado", article, LocaleUtil.getDefault().toString(),
								Integer.toString(estadoInicial.getIdEstadoOrigen()));
					}
				}

			}//fin if

			//Verificamos si hay que pasar al siguiente estado o no.

			if (estadoSiguiente != null && estadoSiguiente.getCondicion().startsWith("evaluacionPanelFinalizada")) {
				boolean error = false;
				Boolean evalPanelFinalizada = true;
				try
				{
					String value = JournalUtil.getRootParseValue("evaluacionPanelFinalizada", article, LocaleUtil.getDefault().toString());

					if (!value.equals("true") && !value.equals("1")) {
						evalPanelFinalizada = false;
					}
				}
				catch (Exception ex) {
					ex.printStackTrace();
					error = true;
				}

				finally {
					if (!evalPanelFinalizada || error) {
						//Dejamos el estado al anterior, pues la evaluaciï¿½n  no ha finalizado aun
						JournalUtil.setParseValue("estado", article, LocaleUtil.getDefault().toString(),
								Integer.toString(estadoInicial.getIdEstadoOrigen()));
					}
				}

			}//fin if
			
			
			
			if (estadoSiguiente != null && estadoSiguiente.getCondicion().startsWith("puntuacionONCYT")) {
				boolean error = false;
				try
				{
					SolicitudRT solicitudRT = new SolicitudRT(this.getServiceContext());
					article = solicitudRT.registrarPuntuacionONCYT(article);
				}
				catch (Exception ex) {
					ex.printStackTrace();
					error = true;
				}
				

			}//fin if
			
			if (estadoSiguiente != null && estadoSiguiente.getCondicion().startsWith("puntuacionOncytSG")) {
				boolean error = false;
				try
				{
					SolicitudRT solicitudRT = new SolicitudRT(this.getServiceContext());
					article = solicitudRT.registrarPuntuacionOncytSG(article);
				}
				catch (Exception ex) {
					ex.printStackTrace();
					error = true;
				}
				

			}//fin if
			
			
			//Actualizamos el nombre del area tematica
			String idArea = JournalUtil.getRootParseValue("areaTematica", article, LocaleUtil.getDefault().toString());

			if (idArea != null && !idArea.isEmpty()) {
				String nombreArea = this.getNombreArea(Integer.parseInt(idArea));
				JournalUtil.setParseValue("nombreArea", article, LocaleUtil.getDefault().toString(), nombreArea);
			}
		}

		/**
		 * Tratamiento de campos de PROYECTOS ESTRATEGICOS.
		 */
		if (structureId.equals(Constantes.ESTRUCTUREID_SOLICITUD_PE)) {
			//Actualizamos el nombre del ï¿½rea temï¿½tica
			String idArea = JournalUtil.getRootParseValue("areaTematica", article, LocaleUtil.getDefault().toString());

			if (idArea != null && !idArea.isEmpty()) {
				String nombreArea = this.getNombreArea(Integer.parseInt(idArea));
				JournalUtil.setParseValue("nombreArea", article, LocaleUtil.getDefault().toString(), nombreArea);
			}
		}

		return article;
	}
	
	
	

}