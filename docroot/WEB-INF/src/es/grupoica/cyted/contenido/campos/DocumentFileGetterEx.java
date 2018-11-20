package es.grupoica.cyted.contenido.campos;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
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

public class DocumentFileGetterEx extends AbstractValueGetter {

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
	public Serializable getSimpleField(PortletRequest request,
			DDMStructure ddmStructure, String fieldName) throws PortalException, SystemException {

		UploadRequest uploadRequest = PortalUtil.getUploadPortletRequest(request);
		
		if(uploadRequest.getFile(fieldName)== null){			
			return null;
		}
		
		// Obtenemos los datos necesarios para meter
		// el fichero
		
		File file = uploadRequest.getFile(fieldName);
		String sourceFileName = uploadRequest.getFileName(fieldName);
		long folderId = ParamUtil.getLong(request, fieldName + "_folderId",-1L);
		String fileNameHeader = ParamUtil.getString(request, fieldName + "_fileNameHeader","___");
		String previousFileName = ParamUtil.getString(request, fieldName + "_previousFileName","");
		
		return new DocumentValue(file, sourceFileName, folderId, fileNameHeader, previousFileName);
	}

	@Override
	public List<Serializable> getMultipleField(PortletRequest request,
			DDMStructure ddmStructure, String fieldName) throws PortalException, SystemException {
		
		UploadRequest uploadRequest = PortalUtil.getUploadPortletRequest(request);
		
		if(uploadRequest.getFile(fieldName)== null){			
			return null;
		}
		
		List<Serializable> values = new ArrayList<Serializable>();
		
		File[] files = uploadRequest.getFiles(fieldName);
		String[] sourceFileNames = uploadRequest.getFileNames(fieldName);
		long[] folderIds = ParamUtil.getLongValues(request, fieldName + "_folderId");
		String[] fileNameHeaders = ParamUtil.getParameterValues(request, fieldName + "_fileNameHeader");
		String[] previousFileNames = ParamUtil.getParameterValues(request, fieldName + "_previousFileName");
		
		
		for(int i=0; i < files.length; i++){			
			values.add(new DocumentValue(files[i], 
					sourceFileNames.length > 0 ? sourceFileNames[i]:"",
					folderIds.length > 0 ? folderIds[i]:0 , 
					fileNameHeaders.length > 0 ? fileNameHeaders[i]:"", 
					previousFileNames.length > 0 ? previousFileNames[i]:""));
		}
		return values;
	}
	
	
	@Override
	public boolean isSimpleFieldInRequest(PortletRequest request,
			DDMStructure ddmStructure, String fieldName)
			throws PortalException, SystemException {
		
		UploadRequest uploadRequest = PortalUtil.getUploadPortletRequest(request);
		
		File file = uploadRequest.getFile(fieldName);

		return (file != null && file.length() > 0);
	}

	@Override
	public boolean isMultipleFieldInRequest(PortletRequest request,
			DDMStructure ddmStructure, String fieldName)
			throws PortalException, SystemException {
		
		UploadRequest uploadRequest = PortalUtil.getUploadPortletRequest(request);
		
		File[] files = uploadRequest.getFiles(fieldName);

		return files != null && files.length > 0;
	}

	public Object getValueDLFileEntry(PortletRequest request, Object value, Object currentValue){
		
		Object objValue = null;
		if(value instanceof List){
			
			List<DocumentValue> documentValues =  (List<DocumentValue>)value;
			
			String[] currentValues =  null;
			if(currentValue != null && currentValue.getClass().isArray()){
				currentValues = (String[])currentValue;
			}else if (currentValue != null && !GetterUtil.get(currentValue, "").isEmpty()){
				currentValues = new String[]{(String)currentValue};
			}
			
			
			List<String> values = new ArrayList<String>();
			
			for(int i = 0; i < documentValues.size(); i++){
				
				DocumentValue documentValue = documentValues.get(i);
				String documentCurrentValue = currentValues != null && currentValues.length > i ? currentValues[i] : null;
				
				if(documentValue.getFile() != null){
					values.add(getValueDLFileEntry(request, documentValue, documentCurrentValue));
				}else if(currentValues != null){
					values.add(currentValues[i]);
				}
			}			
			objValue = values;
			
		}else{
			DocumentValue documentValue = (DocumentValue)value;
			objValue = currentValue;
			if(documentValue.getFile() != null && documentValue.getFile().length() > 0){
				objValue = getValueDLFileEntry(request, (DocumentValue)value, (String)currentValue);
			}
		}
		return objValue;
	}
	
	 
	
	private  String getValueDLFileEntry(PortletRequest request, DocumentValue documentValue, String currentValue){
		
		String value ="";		
		
		String mimeType = MimeTypesUtil.getContentType(documentValue.getFile());
		String title = documentValue.getFileNameHeader() + "_" + UUID.randomUUID() ;
		String changeLog = "";
		String description = documentValue.getSourceFileName();
		long size = documentValue.getFile().length();
		Long folderId = documentValue.getFolderId();
		String sourceFileName = documentValue.getSourceFileName();
		File file = documentValue.getFile();
		
		try {		
			
			ServiceContext serviceContext =
					ServiceContextFactory.getInstance(DLFileEntry.class.getName(), request);
							
			ThemeDisplay themeDisplay= (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
			
			long groupId = themeDisplay.getScopeGroupId();			
			long repositoryId = groupId;

			
			FileEntry dlFileEntry;
			if (GetterUtil.get((String)currentValue, "").isEmpty()){
				dlFileEntry = DLAppServiceUtil.addFileEntry(repositoryId, folderId, sourceFileName, mimeType, title, description, changeLog, null, size, serviceContext);
			}else{
				JSONObject jDLOld = JSONFactoryUtil.createJSONObject((String)currentValue);
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
