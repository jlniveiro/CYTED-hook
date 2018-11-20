package es.grupoica.cyted.contenido.campos;

import javax.portlet.PortletRequest;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portlet.dynamicdatamapping.model.DDMStructure;

public class RequestValueGetter {
	
	private final ValueGetter valueGetter;
	public RequestValueGetter(ValueGetter valueGetter) {
		super();
		this.valueGetter = valueGetter;
	}
	
	/*
	public boolean isParamInRequest(PortletRequest request, DDMStructure ddmStructure,
			 String fieldName, String parentFieldName)  throws PortalException, SystemException {
		
		return  isMultiple(ddmStructure, parentFieldName) ? valueGetter.isMultipleFieldInRequest(request, ddmStructure, fieldName)
				: valueGetter.isSimpleFieldInRequest(request, ddmStructure, fieldName);
	}

	public Object getValue(PortletRequest request, DDMStructure ddmStructure,
			 String fieldName, String parentFieldName)  throws PortalException, SystemException {
		
		return   isMultiple(ddmStructure, parentFieldName) ? valueGetter.getMultipleField(request, ddmStructure, fieldName)
									: valueGetter.getSimpleField(request, ddmStructure, fieldName);
	}
*/

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
