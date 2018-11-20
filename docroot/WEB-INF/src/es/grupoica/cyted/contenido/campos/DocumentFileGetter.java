package es.grupoica.cyted.contenido.campos;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.portlet.PortletRequest;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.upload.UploadRequest;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.service.DLAppServiceUtil;
import com.liferay.portlet.dynamicdatamapping.model.DDMStructure;
import com.liferay.portlet.journal.model.JournalArticle;

public class DocumentFileGetter extends AbstractValueGetter {
	
	// TODO 
	private Map<String, Object> journalData;
	
	public DocumentFileGetter(){}

	@Override
	public Serializable getSimpleField(JournalArticle article, DDMStructure ddmStructure, String fieldName)
			throws PortalException, SystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Serializable getSimpleField(Map<String, Object> journalData, DDMStructure ddmStructure, String fieldName)
			throws PortalException, SystemException {
		// TODO Auto-generated method stub
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
	
	
	
	

	/*
	@Override
	public Serializable getSimpleField(JournalArticle article,
			DDMStructure ddmStructure, String fieldName) throws PortalException, SystemException {

		UploadRequest uploadRequest = PortalUtil.getUploadPortletRequest(request);
		
		if(uploadRequest.getFile(fieldName)== null){
			
			// Si no tiene fichero obtenemos el antiguo
			return (Serializable)journalData.get(fieldName);
		}
		
		File file = uploadRequest.getFile(fieldName);
		String sourceFileName = uploadRequest.getFileName(fieldName);
		long folderId = ParamUtil.getLong(request, fieldName + "_folderId",-1L);
		//long folderId = ParamUtil.getLong(request, fieldName + "_folderId",-1L);
		String fileNameHeader = ParamUtil.getString(request, fieldName + "_fileNameHeader","___");
		
		return getValueDLFileEntry(request, file, sourceFileName, folderId, fileNameHeader, (Serializable)journalData.get(fieldName));
	}

	@Override
	public List<Serializable> getMultipleField(PortletRequest request,
			DDMStructure ddmStructure, String fieldName) throws PortalException, SystemException {

		
		return fromArray(insertDLFileEntries(request, fieldName, (Serializable[])journalData.get(fieldName)));
	}
	
	private static String[] insertDLFileEntries(PortletRequest request,
			String fieldName, Serializable[] oldValues){
		
		UploadRequest uploadRequest = PortalUtil.getUploadPortletRequest(request);
		if(uploadRequest.getFiles(fieldName)== null){
			return null;
		}
		
		File[] files = uploadRequest.getFiles(fieldName);
		String[] values = new String[files.length];
		
		for(int i = 0; i < files.length; i++){
			
			String oldValue = (oldValues != null && oldValues.length  > i )? (String)oldValues[i] : "";
			
			File file = files[i];
			if(file == null){
				values[i] = oldValue;
				continue;
			}
			String sourceFileName = uploadRequest.getFileNames(fieldName)[i];
			
			if(GetterUtil.get(sourceFileName, "").isEmpty()){
				values[i] = oldValue;
				continue;
			}
			
			long folderId = ParamUtil.getLong(request, fieldName + "_folderId",-1L);
			if(folderId == -1L){
				values[i] = "";
				continue;
			}
			String fileNameHeader = ParamUtil.getString(request, fieldName + "_fileNameHeader","___");
			
			values[i] = getValueDLFileEntry(request, file, sourceFileName, folderId, fileNameHeader, oldValue);
		}		
		return values;
	}	
	
	private static String getValueDLFileEntry(PortletRequest request, File file, String sourceFileName, long folderId, String fileNameHeader, Serializable oldValue){
		
		String value ="";		
		
		String mimeType = MimeTypesUtil.getContentType(file);
		String title = fileNameHeader + "_" + UUID.randomUUID() ;
		String changeLog = "";
		String description = sourceFileName;
		long size = file.length();
		
		try {		
			
			ServiceContext serviceContext =
					ServiceContextFactory.getInstance(DLFileEntry.class.getName(), request);
							
			ThemeDisplay themeDisplay= (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
			
			long groupId = themeDisplay.getScopeGroupId();			
			long repositoryId = groupId;

			
			FileEntry dlFileEntry;
			if (GetterUtil.get((String)oldValue, "").isEmpty()){
				dlFileEntry = DLAppServiceUtil.addFileEntry(repositoryId, folderId, sourceFileName, mimeType, title, description, changeLog, null, size, serviceContext);
			}else{
				JSONObject jDLOld = JSONFactoryUtil.createJSONObject((String)oldValue);
				dlFileEntry = DLAppServiceUtil.getFileEntryByUuidAndGroupId(jDLOld.getString("uuid"), jDLOld.getLong("groupId"));
				DLAppServiceUtil.updateFileEntry(dlFileEntry.getFileEntryId(), sourceFileName, mimeType, title, description, changeLog, true, file, serviceContext);
			}			
			
			JSONObject jsonDLFileEntry = JSONFactoryUtil.createJSONObject();
			jsonDLFileEntry.put("uuid", dlFileEntry.getUuid());
			jsonDLFileEntry.put("groupId", dlFileEntry.getGroupId());
			value= jsonDLFileEntry.toString();
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return value;
		
	}

  */

}
