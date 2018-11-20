package es.grupoica.cyted.contenido.campos;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletRequest;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portlet.dynamicdatamapping.model.DDMStructure;
import com.liferay.portlet.journal.model.JournalArticle;

import es.grupoica.cyted.util.JournalUtil;

public class SelectGetter extends AbstractValueGetter {

	@Override
	public Serializable getSimpleField(JournalArticle article,
			DDMStructure ddmStructure,	String fieldName) throws PortalException, SystemException {
		
		boolean isMultiple = "true".equals(ddmStructure.getFieldProperty(fieldName, "multiple"));
				
		String valueString = !isMultiple ? JournalUtil.getRootParseValue(fieldName, article, LocaleUtil.getDefault().toString())
							: StringUtil.merge(JournalUtil.getNode(fieldName, article, LocaleUtil.getDefault().toString()));
		
		return JSONFactoryUtil.createJSONArray("[" + valueString + "]").toString();
	}
	
	
	@Override
	public Serializable getSimpleField(Map<String,Object> journalData,
			DDMStructure ddmStructure,	String fieldName) throws PortalException, SystemException {
		
		boolean isMultiple = "true".equals(ddmStructure.getFieldProperty(fieldName, "multiple"));
				
		String valueString = !isMultiple ? journalData.get(fieldName).toString() : "";
		
		return JSONFactoryUtil.createJSONArray("[" + valueString + "]").toString();
	}

	@Override
	public List<Serializable> getMultipleField(JournalArticle article,
			DDMStructure ddmStructure, String fieldName) throws PortalException, SystemException {
		return  null;
	}

	@Override
	public boolean isMultipleFieldInJournal(JournalArticle article,
			DDMStructure ddmStructure, String fieldName)
			throws PortalException, SystemException {
        return (JournalUtil.getRootParseValue(fieldName, article, LocaleUtil.getDefault().toString()) != null);
	}


	@Override
	public List<Serializable> getMultipleField(Map<String, Object> journalData, DDMStructure ddmStructure,
			String fieldName) throws PortalException, SystemException {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
