package es.grupoica.cyted.util;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.User;
import com.liferay.portal.model.UserGroupRole;
import com.liferay.portal.service.UserGroupRoleLocalServiceUtil;

import java.util.ArrayList;
import java.util.List;
public class RolUtil {

	/**
	 * Obtiene los usuarios que pertenecen a un rol determinado.
	 * @param roleId
	 * @param groupId
	 * @return
	 * @throws SystemException
	 * @throws PortalException
	 */
	public List<User> getUsersByRole(long roleId, long groupId) throws PortalException, SystemException {

		List<User> usuariosRol = new ArrayList<User>();

		//usuariosRol = UserLocalServiceUtil.getRoleUsers(roleId);
		List<UserGroupRole> userGroupRoles = UserGroupRoleLocalServiceUtil.getUserGroupRolesByGroupAndRole(groupId, roleId);

		for (UserGroupRole userGroupRole : userGroupRoles) {
			User oneUser = userGroupRole.getUser();
			usuariosRol.add(oneUser);
		}

		return usuariosRol;
	}

}