package es.grupoica.cyted.contenido;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.liferay.compat.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portlet.dynamicdatamapping.model.DDMStructure;
import com.liferay.portlet.dynamicdatamapping.storage.Field;

public class FieldCreator  {
	
	
	private final Map<String,Object> journalData;
	public FieldCreator(Map<String,Object> journalData) {
		super();
		this.journalData = journalData;
	}
	 

	public Field createField(DDMStructure ddmStructure,
			 String fieldName, String parentFieldName)  throws PortalException, SystemException {		
		
		boolean isMultiple = isMultiple(ddmStructure, parentFieldName);
				
		Field field = isMultiple ? createMultipleField(ddmStructure, fieldName)
								 : createSimpleField(ddmStructure, fieldName);
		

		field.setDefaultLocale(LocaleUtil.getSiteDefault());
		return field;
	}
	
	@SuppressWarnings("unchecked")
	private Field createMultipleField(DDMStructure ddmStructure,
			 String fieldName)  throws PortalException, SystemException{

		Object obj = journalData.get(fieldName);
		List<Serializable> values;
		if(obj instanceof List){
			values = (List<Serializable>)journalData.get(fieldName);
		}else{
			values = new ArrayList<Serializable>();
			values.add((Serializable)obj);
		}		
				
		return new Field(ddmStructure.getStructureId(), 
				fieldName, 
				values,
				LocaleUtil.getSiteDefault());	
	}
	
	private Field createSimpleField(DDMStructure ddmStructure,
			 String fieldName)  throws PortalException, SystemException{		
				
		return new Field(ddmStructure.getStructureId(),
				fieldName,
				(Serializable)journalData.get(fieldName));
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
