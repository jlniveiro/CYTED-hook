package es.grupoica.cyted.contenido;

import java.util.Map;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portlet.dynamicdatamapping.model.DDMStructure;
import com.liferay.portlet.dynamicdatamapping.storage.Field;
import com.liferay.portlet.dynamicdatamapping.storage.Fields;
import com.liferay.portlet.journal.util.JournalConverterUtil;

import es.grupoica.cyted.contenido.FieldCreator;
import es.grupoica.cyted.contenido.FieldValidationException;

public class ContentGeneratorImpl implements ContentGenerator {
	
	 private DDMStructure ddmStructure;
	 private Map<String, Object> journalData;
	 
	 

	public ContentGeneratorImpl(DDMStructure ddmStructure,
			Map<String, Object> journalData) {
		super();
		this.ddmStructure = ddmStructure;
		this.journalData = journalData;
	}

	@Override
	public String generateContent(/*DDMStructure ddmStructure,
			Map<String, Object> journalData*/) throws PortalException, SystemException, FieldValidationException{
		
		// Obtenemos la estructura de la request 
		Fields ddmFields = createJournalFields(	ddmStructure, 
				journalData);
		
		// Actualizamos el fieldDisplay
		updateFieldsDisplay(ddmFields, ddmStructure);

		try {

			// Creamos el contenido a partir de los fields
			return  JournalConverterUtil.getContent(ddmStructure, ddmFields);
			
		} catch (Exception e) {
			throw new SystemException(e);
		}
	}
	
	private Fields createJournalFields(DDMStructure ddmStructure, Map<String, Object> journalData) throws PortalException, SystemException{
		
		Fields ddmFields = crearDDMFields(ddmStructure);
		
		for (String fieldName: ddmStructure.getRootFieldNames()){
			
			if("_fieldsDisplay".equals(fieldName)){
				continue;
			}
			
			addField(ddmStructure, fieldName, journalData, ddmFields, null);			
		}
		return ddmFields;
	}
	
	private void updateFieldsDisplay(Fields ddmFields, DDMStructure ddmStructure) throws PortalException, SystemException {
		
		for (String fieldName: ddmStructure.getRootFieldNames()){
			
			if("_fieldsDisplay".equals(fieldName)){
				continue;
			}			
			updateFieldsDisplay(ddmFields, fieldName, ddmStructure, null);
		}		
	}
	
	/**
	 * 
	 * @param request
	 * @param ddmStructure
	 * @param fieldName
	 * @param datosArticulo
	 * @param ddmFields
	 * @param parentRepeateable
	 * @return
	 * @throws PortalException
	 * @throws SystemException
	 */
	private void addField(DDMStructure ddmStructure,
			String fieldName,  Map<String,Object> journalData, Fields ddmFields, String parentFieldName)
					throws PortalException, SystemException{
		
		String fieldType = ddmStructure.getFieldType(fieldName);		
		
		if(!"ddm-separator".equals(fieldType)){
			ddmFields.put(
					new FieldCreator(journalData).createField(ddmStructure, fieldName, parentFieldName));
		}
				
		for (String childFieldName : ddmStructure.getChildrenFieldNames(fieldName)){
			addField(ddmStructure, childFieldName, journalData, ddmFields, fieldName);
		}
	}	
	

	
	@SuppressWarnings("unused")
	private void updateFieldsDisplay(Fields ddmFields, String fieldName, DDMStructure ddmStructure, String parentName) throws PortalException, SystemException {
		
		
		boolean isParentRepeateable = (parentName != null && ddmStructure.getFieldRepeatable(parentName));
		boolean isRepeateable = ddmStructure.getFieldRepeatable(fieldName);
		
		if(!isRepeateable && !isParentRepeateable){
			
			updateFieldsDisplay(ddmFields, fieldName);
			return;
		}

		if(!ddmStructure.getChildrenFieldNames(fieldName).isEmpty()){			

			String firstChildName = ddmStructure.getChildrenFieldNames(fieldName).get(0);			
			if(ddmStructure.getFieldRepeatable(firstChildName)){
				updateFieldsDisplay(ddmFields, firstChildName, ddmStructure, fieldName);
			};
			
			// Obtenemos aquel que tenga un mayor número de valores
			int numValues = 0;
			for(String childName : ddmStructure.getChildrenFieldNames(fieldName)){
				
				Field childFile = ddmFields.get(childName);
				
				if(childFile != null && childFile.getValues(LocaleUtil.getSiteDefault()).size() > numValues){
					numValues = childFile.getValues(LocaleUtil.getSiteDefault()).size();
				}
			}
			
			for(int i = 0; i < numValues; i++){
				
				updateFieldsDisplay(ddmFields, fieldName);
				
				for (String childFieldName : ddmStructure.getChildrenFieldNames(fieldName)){
					
					updateFieldsDisplay(ddmFields, childFieldName);
				}
			}
			
			
			
			// FIn
			/*
			Field childFile = ddmFields.get(firstChildName);
			if(childFile == null){
				return;
			}
			
			
			for(Serializable value : childFile.getValues(LocaleUtil.getSiteDefault())){
				
				updateFieldsDisplay(ddmFields, fieldName);
				
				for (String childFieldName : ddmStructure.getChildrenFieldNames(fieldName)){
					
					updateFieldsDisplay(ddmFields, childFieldName);
				}
			}
			*/
		}
	}

	
	/**
	 * Crea un objeto Fields vacío. 
	 * 
	 * @param ddmStructure
	 * @return
	 * @throws PortalException
	 * @throws SystemException
	 */
	private Fields crearDDMFields(DDMStructure ddmStructure) throws PortalException, SystemException{

		Fields ddmFields = new Fields();
		
		ddmFields.getAvailableLocales().clear();
		ddmFields.getAvailableLocales().add(LocaleUtil.getSiteDefault());
		
		
		Field fieldsDisplayField = new Field(
		ddmStructure.getStructureId(), "_fieldsDisplay",
		StringPool.BLANK);		
		
		ddmFields.put(fieldsDisplayField);
		
		return ddmFields;
	} 
	
	private void updateFieldsDisplay(Fields ddmFields, String fieldName) {
		String fieldsDisplayValue =
			fieldName.concat("_INSTANCE_").concat(
				StringUtil.randomString());

		Field fieldsDisplayField = ddmFields.get( "_fieldsDisplay");

		String[] fieldsDisplayValues = StringUtil.split(
			(String)fieldsDisplayField.getValue());

		fieldsDisplayValues = ArrayUtil.append(
			fieldsDisplayValues, fieldsDisplayValue);

		fieldsDisplayField.setValue(StringUtil.merge(fieldsDisplayValues));
	}	

}
