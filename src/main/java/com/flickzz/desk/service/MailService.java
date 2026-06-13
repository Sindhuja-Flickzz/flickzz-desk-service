package com.flickzz.desk.service;

import static com.flickzz.desk.config.FlickzzDeskUtility.*;

import java.io.*;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.core.io.*;
import org.springframework.mail.*;
import org.springframework.mail.javamail.*;
import org.springframework.stereotype.Service;
import org.springframework.util.*;

import jakarta.mail.*;
import jakarta.mail.internet.*;

@Service
public class MailService {

	private static final Logger log = LoggerFactory.getLogger(MailService.class);

	private final JavaMailSender mailSender;

	@Value("${app.mail.from}")
	private String fromAddress;

	@Value("${application.baseUrl}")
	private String baseUrl;

	public MailService(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void sendTemporaryPasswordEmail(String toEmail, String firstName, String temporaryPassword) {
		log.info(generateLog("sendTemporaryPasswordEmail", this.getClass().getName()));
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

		log.info("Generated temporary password email");
		sendSimpleEmail(toEmail, subject, body);
	}

	public void sendEnquiryLink(String toEmail, String userName, String token) {
		log.info(generateLog("sendEnquiryLink", this.getClass().getName()));

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
		log.info("Generated enquiry verification email");
		sendHtmlEmail(toEmail, subject, body);
	}

	private void sendHtmlEmail(String toEmail, String subject, String body) {
		log.info(generateLog("sendHtmlEmail", this.getClass().getName()));
		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
			helper.setFrom(this.fromAddress);
			helper.setTo(toEmail);
			helper.setSubject(subject);
			helper.setText(body, true); // true = HTML
			log.info("About to send HTML email to " + toEmail);
			mailSender.send(mimeMessage);
			log.info("HTML email sent successfully to " + toEmail);
		} catch (MessagingException e) {
			log.error("Error while sending HTML email", e);
		}

	}

	private void sendSimpleEmail(String toEmail, String subject, String text) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom(this.fromAddress);
			message.setTo(toEmail);
			message.setSubject(subject);
			message.setText(text);
			log.info("About to send simple email to {}", toEmail);
			mailSender.send(message);
			log.info("Mail Sent Successfully");
		} catch (Exception e) {
			log.error("Error while sending mail", e);
		}
	}

	// Send mail with attachment
	public String sendMailWithAttachment(String toEmail, String subject, String text) {

		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper;

		try {

			helper = new MimeMessageHelper(mimeMessage, true);

			helper.setFrom(this.fromAddress);
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
