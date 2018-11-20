package es.grupoica.cyted.contenido.campos;


public class ValueGetterFactory {
	
	public static final String DDM_TYPE_TEXT="text";
	public static final String DDM_TYPE_TEXTAREA="textarea";
	public static final String DDM_TYPE_CHECKBOX="checkbox";
	public static final String DDM_TYPE_RADIO="radio";
	public static final String DDM_TYPE_SELECT="select";
	public static final String DDM_TYPE_DATE="ddm-date";
	public static final String DDM_TYPE_DOCUMENT_LIBRARY="ddm-documentlibrary";
	public static final String DDM_TYPE_DECIMAL="ddm-decimal";
	public static final String DDM_TYPE_INTEGER="ddm-integer";
	public static final String DDM_TYPE_SEPARATOR="ddm-separator";
	
	
	
	public static ValueGetter getValueGetter(String fieldType){
		
		ValueGetter getter = null;
		switch(fieldType){
		case DDM_TYPE_TEXT:
		case DDM_TYPE_TEXTAREA:
		case DDM_TYPE_DECIMAL:
		case DDM_TYPE_INTEGER:
			getter = new TextFieldGetter(fieldType);
			break;
		//case DDM_TYPE_RADIO:
		//	getter = new RadioGetter();
		//	break;			
		case DDM_TYPE_SELECT:
			getter = new SelectGetter();
			break;
		case DDM_TYPE_CHECKBOX:
			getter = new CheckGetter();
			break;			
		case DDM_TYPE_DATE:			
			getter = new DateGetter("dd/MM/yyyy");
			break;
		case DDM_TYPE_DOCUMENT_LIBRARY:
			getter = new DocumentFileGetterEx();
			break;
		case DDM_TYPE_SEPARATOR:
			getter = new SeparatorGetter();
			break;
		default:
			getter= null;
		}
		return getter;
	}

}
