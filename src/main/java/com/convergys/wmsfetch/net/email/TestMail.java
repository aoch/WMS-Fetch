package com.convergys.wmsfetch.net.email;

import java.util.Date;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class TestMail {

	private String host = "mailhost.oz.convergys.com"; // smtp HOST URL

	private String username = "andrew.och@convergys.com"; // change it

	private String password = "Pass3333"; // change it

	private String mail_head_name = "this is head of this mail";

	private String mail_head_value = "this is head of this mail";

	private String mail_to = "leo.shao@convergys.com"; // change it

	private String mail_from = "leo.shao@convergys.com"; // change it

	private String mail_subject = "this is the subject of this test mail";

	private String mail_body = "this is the mail_body of this test mail";

	private String personalName = "my mail";

	public TestMail() {
	}

	public void send() throws Exception {
		try {
			Properties props = new Properties();
			Authenticator auth = new Email_Autherticator();
			props.put("mail.smtp.host", host);
			props.put("mail.smtp.auth", "true");
			Session session = Session.getDefaultInstance(props, auth);

			MimeMessage message = new MimeMessage(session);

			message.setSubject(mail_subject);
			message.setText(mail_body);
			message.setHeader(mail_head_name, mail_head_value);
			message.setSentDate(new Date());
			Address address = new InternetAddress(mail_from, personalName);
			message.setFrom(address);
			Address toAddress = new InternetAddress(mail_to);
			message.addRecipient(Message.RecipientType.TO, toAddress);
			Transport.send(message);
			System.out.println("send ok!");
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception(ex.getMessage());
		}
	}

	public class Email_Autherticator extends Authenticator {
		public Email_Autherticator() {
			super();
		}

		public Email_Autherticator(String user, String pwd) {
			super();
			username = user;
			password = pwd;
		}

		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(username, password);
		}
	}

	public static void main(String[] args) {
		TestMail sendmail = new TestMail();
		try {
			sendmail.send();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
