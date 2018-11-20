package es.grupoica.cyted.contenido.campos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portlet.dynamicdatamapping.model.DDMStructure;
import com.liferay.portlet.journal.model.JournalArticle;

import es.grupoica.cyted.util.JournalUtil;


public abstract class AbstractValueGetter implements ValueGetter {
	
	
	@Override
	public boolean isSimpleFieldInJournal(JournalArticle article, DDMStructure ddmStructure, String fieldName)
			throws PortalException, SystemException {
		
		return JournalUtil.getRootParseValue(fieldName, article, LocaleUtil.getDefault().toString()) != null;
	}

	@Override
	public boolean isSimpleFieldInMap(Map<String, Object> journalData, DDMStructure ddmStructure, String fieldName)
			throws PortalException, SystemException {
		// TODO Auto-generated method stub
		return journalData.get(fieldName) != null;
	}

	@Override
	public boolean isMultipleFieldInJournal(JournalArticle article, DDMStructure ddmStructure, String fieldName)
			throws PortalException, SystemException {
		// TODO Auto-generated method stub
		return JournalUtil.getNode(fieldName, article, LocaleUtil.getDefault().toString()) != null;
	}

	@Override
	public boolean isMultipleFieldInMap(Map<String, Object> journalData, DDMStructure ddmStructure, String fieldName)
			throws PortalException, SystemException {
		// TODO Auto-generated method stub
		return journalData.get(fieldName) != null;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
	}

	
	protected <E> List<Serializable> fromArray(E[] array) {
		List<Serializable> list = new ArrayList<Serializable>();
		if (ArrayUtil.isEmpty(array)) {
			return list;
		}

		for(E e : array){
			list.add((Serializable)e);
		}
		return list;
	}
	
	
}
