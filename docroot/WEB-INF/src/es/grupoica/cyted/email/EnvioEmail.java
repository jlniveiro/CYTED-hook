package es.grupoica.cyted.email;

import com.liferay.mail.service.MailServiceUtil;
import com.liferay.portal.kernel.mail.MailMessage;

import es.grupoica.cyted.bean.EmailBean;

import java.io.UnsupportedEncodingException;

import java.util.ArrayList;

import javax.mail.internet.InternetAddress;
public class EnvioEmail {

	public void enviarCorreo(ArrayList<EmailBean> destinatariosTo, ArrayList<EmailBean> destinatariosCC, ArrayList<EmailBean> destinatariosCCO,
			String emailSubject, String contenido) {
		String emailBody = "";
		//String emailSubject = "Bienvenido a CYTED. Datos de acceso a la plataforma";
		boolean destinatarios = false;

		try {
			InternetAddress from = new InternetAddress("joseluis.niveiro@grupoica.com","Administrador CYTED");
			InternetAddress[] to = null; //Objetos InternetAddress con el email de los destinatarios
			InternetAddress[] cc = null; //Objetos InternetAddress con el email de los destinatarios en copia
			InternetAddress[] cco = null; //Objetos InternetAddress con el email de los destinatarios en copia oculta

			//Creamos los directorios de usuarios destinatarios, en copia y ocultos
			//Destinatarios

			if (destinatariosTo != null && !destinatariosTo.isEmpty()) {
				//a�adimos los destinatarios To
				to = new InternetAddress[destinatariosTo.size()];
				int countTo = 0;

				for (EmailBean destinatario : destinatariosTo) {
					to[countTo] = new InternetAddress(destinatario.getEmail(), destinatario.getNombre());
					countTo++;
				}
			}

			//En Copia

			if (destinatariosCC != null && !destinatariosCC.isEmpty()) {
				//a�adimos los destinatarios en copia
				cc = new InternetAddress[destinatariosCC.size()];
				int countCC = 0;

				for (EmailBean destinatario : destinatariosCC) {
					cc[countCC] = new InternetAddress(destinatario.getEmail(), destinatario.getNombre());
					countCC++;
				}
			}

			//En Copia oculta

			if (destinatariosCCO != null && !destinatariosCCO.isEmpty()) {
				//a�adimos los destinatarios en copia
				cco = new InternetAddress[destinatariosCCO.size()];
				int countCCO = 0;

				for (EmailBean destinatario : destinatariosCCO) {
					cco[countCCO] = new InternetAddress(destinatario.getEmail(), destinatario.getNombre());
					countCCO++;
				}
			}

			MailMessage message = new MailMessage();
			message.setFrom(from);

			if ((to != null) && (to.length > 0)) {
				message.setTo(to);
				destinatarios = true;
			}

			if ((cc != null) && (cc.length > 0)) {
			message.setCC(cc);
			destinatarios = true;
			}

			if ((cco != null) && (cco.length > 0)) {
			message.setBCC(cco);
			destinatarios = true;
			}

			if (destinatarios) {
			message.setSubject(emailSubject);
			message.setBody(contenido);
			message.setHTMLFormat(true);
			    //message.addFileAttachment(file);
			    MailServiceUtil.sendEmail(message);
			}
		}
		catch (UnsupportedEncodingException unex) {
			unex.printStackTrace();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}