package es.grupoica.cyted.contenido.campos;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletRequest;

import com.liferay.compat.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portlet.dynamicdatamapping.model.DDMStructure;
import com.liferay.portlet.journal.model.JournalArticle;

import es.grupoica.cyted.util.JournalUtil;

public class CheckGetter extends AbstractValueGetter {

	@Override
	public Serializable getSimpleField(JournalArticle article,
			DDMStructure ddmStructure, String fieldName) throws PortalException, SystemException {

		return JournalUtil.getRootParseValue(fieldName, article, LocaleUtil.getDefault().toString());
	}
	
	@Override
	public Serializable getSimpleField(Map<String, Object> journalData,
			DDMStructure ddmStructure, String fieldName) throws PortalException, SystemException {

		return journalData.get(fieldName).toString();
	}

	
	@Override
	public boolean isSimpleFieldInJournal(JournalArticle article,
			DDMStructure ddmStructure, String fieldName)
			throws PortalException, SystemException {

		return JournalUtil.getRootParseValue(fieldName, article, LocaleUtil.getDefault().toString()) != null;	
	}
	
	@Override
	public boolean isSimpleFieldInMap(Map<String, Object> journalData,
			DDMStructure ddmStructure, String fieldName)
			throws PortalException, SystemException {

		return journalData.get(fieldName) != null;
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
