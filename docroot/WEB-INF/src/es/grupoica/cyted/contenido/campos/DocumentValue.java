package es.grupoica.cyted.contenido.campos;

import java.io.File;
import java.io.Serializable;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;

public class DocumentValue implements Serializable{

	/**
	 * 
	 */
	private final File file;
	private final String sourceFileName;
	private final long folderId;
	private final String fileNameHeader; 
	private final String previousFileName;
	
	private static final long serialVersionUID = 3074928643764559950L;

	public DocumentValue(File file, String sourceFileName, long folderId,
			String fileNameHeader, String previousFileName) {
		super();
		this.file = file;
		this.sourceFileName = sourceFileName;
		this.folderId = folderId;
		this.fileNameHeader = fileNameHeader;
		this.previousFileName = previousFileName;
	}

	public File getFile() {
		return file;
	}

	public String getSourceFileName() {
		return sourceFileName;
	}

	public long getFolderId() {
		return folderId;
	}

	public String getFileNameHeader() {
		return fileNameHeader;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	

	public String getPreviousFileName() {
		return previousFileName;
	}

	@Override
	public String toString() {
		JSONObject obj = JSONFactoryUtil.createJSONObject();
		obj.put("fileName", 
				this.sourceFileName != null  && !this.sourceFileName.isEmpty() ? this.sourceFileName : this.previousFileName);
		return obj.toString();
	}
	

}
