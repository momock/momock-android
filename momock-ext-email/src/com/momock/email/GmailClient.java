/*******************************************************************************
 * Copyright 2013 momock.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.momock.email;

import java.util.Date;
import java.util.Properties;
import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class GmailClient extends javax.mail.Authenticator {
	private int retry = 3;
	private String username;
	private String password;

	private String[] receivers;
	private String sender;

	private String port;
	private String sport;

	private String host;

	private String subject;
	private String body;

	private boolean auth;

	private boolean debuggable;

	private Multipart multipart;

	public GmailClient(String user, String pass) {
		host = "smtp.gmail.com";
		port = "465";
		sport = "465";

		username = user;
		password = pass;
		sender = "";
		subject = "";
		body = "";

		debuggable = false;
		auth = true;

		multipart = new MimeMultipart();

		MailcapCommandMap mc = (MailcapCommandMap) CommandMap
				.getDefaultCommandMap();
		mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
		mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
		mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
		mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
		mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
		CommandMap.setDefaultCommandMap(mc);
	}

	public boolean send() throws Exception {
		Properties props = _setProperties();

		if (!username.equals("") && !password.equals("") && receivers.length > 0
				&& !sender.equals("") && !subject.equals("")
				&& !body.equals("")) {
			Session session = Session.getInstance(props, this);

			MimeMessage msg = new MimeMessage(session);

			msg.setFrom(new InternetAddress(sender));

			InternetAddress[] addressTo = new InternetAddress[receivers.length];
			for (int i = 0; i < receivers.length; i++) {
				addressTo[i] = new InternetAddress(receivers[i]);
			}
			msg.setRecipients(MimeMessage.RecipientType.TO, addressTo);

			msg.setSubject(subject);
			msg.setSentDate(new Date());

			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText(body);
			multipart.addBodyPart(messageBodyPart);

			msg.setContent(multipart);

			Transport.send(msg);

			return true;
		} else {
			return false;
		}
	}

	public void addAttachment(String filename) throws Exception {
		BodyPart messageBodyPart = new MimeBodyPart();
		DataSource source = new FileDataSource(filename);
		messageBodyPart.setDataHandler(new DataHandler(source));
		messageBodyPart.setFileName(filename);

		multipart.addBodyPart(messageBodyPart);
	}

	@Override
	public PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(username, password);
	}

	private Properties _setProperties() {
		Properties props = new Properties();

		props.put("mail.smtp.host", host);

		if (debuggable) {
			props.put("mail.debug", "true");
		}

		if (auth) {
			props.put("mail.smtp.auth", "true");
		}

		props.put("mail.smtp.port", port);
		props.put("mail.smtp.socketFactory.port", sport);
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");

		return props;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String _body) {
		this.body = _body;
	}

	public void setReceivers(String[] receivers) {
		this.receivers = receivers;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public int getRetry() {
		return retry;
	}

	public void setRetry(int retry) {
		this.retry = retry;
	}

}
