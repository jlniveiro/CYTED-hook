package es.grupoica.cyted.contenido.campos;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletRequest;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portlet.dynamicdatamapping.model.DDMStructure;
import com.liferay.portlet.journal.model.JournalArticle;

public interface ValueGetter {

	/**
	 * Obtiene el valor para un derterminado canpo del Journal
	 * @param article
	 * @param ddmStructure
	 * @param fieldName
	 * @return
	 * @throws PortalException
	 * @throws SystemException
	 */
	public Serializable getSimpleField(JournalArticle article, 
			DDMStructure ddmStructure, String fieldName) throws PortalException, SystemException;
	
	/**
	 * Obtiene el valor para un deternminado campo del mapa
	 * @param journalData
	 * @param ddmStructure
	 * @param fieldName
	 * @return
	 * @throws PortalException
	 * @throws SystemException
	 */
	public Serializable getSimpleField(Map<String,Object> journalData, 
			DDMStructure ddmStructure, String fieldName) throws PortalException, SystemException;
	
	
	/**
	 * Obtiene los valores para tipo repetible dentro del Journal
	 * @param article
	 * @param ddmStructure
	 * @param fieldName
	 * @return
	 * @throws PortalException
	 * @throws SystemException
	 */
	public List<Serializable> getMultipleField(JournalArticle article,
			DDMStructure ddmStructure, String fieldName) throws PortalException, SystemException;
	
	
	/**
	 * Obtiene los valores para tipo repetible dentro del mapa
	 * @param journalData
	 * @param ddmStructure
	 * @param fieldName
	 * @return
	 * @throws PortalException
	 * @throws SystemException
	 */
	public List<Serializable> getMultipleField(Map<String,Object> journalData,
			DDMStructure ddmStructure, String fieldName) throws PortalException, SystemException;
	
	/**
	 * Comprueba si está el valor dentro del Journal
	 * @param article
	 * @param ddmStructure
	 * @param fieldName
	 * @return
	 * @throws PortalException
	 * @throws SystemException
	 */
	public boolean isSimpleFieldInJournal(JournalArticle article,
			DDMStructure ddmStructure, String fieldName) throws PortalException, SystemException;
	
	
	/**
	 * Comprueba si está el valor dentro del Mapa
	 * @param journalData
	 * @param ddmStructure
	 * @param fieldName
	 * @return
	 * @throws PortalException
	 * @throws SystemException
	 */
	public boolean isSimpleFieldInMap(Map<String,Object> journalData,
			DDMStructure ddmStructure, String fieldName) throws PortalException, SystemException;
			
	/**
	 * Comprueba si está dentro del Journal
	 * @param article
	 * @param ddmStructure
	 * @param fieldName
	 * @return
	 * @throws PortalException
	 * @throws SystemException
	 */
	public boolean isMultipleFieldInJournal(JournalArticle article,
			DDMStructure ddmStructure, String fieldName) throws PortalException, SystemException;
	
	
	/**
	 * Comprueba si está dentro del mapa
	 * @param journalData
	 * @param ddmStructure
	 * @param fieldName
	 * @return
	 * @throws PortalException
	 * @throws SystemException
	 */
	public boolean isMultipleFieldInMap(Map<String,Object> journalData,
			DDMStructure ddmStructure, String fieldName) throws PortalException, SystemException;

}
