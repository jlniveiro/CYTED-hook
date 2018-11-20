package es.grupoica.cyted.util;

import com.liferay.counter.model.Counter;
import com.liferay.counter.service.CounterLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portlet.asset.model.AssetEntry;
import com.liferay.portlet.asset.service.AssetEntryLocalServiceUtil;
import com.liferay.portlet.dynamicdatamapping.util.DDMXMLUtil;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;

import java.util.List;
public class JournalUtil {

	public static List<Node> getNode(String fieldName, JournalArticle article, String locale) {

		//document.getRootElement().selectNodes("//dynamic-element[@name='Evaluaciones']
		List<Node> nodes = null;
		try {
		Document document = SAXReaderUtil.read(article.getContentByLocale(locale));
		nodes = document.getRootElement().selectNodes("/root/dynamic-element[@name='" + fieldName +"']");
		//value = node.getText();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return nodes;
	}

	/**
	 * Obtiene el contenido de un determinado valor de un campo hijo de un Nodo con hijos del JournalArticle especificado.
	 * @param nodo
	 * @param fieldname
	 * @param article
	 * @param locale
	 * @return
	 */
	public static String getNodeParseValue(Node nodo, String fieldname, JournalArticle article, String locale) {
		String value = "";
		try {
			Node node = nodo.selectSingleNode("dynamic-element[@name='" + fieldname +"']/dynamic-content");
			if (node != null){
				value = node.getText();
			}
		} catch (Exception e) {
			e.printStackTrace();
			value = "";
		}

		return value;
	}

	/**
	 * Obtiene el contenido de un determinado campo del JournalArticle especificado.
	 * @param fieldname
	 * @param article
	 * @param locale
	 * @return
	 */
	public String getPaginaLlamada(String url) {
		String pagina = "";
		try {
			 //Obtenemos la �ltima "/"
			 int indice = url.lastIndexOf("/");
			 pagina = url.substring(indice + 1);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return pagina;
	}

	/**
	 * Obtiene el contenido de un determinado campo desde la raiz del JournalArticle especificado.
	 * @param fieldname
	 * @param article
	 * @param locale
	 * @return
	 */
	public static String getRootParseValue(String fieldname, JournalArticle article, String locale) {
		String value = null;
		try {
			Document document = SAXReaderUtil.read(article.getContentByLocale(locale));
			Node node = document.selectSingleNode("/root/dynamic-element[@name='" + fieldname +"']/dynamic-content");
			if (node != null){
				value = node.getText();
			}
		} catch (Exception e) {
			e.printStackTrace();
			value = null;
		}

		return value;
	}

	public Long getSiguiente(String nombreContador) {
		Long numcont = new Long(0);
		try
		{
			Counter contador = CounterLocalServiceUtil.getCounter(nombreContador);

			if (contador == null) {
				contador = CounterLocalServiceUtil.createCounter(nombreContador);
			}

			numcont = contador.getCurrentId();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

		return numcont;
	}

	public void incrementaContador(String nombreContador) {
		try
		{
			CounterLocalServiceUtil.increment(nombreContador, 1);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Rellena caracteres por la derecha
	 * @param cadena
	 * @param caracter
	 * @param ancho
	 * @return
	 */
	public String rellenarCadena(String cadena, String caracter, int ancho) {

		while (cadena.length() < ancho) {
			cadena+= caracter;
		}

		return cadena.substring(0, ancho);
	}

	/**
	 * Rellena caracteres por la izquierda
	 * @param cadena
	 * @param caracter
	 * @param ancho
	 * @return
	 */
	public String rellenarCadenaIzq(String cadena, String caracter, int ancho) {

	while (cadena.length() < ancho) {
		cadena = caracter+cadena;
	}

	return cadena.substring(0, ancho);
	}

	/**
	 * Modifica el valor del campo solicitado por el nuevo valor.
	 * @param fieldname
	 * @param article
	 * @param locale
	 * @param newValue
	 */
	public static void setParseValue(String fieldname, JournalArticle article, String locale, String newValue) {

		try {
			 //System.out.println(article.getContent());

			Document document = null;
			Node node = null;
			 //Extraemos el XML del Journal
			 //document = SAXReaderUtil.read(article.getContentByLocale(locale));
			 //rootElement = document.getRootElement();

			String xmlString = article.getContent();
			document = SAXReaderUtil.read(xmlString);
			node = document.selectSingleNode("/root/dynamic-element[@name='" + fieldname +"']/dynamic-content");
			
			 //node.setText("![CDATA[" + newValue + "]]");
			 node.setText(newValue);
			 String newContent = DDMXMLUtil.formatXML(document);
			 article.setContent(newContent);

			JournalArticle ja = JournalArticleLocalServiceUtil.updateJournalArticle(article);
		     //System.out.println("CONTENIDO1: " + ja.getContent());
		     //System.out.println("CONTENIDO2: " + article.getContent());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Obtiene el AssetEntry de un JournalArticle
	 * @param journalArticle
	 * @return
	 * @throws PortalException
	 * @throws SystemException
	 */
	public static AssetEntry getAssetEntry(JournalArticle journalArticle) throws PortalException, SystemException{
		return AssetEntryLocalServiceUtil.getEntry(JournalArticle.class.getName(),
				journalArticle.getResourcePrimKey());
	}

}