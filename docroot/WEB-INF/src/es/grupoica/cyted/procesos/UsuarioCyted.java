package es.grupoica.cyted.procesos;

import com.liferay.counter.service.CounterLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.servlet.PortalMessages;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.model.ClassName;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.Contact;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.LayoutSet;
import com.liferay.portal.model.ResourcePermission;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ClassNameLocalServiceUtil;
import com.liferay.portal.service.CompanyLocalServiceUtil;
import com.liferay.portal.service.ContactLocalServiceUtil;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.LayoutSetLocalServiceUtil;
import com.liferay.portal.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.service.persistence.UserPersistence;
import com.liferay.portal.service.persistence.UserUtil;
import com.liferay.portlet.asset.model.AssetEntry;
import com.liferay.portlet.asset.service.AssetEntryLocalServiceUtil;
import com.liferay.portlet.journal.model.JournalArticle;


import es.grupoica.cyted.bean.EmailBean;
import es.grupoica.cyted.email.EnvioEmail;
import es.grupoica.cyted.util.Constantes;
import es.grupoica.cyted.util.JournalUtil;
import es.grupoica.cyted.util.PasswordGenerator;
import es.grupoica.cyted.util.UsuarioUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
public class UsuarioCyted {

	/**
	 * Crea un nuevo usuario en el sistema CYTED
	 * @param article
	 * @return
	 * @throws SystemException
	 * @throws PortalException
	 * @throws NumberFormatException
	 */
	public JournalArticle crearUsuario(JournalArticle article, ServiceContext serviceContext) throws NumberFormatException, PortalException, SystemException {

		//Extraemos los datos necesarios para la creaciï¿½n del usuario
		long facebookId = 0;
		String openId = null;
		//EMAIL DE ACCESO
		String emailAddress = JournalUtil.getRootParseValue("email", article, LocaleUtil.getDefault().toString());
		//ROL DEL USUARIO
		String tipoUsuario = JournalUtil.getRootParseValue("tipoUsuario", article, LocaleUtil.getDefault().toString());
		Role role = RoleLocalServiceUtil.getRole(new Long(tipoUsuario));
		
		//Role role = RoleLocalServiceUtil.getRole(article.getCompanyId(), "Organization Administrator");
		User user = null;

		try
		{
			if (role!= null) {

				//long idContact = CounterLocalServiceUtil.increment(Contact.class.getName());
				long idContact = CounterLocalServiceUtil.increment();
				//String greeting="Welcome "+role.getName();
				String greeting ="Welcome " + role.getName();
				//long id = CounterLocalServiceUtil.increment(User.class.getName());
				long id = CounterLocalServiceUtil.increment();
				String screenName ="userCYTED"+id;
				user = UserLocalServiceUtil.createUser(id);
				user.setCompanyId(article.getCompanyId());
				//contraseï¿½a
				String password = PasswordGenerator.getPassword(8) ;

				if (password != null && !password.trim().isEmpty()) {
					user.setPassword(password);
				}
				
				user.setPasswordEncrypted(true);
				//String screenName=organization.getName().toLowerCase()+id;
				user.setScreenName(screenName);
				user.setEmailAddress(emailAddress != null ? emailAddress : "");
				user.setFacebookId(facebookId);
				user.setOpenId(openId);
				user.setGreeting(greeting);
				//user.setFirstName(journalUtil.getParseValue("nombre", article, LocaleUtil.getDefault().toString()));
				user.setFirstName(JournalUtil.getRootParseValue("nombre", article, LocaleUtil.getDefault().toString()));
				//Nombre
				//user.setMiddleName(journalUtil.getParseValue("nombre", article, LocaleUtil.getDefault().toString()));
				user.setLastName(JournalUtil.getRootParseValue("apellidos", article, LocaleUtil.getDefault().toString()));
				user.setJobTitle("Usuario CYTED " + role.getName() + " - " + user.getFirstName());
				user.setCreateDate(Calendar.getInstance().getTime());
				user.setContactId(idContact);
				user.setPasswordReset(true);
				user.setPasswordEncrypted(false);
				user.setPasswordModifiedDate(new Date());
				user.setCreateDate(Calendar.getInstance().getTime());
				user.setModifiedDate(Calendar.getInstance().getTime());
				user.setLanguageId(LanguageUtil.getLanguageId(LocaleUtil.getDefault()));
				//user.setTimeZoneId(themeDisplay.getTimeZone().getDisplayName());
				user.setTimeZoneId(TimeZoneUtil.getDefault().getDisplayName());
				//añadimos los datos adicionales al usuario
				//NACIONALIDAD
				user.setComments("nacionalidad=" + JournalUtil.getRootParseValue("nacionalidad", article, LocaleUtil.getDefault().toString()));
				//Verificamos si es personal de Comité de Área
				//AREA
				//Gestor de Área y Vocal de Área
				if (tipoUsuario.equals(Constantes.ROL_GESTOR_AREA.toString()) || tipoUsuario.equals(Constantes.ROL_VOCAL_AREA.toString())){
					user.setComments(user.getComments() + ",idArea=" + JournalUtil.getRootParseValue("areaTematicaUsuario", article, LocaleUtil.getDefault().toString()));
				}
				
				
				/* NUEVO METODO */
				String fechaNacimiento = JournalUtil.getRootParseValue("fechaNacimiento", article, LocaleUtil.getDefault().toString());
				Calendar calnac = Calendar.getInstance();
				if (fechaNacimiento != null && !fechaNacimiento.isEmpty()){
					try{
						calnac.setTimeInMillis(new Long(fechaNacimiento));
					}
					catch (NumberFormatException nex){
						calnac = null;
					}
				}
				
				//UserLocalServiceUtil.addRoleUsers(role.getRoleId(), userid);
				Role rolePu = RoleLocalServiceUtil.getRole(article.getCompanyId(), "Power User");
				Role roleUs = RoleLocalServiceUtil.getRole(article.getCompanyId(), "User");
				//UserLocalServiceUtil.addRoleUsers(rolePu.getRoleId(), userid);
				long[] roleIds ={role.getRoleId(), rolePu.getRoleId(), roleUs.getRoleId()};
				boolean usuarioOK = true;

				//Comprobamos si existe ya el usuario en base de datos.
				User usuarioRepetido = UserLocalServiceUtil.fetchUserByEmailAddress(article.getCompanyId(), emailAddress);

				if (usuarioRepetido != null) {
				usuarioOK = false;
				throw new PortalException("Ya existe un usuario con el email " + emailAddress);
				}

				//Persistimos el usuario
				UserPersistence userPers = UserUtil.getPersistence();
				userPers.update(user);
				userPers.clearCache();

				if (usuarioOK)
				{
					//Incorporamos al Journal el IdUsuario creado
					JournalUtil.setParseValue("idUsuario", article, Locale.getDefault().toString(), new Long(user.getUserId()).toString());
					//Fecha de alta
					JournalUtil.setParseValue("FechaAlta", article, Locale.getDefault().toString(), String.valueOf(user.getCreateDate().getTime()));
					//usuario creado
					long userid[] = {user.getUserId()};
					//User-Role (le añadimos el rol de USER de Portal)
					UserLocalServiceUtil.addRoleUser(roleUs.getRoleId(), user);
					//User-Group
					UserLocalServiceUtil.addDefaultGroups(user.getUserId());
					//User-Group-Role
					UserGroupRoleLocalServiceUtil.addUserGroupRoles(user.getUserId(), article.getGroupId(), roleIds);

					//ClassName
					ClassName clsNameUser = ClassNameLocalServiceUtil.getClassName("com.liferay.portal.model.User");
					long classNameId = clsNameUser.getClassNameId();

					//Insert Group for a user
					//long gpId = CounterLocalServiceUtil.increment(Group.class.getName());
					long gpId = CounterLocalServiceUtil.increment();
					Group userGrp = GroupLocalServiceUtil.createGroup(gpId);
					userGrp.setClassNameId(classNameId);
					userGrp.setClassPK(userid[0]);
					userGrp.setCompanyId(article.getCompanyId());
					userGrp.setName(String.valueOf(userid[0]));
					userGrp.setFriendlyURL("/"+ user.getScreenName());
					userGrp.setCreatorUserId(user.getUserId());
					userGrp.setActive(true);
					userGrp.setTreePath("/" + gpId + "/");
					GroupLocalServiceUtil.addGroup(userGrp);

					//Insert Contact for a user
					//long idContact = CounterLocalServiceUtil.increment(Contact.class.getName());
					Contact contact = ContactLocalServiceUtil.createContact(idContact);
					contact.setCompanyId(article.getCompanyId());
					contact.setClassNameId(classNameId);
					contact.setCreateDate(Calendar.getInstance().getTime());
					contact.setUserName(screenName);
					contact.setUserId(article.getUserId());
					contact.setClassPK(user.getUserId());
					//Obtenemos el AccountId desde el CompanyId
					Company compania = CompanyLocalServiceUtil.getCompany(article.getCompanyId());
					contact.setAccountId(compania.getAccountId());
					contact.setModifiedDate(Calendar.getInstance().getTime());
					contact.setFirstName("contact-"+contact.getContactId());
					contact.setLastName("contact-"+contact.getContactId());
					contact.setMiddleName("contact-"+contact.getContactId());
					contact.setPrefixId(0);
					contact.setSuffixId(0);
					contact.setJobTitle("Usuario CYTED " + contact.getContactId());
					if (calnac != null){
						contact.setBirthday(calnac.getTime());
					}
					contact.setEmailAddress(user.getEmailAddress());
					ContactLocalServiceUtil.addContact(contact);

					//Create AssetEntry
					//long assetEntryId = CounterLocalServiceUtil.increment(AssetEntry.class.getName());
					long assetEntryId = CounterLocalServiceUtil.increment();
					AssetEntry ae = AssetEntryLocalServiceUtil.createAssetEntry(assetEntryId);
					ae.setCompanyId(article.getCompanyId());
					ae.setClassPK(user.getUserId());
					ae.setGroupId(userGrp.getGroupId());
					ae.setClassNameId(classNameId);
					AssetEntryLocalServiceUtil.addAssetEntry(ae);

					//Insert ResourcePermission for a User
					//long resPermId = CounterLocalServiceUtil.increment(ResourcePermission.class.getName());

					for (int r = 0; r < roleIds.length; r++) {
					long rol = roleIds[r];
						long resPermId = CounterLocalServiceUtil.increment();
						ResourcePermission rpEntry = ResourcePermissionLocalServiceUtil.createResourcePermission(resPermId);
						rpEntry.setCompanyId(article.getCompanyId());
						rpEntry.setName("com.liferay.portal.model.User");
						rpEntry.setRoleId(rol);
						rpEntry.setPrimKey(String.valueOf(user.getUserId()));
						rpEntry.setScope(4);
						rpEntry.setActionIds(31);

						// rpEntry.setPrimaryKey(userid[0]);

						ResourcePermissionLocalServiceUtil.addResourcePermission(rpEntry);
					}

					//Insert Layoutset for public and private
					//long layoutSetIdPub = CounterLocalServiceUtil.increment(LayoutSet.class.getName());
					long layoutSetIdPub = CounterLocalServiceUtil.increment();
					LayoutSet layoutSetPub = LayoutSetLocalServiceUtil.createLayoutSet(layoutSetIdPub);
					layoutSetPub.setCompanyId(article.getCompanyId());
					layoutSetPub.setPrivateLayout(false);
					layoutSetPub.setGroupId(userGrp.getGroupId());
					layoutSetPub.setThemeId("classic");
					try {
						LayoutSetLocalServiceUtil.addLayoutSet(layoutSetPub);
					}catch (SystemException se) {
						se.printStackTrace();
					}

					//long layoutSetIdPriv= CounterLocalServiceUtil.increment(LayoutSet.class.getName());
					long layoutSetIdPriv = CounterLocalServiceUtil.increment();
					LayoutSet layoutSetPriv = LayoutSetLocalServiceUtil.createLayoutSet(layoutSetIdPriv);
					layoutSetPriv.setCompanyId(article.getCompanyId());
					layoutSetPriv.setPrivateLayout(true);
					layoutSetPriv.setThemeId("classic");
					layoutSetPriv.setGroupId(userGrp.getGroupId());
					try {
						LayoutSetLocalServiceUtil.addLayoutSet(layoutSetPriv);
					}catch (SystemException se) {
						se.printStackTrace();
					}

					//Indexamos el usuario en Lucene para que aparezca en el Panel de Control
					try {
						IndexerRegistryUtil.getIndexer(User.class).reindex(user);
					}
					catch (Exception exr) {
						exr.printStackTrace();
					}

					//ENVIAMOS EL CORREO AL USUARIO CON SU CONTRASEï¿½A DE ACCESO
					try {
						this.enviarCorreoNuevoUsuario(user);
					}
					catch (Exception exmail) {
						exmail.printStackTrace();
					}
				}//fin if (usuarioOK)
			}else {
				return article;
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			//if (ex.toString().contains(Ctes.SQL_CONSTRAINT)

			if (ex.toString().contains("ConstraintViolationException")) {
				SessionMessages.add(serviceContext.getRequest(), "Ya existe un usuario con el email " + emailAddress);
				PortalMessages.add(serviceContext.getRequest(), PortalMessages.KEY_MESSAGE, "Ya existe un usuario con el email " + emailAddress);
				throw new PortalException("Ya existe un usuario con el email " + emailAddress);
			}
		}

		return article;
	}
	
	
	
	/**
	 * Modifica los datos de un usuario existente en CYTED
	 * @param article
	 * @return
	 * @throws SystemException
	 * @throws PortalException
	 * @throws NumberFormatException
	 */
	public JournalArticle modificarUsuario(JournalArticle article, ServiceContext serviceContext) throws NumberFormatException, PortalException, SystemException {

		//Extraemos los datos necesarios para la creacion del usuario
		//EMAIL DE ACCESO
		String emailAddress = JournalUtil.getRootParseValue("email", article, LocaleUtil.getDefault().toString());
		//ROL DEL USUARIO
		String tipoUsuario = JournalUtil.getRootParseValue("tipoUsuario", article, LocaleUtil.getDefault().toString());
		Role role = RoleLocalServiceUtil.getRole(new Long(tipoUsuario));
		
		//Role role = RoleLocalServiceUtil.getRole(article.getCompanyId(), "Organization Administrator");
		User user = null;

		try
		{
			if (role!= null) {

				UsuarioUtil util = new UsuarioUtil();
				String idUsuario = JournalUtil.getRootParseValue("idUsuario", article, LocaleUtil.getDefault().toString());
				user = util.getUsuario(Long.valueOf(idUsuario));
				//Actualizamos valores en función de los cambios rcibidos del Journal
				String greeting ="Welcome " + role.getName();
				user.setGreeting(greeting);
				user.setFirstName(JournalUtil.getRootParseValue("nombre", article, LocaleUtil.getDefault().toString()));
				//Nombre
				user.setLastName(JournalUtil.getRootParseValue("apellidos", article, LocaleUtil.getDefault().toString()));
				user.setJobTitle("Usuario CYTED " + role.getName() + " - " + user.getFirstName());
				user.setModifiedDate(Calendar.getInstance().getTime());
				String fechaNacimiento = JournalUtil.getRootParseValue("fechaNacimiento", article, LocaleUtil.getDefault().toString());
				Calendar calnac = Calendar.getInstance();
				calnac.setTimeInMillis(new Long(fechaNacimiento));
				
				//añadimos los datos adicionales al usuario
				//NACIONALIDAD
				user.setComments("nacionalidad=" + JournalUtil.getRootParseValue("nacionalidad", article, LocaleUtil.getDefault().toString()));
				//Verificamos si es personal de Comité de Área
				//AREA
				//Gestor de Área y Vocal de Área
				if (tipoUsuario.equals("26132") || tipoUsuario.equals("40006")){
					user.setComments(user.getComments() + ",idArea=" + JournalUtil.getRootParseValue("areaTematicaUsuario", article, LocaleUtil.getDefault().toString()));
				}
				
				//Persistimos el usuario
				UserPersistence userPers = UserUtil.getPersistence();
				userPers.update(user);
				userPers.clearCache();
				
				//Actualizamos los Roles del usuario
				Role rolePu = RoleLocalServiceUtil.getRole(article.getCompanyId(), "Power User");
				Role roleUs = RoleLocalServiceUtil.getRole(article.getCompanyId(), "User");
				//UserLocalServiceUtil.addRoleUsers(rolePu.getRoleId(), userid);
				long[] roleIds ={role.getRoleId(), rolePu.getRoleId(), roleUs.getRoleId()};
				//User-Group-Role
				//Eliminamos los roles que ya tenía
				UserGroupRoleLocalServiceUtil.deleteUserGroupRolesByUserId(user.getUserId());
				//Actualizamos la lista con los actuales
				UserGroupRoleLocalServiceUtil.addUserGroupRoles(user.getUserId(), article.getGroupId(), roleIds);
				
				/** Actualizamos los permisos **/
				
				//Obtenemos los actuales
				List<ResourcePermission> permisosActuales = ResourcePermissionLocalServiceUtil.getResourcePermissions(article.getCompanyId(), 
						"com.liferay.portal.model.User", 4, String.valueOf(user.getUserId()));
				//Eliminamos los permisos actuales
				for (ResourcePermission permiso : permisosActuales){
					ResourcePermissionLocalServiceUtil.deleteResourcePermission(permiso);
					IndexerRegistryUtil.getIndexer(User.class).reindex(user);
				}

				//Actualizamos los permisos del usuario
				for (int r = 0; r < roleIds.length; r++) {
					long rol = roleIds[r];
					long resPermId = CounterLocalServiceUtil.increment();
					ResourcePermission rpEntry = ResourcePermissionLocalServiceUtil.createResourcePermission(resPermId);
					rpEntry.setCompanyId(article.getCompanyId());
					rpEntry.setName("com.liferay.portal.model.User");
					rpEntry.setRoleId(rol);
					rpEntry.setPrimKey(String.valueOf(user.getUserId()));
					rpEntry.setScope(4);
					rpEntry.setActionIds(31);
					ResourcePermissionLocalServiceUtil.addResourcePermission(rpEntry);
				}

				//Indexamos el usuario en Lucene para que aparezca en el Panel de Control
				try {
					IndexerRegistryUtil.getIndexer(User.class).reindex(user);
				}
				catch (Exception exr) {
					exr.printStackTrace();
				}
			}
			else {
				throw new Exception("Rol de usuario inexistente");
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			//if (ex.toString().contains(Ctes.SQL_CONSTRAINT)

			if (ex.toString().contains("ConstraintViolationException")) {
				SessionMessages.add(serviceContext.getRequest(), "Ya existe un usuario con el email " + emailAddress);
				PortalMessages.add(serviceContext.getRequest(), PortalMessages.KEY_MESSAGE, "Ya existe un usuario con el email " + emailAddress);
				throw new PortalException("Ya existe un usuario con el email " + emailAddress);
			}
			else if (ex.toString().contains("Rol de usuario inexistente")) {
				SessionMessages.add(serviceContext.getRequest(), "El usuario no tiene ROL asignado");
				PortalMessages.add(serviceContext.getRequest(), PortalMessages.KEY_MESSAGE, "El usuario no tiene ROL asignado");
				throw new PortalException("El usuario no tiene ROL asignado");
			}
		}

		return article;
	}


	protected JournalUtil journalUtil = new JournalUtil();

	private String componerContenidoCorreo(User usuario) {
		StringBuffer contenido = new StringBuffer("");

		//
		contenido.append("<p>Estimado " + usuario.getFirstName() + " " + usuario.getLastName() + "</p>")
		.append("<p>Su contraseña de acceso a CYTED es <b>" + usuario.getPassword() + "</b></p>")
		//.append("<p>Puede acceder a CYTED mediente el enlace <a href='192.168.119.186:8080/web/cyted'>Acceso CYTED </a></p>");
		.append(" <p>Un saludo.</p>");

		return contenido.toString();
	}

	private void enviarCorreoNuevoUsuario(User usuario) throws Exception {

		EnvioEmail envio = new EnvioEmail();
		ArrayList<EmailBean> destinatarios = new ArrayList<EmailBean>();
		//Destinatario
		EmailBean destinatario = new EmailBean();
		destinatario.setEmail(usuario.getEmailAddress());
		//destinatario.setEmail("joseluis.niveiro@gmail.com");
		destinatario.setNombre(usuario.getFirstName() + " " + usuario.getLastName());
		destinatarios.add(destinatario);
		//Asunto
		String emailSubject = "Bienvenido a CYTED. Datos de acceso a la plataforma";

		//enviamos
		envio.enviarCorreo(destinatarios, null, null, emailSubject, componerContenidoCorreo(usuario));
	}

}