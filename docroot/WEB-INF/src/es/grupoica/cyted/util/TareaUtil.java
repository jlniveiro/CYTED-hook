package es.grupoica.cyted.util;

import com.liferay.counter.service.CounterLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserServiceUtil;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.tasks.model.TasksEntry;
import com.liferay.tasks.service.TasksEntryLocalServiceUtil;

import es.grupoica.cyted.bbdd.model.ICATarea;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class TareaUtil {

	public ICATarea componerTarea(JournalArticle journalArticle, ICATarea tareaBase) {

		ICATarea tareaAEnviar = tareaBase;

		//En primer lugar obtenemos la lista de campos a cumplimentar en la tarea
		List<String> campos = getListaCamposTarea(tareaBase.getDescripcion());
		//Reemplazamos los valores de las variables sobre el contenido actual
		String contenidoFormateado = tareaBase.getDescripcion();

		for (String campo : campos) {
			String valorCampo = journalUtil.getRootParseValue(campo, journalArticle, LocaleUtil.getDefault().toString());
			if (valorCampo != null){ 
				contenidoFormateado = contenidoFormateado.replace("${" + campo + "}", valorCampo);
			}
		}

		//El valor formateado lo incorporamos al anuncio a enviar
		tareaAEnviar.setDescripcion(contenidoFormateado);

		return tareaAEnviar;
	}

	public void enviarTarea(JournalArticle journalArticle, ICATarea tareaEnviar, User user) throws PortalException, SystemException {

		//dia actual
		Calendar calHoy = Calendar.getInstance();
		//Nueva tarea
		long tasksEntryId = CounterLocalServiceUtil.increment();
		//Creador de tareas
		//TasksEntryPersistenceImpl tareaPers = new TasksEntryPersistenceImpl();
		TasksEntry tasksEntry = TasksEntryLocalServiceUtil.createTasksEntry(tasksEntryId);

		//A�adimos los campos necesarios a la tarea
		tasksEntry.setGroupId(journalArticle.getGroupId());
		tasksEntry.setCompanyId(user.getCompanyId());
		tasksEntry.setUserId(UserServiceUtil.getCurrentUser().getUserId());
		tasksEntry.setUserName(UserServiceUtil.getCurrentUser().getFirstName());

		if (tareaEnviar.getPersonaAsignada() == null) {
			tasksEntry.setAssigneeUserId(user.getUserId());
		}
		else {
			tasksEntry.setAssigneeUserId(tareaEnviar.getPersonaAsignada());
		}

		tasksEntry.setCreateDate(calHoy.getTime());
		tasksEntry.setModifiedDate(calHoy.getTime());
		tasksEntry.setTitle(tareaEnviar.getDescripcion());

		if (tareaEnviar.getFechaFinalizacion() != null) {
			tasksEntry.setFinishDate(tareaEnviar.getFechaFinalizacion());
		}

		tasksEntry.setPriority(1);
		tasksEntry.setStatus(1);

		//Creamos la tarea
		TasksEntryLocalServiceUtil.addTasksEntry(tasksEntry);
	}

	protected JournalUtil journalUtil = new JournalUtil();

	private List<String> getListaCamposTarea(String content) {

		List<String> lista = new ArrayList<String>();
		String contenidoPendiente = content;
		int longContenidoOrigen = content.length();
		int actual = 0;

		while (actual < longContenidoOrigen && actual != -1) {
			//Obtenemos la variable a sustituir
			actual = contenidoPendiente.indexOf("${");

			if (actual != -1) {
				//Hay valores. Extraemos el texto hasta la variable encontrada
				String variable = "";
				int fin = contenidoPendiente.indexOf("}");

				if (fin != -1) {
					variable = contenidoPendiente.substring(actual + 2, fin);
					//A�adimos la variable a la lista
					lista.add(variable);
					contenidoPendiente = contenidoPendiente.substring(fin + 1);
				}
			}//fin if (actual != -1)
		}//fin while

		return lista;
	}

	private static final Logger logger = LogManager.getLogger(AnuncioUtil.class.getName());

}