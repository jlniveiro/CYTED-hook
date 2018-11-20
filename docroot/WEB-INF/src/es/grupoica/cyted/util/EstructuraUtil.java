package es.grupoica.cyted.util;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.service.ClassNameLocalServiceUtil;
import com.liferay.portal.service.CompanyLocalServiceUtil;
import com.liferay.portlet.dynamicdatamapping.NoSuchStructureException;
import com.liferay.portlet.dynamicdatamapping.model.DDMStructure;
import com.liferay.portlet.dynamicdatamapping.service.DDMStructureLocalServiceUtil;
import com.liferay.portlet.journal.model.JournalArticle;
public class EstructuraUtil {

	/**
	 * Obtiene el ID de la Estructura a la que pertenece el Journal
	 * @param ddmStructureKey
	 * @param journalArticle
	 * @return
	 * @throws PortalException
	 * @throws SystemException
	 */
	public Long getIdStructureJournal(String ddmStructureKey, JournalArticle journalArticle) throws PortalException, SystemException {

		Long idStructure = new Long(0);

		idStructure = this.getEstructuraJournal(ddmStructureKey, journalArticle).getStructureId();

		return idStructure;
	}

	/**
	 * Obtiene la estructura a la que pertenece el Journal
	 * @param ddmStructureKey
	 * @param journalArticle
	 * @return
	 * @throws PortalException
	 * @throws SystemException
	 */
	private DDMStructure getEstructuraJournal(String ddmStructureKey, JournalArticle journalArticle) throws PortalException, SystemException {

		DDMStructure structure = null;

		try { // try on the current site
		structure = DDMStructureLocalServiceUtil.getStructure(
				journalArticle.getGroupId(),
				ClassNameLocalServiceUtil.getClassNameId(JournalArticle.class.getName()),
				journalArticle.getStructureId());
		} catch (NoSuchStructureException noSuchStructureException) { // if not search on Global
		long globalGroupId = CompanyLocalServiceUtil.getCompany(journalArticle.getCompanyId()).getGroup().getGroupId();
		structure = DDMStructureLocalServiceUtil.getStructure(globalGroupId, ClassNameLocalServiceUtil.getClassNameId(JournalArticle.class.getName()), journalArticle.getStructureId());
		}

		return structure;
	}

}