/**
 * 
 */
package com.convergys.wmsfetch.net.email;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;

/**
 * @author Andrew Och
 * @version %I%, %G%
 * @since 1.0
 * 
 */
public class MailSender {
	private static transient final Logger logger = Logger
			.getLogger(MailSender.class);

	public static final String DEFAULT_MAIL_SERVER = "mailhost.oz.convergys.com";

	public static final String HEADER_TYPE = "text/html";

	/**
	 * Just an example
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		MailSender mailSender = new MailSender();
		mailSender.setDebug(true);
		mailSender.setUsername("andrew.och@convergys.com");
		mailSender.setPassword("secret");
		mailSender.setAttachmentFileName("D:" + File.separator
				+ "AWCC.0.0.2.126.txt");
		mailSender.setMail_fromPersonalName("Andrew_Och");
		mailSender.setMail_from("andrew.och@convergys.com");
		mailSender.setMail_to("andrew.och@convergys.com");
		mailSender.setMail_subject("Release Notes");
		mailSender.setMail_body("Please Check the Release notes");
		mailSender.send();
	}

	/**
	 * @return the mail_body
	 */
	public String getMail_body() {
		return mail_body;
	}

	/**
	 * @param mailBody
	 *            the mail_body to set
	 */
	public void setMail_body(String mailBody) {
		mail_body = mailBody;
	}

	private boolean debug = false;

	private String mail_to;

	private String mail_from;

	private String mail_fromPersonalName;

	private String mail_subject;

	private String mail_body;

	private String attachmentFileName;

	private String username;

	private String password;

	// smtp mailServer URL
	private String mailServerURL = DEFAULT_MAIL_SERVER;

	private Properties properties;

	public MailSender() {
		properties = new Properties();
		properties.setProperty("mail.transport.protocol", "smtp");
		properties.setProperty("mail.host", mailServerURL);
		properties.setProperty("mail.smtp.auth", "true");
	}

	/**
	 * @return the attachmentFileName
	 */
	public String getAttachmentFileName() {
		return attachmentFileName;
	}

	/**
	 * @return the mail_from
	 */
	public String getMail_from() {
		return mail_from;
	}

	/**
	 * @return the mail_fromPersonalName
	 */
	public String getMail_fromPersonalName() {
		return mail_fromPersonalName;
	}

	/**
	 * @return the mail_subject
	 */
	public String getMail_subject() {
		return mail_subject;
	}

	/**
	 * @return the mail_to
	 */
	public String getMail_to() {
		return mail_to;
	}

	/**
	 * @return the mailServerURL
	 */
	public String getMailServerURL() {
		return mailServerURL;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @return the properties
	 */
	public Properties getProperties() {
		return properties;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return the debug
	 */
	public boolean isDebug() {
		return debug;
	}

	public void send() throws Exception {

		// New passwordAuthenticator
		Authenticator auth = new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		};

		Session mailSession = Session.getDefaultInstance(properties, auth);
		mailSession.setDebug(debug);
		Transport transport = mailSession.getTransport();

		MimeMessage message = new MimeMessage(mailSession);
		message.setSubject(mail_subject);

		MimeBodyPart textPart = new MimeBodyPart();
		textPart.setContent(mail_body, HEADER_TYPE);

		MimeBodyPart attachFilePart = new MimeBodyPart();
		FileDataSource fileDataSource = new FileDataSource(attachmentFileName);
		attachFilePart.setDataHandler(new DataHandler(fileDataSource));
		attachFilePart.setFileName(fileDataSource.getName());

		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(textPart);
		multipart.addBodyPart(attachFilePart);

		message.setContent(multipart);

		message.setSentDate(new Date());
		Address address = new InternetAddress(mail_from, mail_fromPersonalName);
		message.setFrom(address);
		Address toAddress = new InternetAddress(mail_to);
		message.addRecipient(Message.RecipientType.TO, toAddress);

		transport.connect();
		transport.sendMessage(message, message
				.getRecipients(Message.RecipientType.TO));
		transport.close();

		String infoMsg = String.format(
				"Email sent from: [%s] to: [%s] subject: [%s]", new Object[] {
						mail_from, mail_to, mail_subject });
		logger.info(infoMsg);
	}

	/**
	 * @param attachmentFileName
	 *            the attachmentFileName to set
	 */
	public void setAttachmentFileName(String attachmentFileName) {
		this.attachmentFileName = attachmentFileName;
	}

	/**
	 * @param debug
	 *            the debug to set
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	/**
	 * @param mailFrom
	 *            the mail_from to set
	 */
	public void setMail_from(String mailFrom) {
		mail_from = mailFrom;
	}

	/**
	 * @param mailFromPersonalName
	 *            the mail_fromPersonalName to set
	 */
	public void setMail_fromPersonalName(String mailFromPersonalName) {
		mail_fromPersonalName = mailFromPersonalName;
	}

	/**
	 * @param mailSubject
	 *            the mail_subject to set
	 */
	public void setMail_subject(String mailSubject) {
		mail_subject = mailSubject;
	}

	/**
	 * @param mailTo
	 *            the mail_to to set
	 */
	public void setMail_to(String mailTo) {
		mail_to = mailTo;
	}

	/**
	 * @param mailServerURL
	 *            the mailServerURL to set
	 */
	public void setMailServerURL(String mailServerURL) {
		this.mailServerURL = mailServerURL;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
		properties.setProperty("mail.password", password);
	}

	/**
	 * @param properties
	 *            the properties to set
	 */
	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
		properties.setProperty("mail.user", username);
	}
}
