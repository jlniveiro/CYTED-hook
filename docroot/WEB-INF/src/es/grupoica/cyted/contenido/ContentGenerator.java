package es.grupoica.cyted.contenido;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;

import es.grupoica.cyted.contenido.FieldValidationException;

public interface ContentGenerator {
	
	public String generateContent(/*DDMStructure ddmStructure,
			Map<String,Object> data*/) throws PortalException, SystemException, FieldValidationException;

}
