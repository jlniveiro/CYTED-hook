package es.grupoica.cyted.contenido.campos;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletRequest;

import com.liferay.compat.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portlet.dynamicdatamapping.model.DDMStructure;
import com.liferay.portlet.journal.model.JournalArticle;

import es.grupoica.cyted.util.JournalUtil;

public class DateGetter extends AbstractValueGetter {
	
	private DateFormat dateFormat;
	
	public DateGetter(String dateFormat){
		try{
			this.dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(dateFormat);
		}catch(Exception ex){
			this.dateFormat = new SimpleDateFormat(dateFormat);
		}
	}
	
	
	private Serializable getValue(JournalArticle article,
			DDMStructure ddmStructure, String fieldName) throws PortalException, SystemException{
		
		return JournalUtil.getRootParseValue(fieldName, article, LocaleUtil.getDefault().toString());
	}

	
	private Serializable getValue(Map<String, Object> journalData,
			DDMStructure ddmStructure, String fieldName) throws PortalException, SystemException{
		
		return journalData.get(fieldName).toString();
	}

	@Override
	public Serializable getSimpleField(JournalArticle article,
			DDMStructure ddmStructure, String fieldName) throws PortalException, SystemException {

		Serializable value = getValue(article, ddmStructure, fieldName);
		return value != null? value : "";
	}
	
	
	@Override
	public Serializable getSimpleField(Map<String, Object> journalData,
			DDMStructure ddmStructure, String fieldName) throws PortalException, SystemException {

		Serializable value = getValue(journalData, ddmStructure, fieldName);
		return value != null? value : "";
	}


	@Override
	public List<Serializable> getMultipleField(JournalArticle article, DDMStructure ddmStructure, String fieldName)
			throws PortalException, SystemException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<Serializable> getMultipleField(Map<String, Object> journalData, DDMStructure ddmStructure,
			String fieldName) throws PortalException, SystemException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
}
