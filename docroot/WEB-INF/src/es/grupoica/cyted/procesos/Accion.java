package es.grupoica.cyted.procesos;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.xml.Node;
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

public class Accion extends ProcesoBase {
	
	public Accion(ServiceContext serviceContext) {
		super(serviceContext);
		// TODO Auto-generated constructor stub
	}

	public JournalArticle procesarAccion(Long structureId, String pagina, JournalArticle article) {

		return article;
	}

	
	/**
	 * Tratamiento para la edicionn de una solicitud existente.
	 * @param article
	 * @param oldArticle
	 * @param structureId
	 * @param pagina
	 * @return
	 * @throws Exception
	 */
	public JournalArticle tratarEdicionAccion(JournalArticle article, Long structureId, String pagina) throws Exception {

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
	 * Trata los casos especiales de seguimiento para las acciones de RT y PTE
	 * @param newArticle
	 * @param flujosCandidatos
	 * @return
	 */
	public ICAFlujoEstados tratarFlujosSeguimiento (JournalArticle newArticle, List<ICAFlujoEstados> flujosCandidatos){
		
		ICAFlujoEstados flujoSiguiente = null;
		
		//Obtenemos los FLUJOS CANDIDATOS para el estado inicial de la ACCIÓN		
		for (ICAFlujoEstados flujo : flujosCandidatos) {
			//Extraemos la condición
			String condicion = flujo.getCondicion();
			Map<String, String> mapaCondiciones = new HashMap<String, String>();

			//Extraemos las condiciones
			String signoComparacion = "";

			//Obtenemos el MAPA DE CONDICIONES para el FLUJO ACTUAL
			if (condicion != null && !condicion.isEmpty()) {
				String[] condiciones = condicion.split(",");
				//Obtenemos los valores y cargamos el mapa de CONDICIONES
				for (int c = 0; c < condiciones.length; c++) {
					String[] cond = condiciones[c].split("=");
					if (cond.length == 2) {
						signoComparacion = "=";
						mapaCondiciones.put(cond[0], signoComparacion + cond[1]);
					}
					else {
						String[] cond2 = condiciones[c].split("<");
						if (cond2.length == 2) {
							signoComparacion = "<";
							mapaCondiciones.put(cond2[0], signoComparacion + cond2[1]);
						}
						else {
							String[] cond3 = condiciones[c].split(">");
							if (cond3.length == 2) {
								signoComparacion = ">";
								mapaCondiciones.put(cond3[0], signoComparacion + cond3[1]);
							}
						}
					}
				}//fin for

				//Examinamos el MAPA DE CONDICIONES y obtenemos las variables del mismo nombre
				//y comprobamos si coinciden o no las condiciones.
				boolean cumpleCondicion = false;
				//Valores que utilizaremos para la comprobación de datos
				List<Node> nodoSeguimiento = null;
				String ejercicioEnCurso = "";
				String anyoInforme = "";
				String anyoSeleccionado = "";
				//Mapa de comparación de valores
				for (Map.Entry<String, String> entry : mapaCondiciones.entrySet()) {
					
					if (entry.getKey().trim().equals("ejercicioEnCurso")){
						//Obtenemos el valor actual del Ejercicio en Curso
						ejercicioEnCurso = JournalUtil.getRootParseValue("ejercicioEnCurso", newArticle, LocaleUtil.getDefault().toString());
						//Obtenemos el valor del campo al que se le iguala y compara
						anyoInforme = entry.getValue().substring(1).trim();
						
						//Obtenemos el nodo que le corresponde y vemos el EJERCICIO en curso con que INFORME corresponde
						//Dependiendo del valor cargamos una u otra lista de Nodos
						if (anyoInforme.trim().equals("AnyoInformeSeguimiento1Anyo")){
							nodoSeguimiento  = JournalUtil.getNode("InformeSeguimiento1Anyo", newArticle, LocaleUtil.getDefault().toString());
						}
						else if (anyoInforme.trim().equals("AnyoInformeSeguimientoAnual")){
							nodoSeguimiento  = JournalUtil.getNode("InformeSeguimientoAnual", newArticle, LocaleUtil.getDefault().toString());
						}
						else if (anyoInforme.trim().equals("AnyoInformeSeguimientoFinal")){
							nodoSeguimiento  = JournalUtil.getNode("InformeSeguimientoFinal", newArticle, LocaleUtil.getDefault().toString());
						}
						
						//Extraemos los valores para comparación y los metermos en un mapa
						for (Node nodo : nodoSeguimiento) {
							Node anyoInformeAnyo = nodo.selectSingleNode("dynamic-element[@name=\"" + anyoInforme + "\"]/dynamic-content");
							String value = (anyoInformeAnyo != null) ? anyoInformeAnyo.getText() : "";

							if (value.trim().equals(ejercicioEnCurso)) {
								anyoSeleccionado = anyoInforme.substring("AnyoInformeSeguimiento".length());
								break;
							}
						}//fin for
					}
					else {
						//Obtenemos los valores para el nodo seleccionado
						List<Node> nodeEvaluacion = null;
						for (Node nodo : nodoSeguimiento) {
							List<Node> evaluaciones = nodo.selectNodes("dynamic-element[@name=\"EvaluacionRedTematicaInformeSeguimiento" + anyoSeleccionado + "\"]/dynamic-content");
							for (Node nodoEval : evaluaciones) {
								Node criterio = nodo.selectSingleNode("dynamic-element[@name=\"" + entry.getKey() + anyoSeleccionado + "\"]/dynamic-content");
								String valorCondicion = (criterio != null) ? criterio.getText() : "";
								
								if (valorCondicion != null && entry.getValue().substring(0, 1) == "=") {
									if ( ! valorCondicion.trim().equals(entry.getValue().substring(1).trim())) {
										//No coincide el valor necesario para este flujo siguiente con el real
										//Salimos de la comparación y pasamos al siguiente flujo
										cumpleCondicion = false;
										break;
									}
									else {
										cumpleCondicion = true;
										break;
									}
								}
								else if (valorCondicion != null && entry.getValue().substring(0, 1) == ">") {
									Integer valorCond = Integer.parseInt(valorCondicion.trim());
									Integer value = Integer.parseInt(entry.getValue().substring(1));
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
								else if (valorCondicion != null && entry.getValue().substring(0, 1) == "<") {
									Integer valorCond = Integer.parseInt(valorCondicion.trim());
									Integer value = Integer.parseInt(entry.getValue().substring(1));
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
							}//fin for (Node nodoEval : evaluaciones)
						}//fin for (Node nodo : nodoSeguimiento)
					}//fin if (entry.getKey().trim().equals("ejercicioEnCurso"))
				    
								
					
				}//fin for Map

				//Si cumpleCondicion

				if (cumpleCondicion) {
					flujoSiguiente = flujo;
					break;
				}
			}//fin if (condicion != null && !condicion.isEmpty())

		}//fin for (ICAFlujoEstados flujo : flujosCandidatos)
		
		return flujoSiguiente;
	}

	

	

	protected AnuncioUtil anuncioUtil = new AnuncioUtil();
	protected String contadorProyecto = "com.liferay.portlet.journal.service.AccionJournalArticleCounter";
	protected JournalUtil journalUtil = new JournalUtil();
	protected RolUtil rolUtil = new RolUtil();
	protected ICAAnuncioService serviceAnuncio = new ICAAnuncioService();
	protected ICAFlujoRolService serviceFlujoRol = new ICAFlujoRolService();
	protected ICAFlujoEstadosService serviceFlujos = new ICAFlujoEstadosService();

	/**
	 * Obtiene el siguiente valor disponible del contador autonumerico.
	 * @return
	 */
	private String getSiguienteCodigoAccion(JournalArticle article)
	{
		StringBuffer codigoProyecto = new StringBuffer("");

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

		codigoProyecto.append("P")
		.append(area)
		.append(ejercicio.substring(2))
		.append("PTE")
		.append(journalUtil.rellenarCadenaIzq(siguiente.toString(), "0", 4));
		logger.info("Nuevo codigo = " + codigoProyecto.toString());

		//ACTUALIZAMOS EL CONTADOR
		journalUtil.incrementaContador(this.contadorProyecto);
		//comprobamos cual es el siguiente valor a incluir
		logger.info("SIGUIENTE: " + journalUtil.getSiguiente(this.contadorProyecto));

		return codigoProyecto.toString();
	}

	private static final Logger logger = LogManager.getLogger(Accion.class.getName());

}
