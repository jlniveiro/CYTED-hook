package es.grupoica.cyted.bbdd.model;

public class VwUsuariosCyted {

	public String getApellidos() {
		return apellidos;
	}

	public String getEmail() {
		return Email;
	}

	public Long getEntryId() {
		return entryId;
	}

	public Long getIdUsuario() {
		return idUsuario;
	}

	public String getInstitucion() {
		return institucion;
	}

	public String getNacionalidad() {
		return nacionalidad;
	}

	public String getNombre() {
		return nombre;
	}

	public String getNombreRol() {
		return nombreRol;
	}

	public Long getTipoUsuario() {
		return tipoUsuario;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public void setEmail(String email) {
		Email = email;
	}

	public void setEntryId(Long entryId) {
		this.entryId = entryId;
	}

	public void setIdUsuario(Long idUsuario) {
		this.idUsuario = idUsuario;
	}

	public void setInstitucion(String institucion) {
		this.institucion = institucion;
	}

	public void setNacionalidad(String nacionalidad) {
		this.nacionalidad = nacionalidad;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public void setNombreRol(String nombreRol) {
		this.nombreRol = nombreRol;
	}

	public void setTipoUsuario(Long tipoUsuario) {
		this.tipoUsuario = tipoUsuario;
	}

	private String apellidos;
	private String Email;
	private Long entryId;
	private Long idUsuario;
	private String institucion;
	private String nacionalidad;
	private String nombre;
	private String nombreRol;
	private Long tipoUsuario;

}