package es.grupoica.cyted.contenido.campos;

import java.util.Map;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portlet.dynamicdatamapping.model.DDMStructure;
import com.liferay.portlet.journal.model.JournalArticle;

public class JournalValueGetter {
	
	private final ValueGetter valueGetter;
	public JournalValueGetter(ValueGetter valueGetter) {
		super();
		this.valueGetter = valueGetter;
	}
	
	public boolean isParamInMap(Map<String,Object> journalData, DDMStructure ddmStructure,
			 String fieldName, String parentFieldName)  throws PortalException, SystemException {
		
		return  isMultiple(ddmStructure, parentFieldName) ? valueGetter.isMultipleFieldInMap(journalData, ddmStructure, fieldName)
				: valueGetter.isSimpleFieldInMap(journalData, ddmStructure, fieldName);
	}

	public Object getValue(Map<String,Object> journalData, DDMStructure ddmStructure,
			 String fieldName, String parentFieldName)  throws PortalException, SystemException {
		
		return   isMultiple(ddmStructure, parentFieldName) ? valueGetter.getMultipleField(journalData, ddmStructure, fieldName)
									: valueGetter.getSimpleField(journalData, ddmStructure, fieldName);
	}
	
	
	
	public boolean isParamInJournal(JournalArticle article, DDMStructure ddmStructure,
			 String fieldName, String parentFieldName)  throws PortalException, SystemException {
		
		return  isMultiple(ddmStructure, parentFieldName) ? valueGetter.isMultipleFieldInJournal(article, ddmStructure, fieldName)
				: valueGetter.isSimpleFieldInJournal(article, ddmStructure, fieldName);
	}

	public Object getValue(JournalArticle article, DDMStructure ddmStructure,
			 String fieldName, String parentFieldName)  throws PortalException, SystemException {
		
		return   isMultiple(ddmStructure, parentFieldName) ? valueGetter.getMultipleField(article, ddmStructure, fieldName)
									: valueGetter.getSimpleField(article, ddmStructure, fieldName);
	}


	/**
	 * Comprueba si el campo pertenece a un campo que es repetible
	 * @param ddmStructure
	 * @param parentFieldName
	 * @return
	 * @throws PortalException
	 * @throws SystemException
	 */
	private boolean isMultiple(DDMStructure ddmStructure, String parentFieldName) throws PortalException, SystemException{
		// TODO Cambiar, habría que comprobar que algún ancestro es repetible, no sólo el padre. 
		return !GetterUtil.getString(parentFieldName,"").isEmpty() 
				&& ddmStructure.getFieldRepeatable(parentFieldName);
	}

}
