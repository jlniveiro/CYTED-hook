package es.grupoica.cyted.contenido;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidationErrors {
	
	private Map<String, Object> errors; 
	
	
	public ValidationErrors() {
		super();
		this.errors = new HashMap<String,Object>();
	}

	@SuppressWarnings("unchecked")
	public void addErrorMessage(String fieldName, String message){		
		
		Object objError = errors.get(fieldName);
		if(objError == null){
			objError = new ArrayList<String>();
			errors.put(fieldName, objError);
		}
		((List<String>)objError).add(message);
	}
	
	@SuppressWarnings("unchecked")
	public void addErrorMessage(String fieldName, String message, int index){		

		Object objError = errors.get(fieldName);
		if(objError == null){
			objError = new HashMap<Integer, List<String>>();
			errors.put(fieldName, objError);
		}
		
		List<String> errores = ((Map<Integer, List<String>>)objError).get(index);
		if(errores == null){
			errores = new ArrayList<String>();
			((Map<Integer, List<String>>)objError).put(index, errores);
		}
		((Map<Integer, List<String>>)objError).get(index).add(message);
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getErrorMessages(String fieldName){
		Object obj = errors.get(fieldName);
		if(obj == null){
			return null;
		}		
		return ((List<String>)obj);
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getErrorMessages(String fieldName, int index){
		Object obj = errors.get(fieldName);
		if(obj == null){
			return null;
		}				
		return ((Map<Integer, List<String>>)obj).get(index);
	}	 
	
	public boolean hasErrors(){
		return !this.errors.isEmpty();
	}
	
	@SuppressWarnings("unchecked")
	public boolean hasErrors(String fieldName){
		
		boolean hasErrors = false;
		Object objErrors = this.errors.get(fieldName);
		if(objErrors == null){
			return hasErrors;
		}
		
		if(objErrors instanceof List){
			hasErrors = !((List<String>)objErrors).isEmpty();
		}else if(objErrors instanceof Map){
			hasErrors = !((Map<Integer, List<String>>)objErrors).isEmpty();
		}
		
		return hasErrors;
	}
	
	@SuppressWarnings("unchecked")
	public boolean hasErrors(String fieldName, int index){
		return this.errors.get(fieldName)!=null && ((Map<Integer, List<String>>)this.errors.get(fieldName)) != null && 
				!((Map<Integer, List<String>>)this.errors.get(fieldName)).get(index).isEmpty();
	}
}
