package es.grupoica.cyted.contenido.campos;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletRequest;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portlet.dynamicdatamapping.model.DDMStructure;
import com.liferay.portlet.journal.model.JournalArticle;

import es.grupoica.cyted.util.JournalUtil;


public class SeparatorGetter extends AbstractValueGetter  {

	@Override
	public Serializable getSimpleField(JournalArticle article,
			DDMStructure ddmStructure, String fieldName)
			throws PortalException, SystemException {
		
		if(!ddmStructure.isFieldRepeatable(fieldName)){
			return StringPool.BLANK;
		}
		
		int numHijos = 1;
		for(String childrenName :ddmStructure.getChildrenFieldNames(fieldName)){
			int enviados = JournalUtil.getNode(fieldName, article, LocaleUtil.getDefault().toString()).size();
			if(enviados > numHijos){
				numHijos = enviados;
			}
		}
		return numHijos;		
	}
	
	@Override
	public Serializable getSimpleField(Map<String, Object> journalData,
			DDMStructure ddmStructure, String fieldName)
			throws PortalException, SystemException {
		
		if(!ddmStructure.isFieldRepeatable(fieldName)){
			return StringPool.BLANK;
		}
		
		return null;		
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
