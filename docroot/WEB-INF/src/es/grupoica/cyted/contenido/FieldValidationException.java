package es.grupoica.cyted.contenido;

import java.util.Map;


public class FieldValidationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final ValidationErrors errors;
	private final Map<String,Object> requestParams;

	public FieldValidationException(ValidationErrors errors, Map<String,Object> requestParams) {
		super();
		this.errors = errors;
		this.requestParams = requestParams;
	}

	public ValidationErrors getErrors() {
		return errors;
	}

	public Map<String, Object> getRequestParams() {
		return requestParams;
	}
	
	
	
	
}
