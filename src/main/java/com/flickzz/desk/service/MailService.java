package com.flickzz.desk.service;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class MailService {

	private static final Logger log = LoggerFactory.getLogger(MailService.class);

	private final JavaMailSender mailSender;
	private final String fromAddress;

	@Value("${application.baseUrl}")
	private String baseUrl;

	public MailService(JavaMailSender mailSender,
			@Value("${app.mail.from:noreply@flickzzdesk.com}") String fromAddress) {
		this.mailSender = mailSender;
		this.fromAddress = fromAddress;
	}

	public void sendTemporaryPasswordEmail(String toEmail, String firstName, String temporaryPassword) {
		if (!StringUtils.hasText(toEmail)) {
			throw new IllegalArgumentException("Destination email address must be provided");
		}

		String displayName = StringUtils.hasText(firstName) ? firstName : "User";
		String subject = "Your Flickzz Desk temporary password";
		String body = String.format("Hello %s,%n%nYour Flickzz Desk account has been created.%n%n"
				+ "Temporary password: %s%n%n"
				+ "Please sign in using this temporary password and update your password after first login.%n%n"
				+ "If you did not request this account, please contact support.%n%n" + "Thank you,%nFlickzz Desk Team",
				displayName, temporaryPassword);

		sendSimpleEmail(toEmail, subject, body);
	}

	public void sendEnquiryLink(String toEmail, String userName, String token) {
		if (!StringUtils.hasText(toEmail)) {
			throw new IllegalArgumentException("Destination email address must be provided");
		}

		String subject = "Your Flickzz Desk enquiry link";
		String enquiryLink = String.format("%s/enquiry/verify?username=%s&token=%s", this.baseUrl, userName, token);
		String body = String.format("Thank you for your enquiry on Flickzz Desk.<br><br>"
				+ "Your User Name for FlickzzDesk login is: <b>%s</b><br><br>"
				+ "Please click the link below to verify:<br><br>"
				+ "<a href='%s' style='display:inline-block;padding:7px 25px;"
				+ "font-size:16px;color:#F4F4F4;background-color:#00246B;"
				+ "text-decoration:none;border-radius:5px;'>Verify</a><br><br>"
				+ "Note: This link is valid for 3 hours only.<br><br>"
				+ "If you did not expect this email, please ignore it.<br><br>" + "Thank you,<br>Flickzz Desk Team",
				userName, enquiryLink);

		sendHtmlEmail(toEmail, subject, body);
	}

	private void sendHtmlEmail(String toEmail, String subject, String body) {
		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
			helper.setFrom(fromAddress);
			helper.setTo(toEmail);
			helper.setSubject(subject);
			helper.setText(body, true); // true = HTML

			mailSender.send(mimeMessage);
		} catch (MessagingException e) {
			log.error("Error while sending HTML email", e);
		}

	}

	private void sendSimpleEmail(String toEmail, String subject, String text) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom(fromAddress);
			message.setTo(toEmail);
			message.setSubject(subject);
			message.setText(text);
			mailSender.send(message);
			log.info("Mail Sent Successfully");
		} catch (Exception e) {

			log.error("Error while sending mail");
		}
	}

	// Send mail with attachment
	public String sendMailWithAttachment(String toEmail, String subject, String text) {

		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper;

		try {

			helper = new MimeMessageHelper(mimeMessage, true);

			helper.setFrom(fromAddress);
			helper.setTo(toEmail);
			helper.setText(text);
			helper.setSubject(subject);

			FileSystemResource file = new FileSystemResource(new File(""));

			helper.addAttachment(file.getFilename(), file);

			mailSender.send(mimeMessage);

			return "Mail Sent Successfully";

		} catch (MessagingException e) {

			return "Error while sending mail";
		}
	}
}
