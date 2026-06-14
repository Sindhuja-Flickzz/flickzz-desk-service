package com.flickzz.desk.service;

import static com.flickzz.desk.config.FlickzzDeskUtility.*;

import java.util.concurrent.*;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.util.*;

import com.resend.*;
import com.resend.core.exception.*;
import com.resend.services.emails.model.*;

@Service
public class MailService {

	private static final Logger log = LoggerFactory.getLogger(MailService.class);

	private final Resend resend;

	@Value("${app.mail.from}")
	private String fromAddress;

	@Value("${application.baseUrl}")
	private String baseUrl;

	public MailService(Resend resend) {
		this.resend = resend;
	}

	@Async
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

	@Async
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
			CreateEmailOptions options = CreateEmailOptions.builder().from(this.fromAddress).to(toEmail)
					.subject(subject).html(body).build();

			log.info("About to send HTML email to {}", toEmail);
			log.info("Sending email from {}", this.fromAddress);
			CreateEmailResponse response = resend.emails().send(options);
			log.info("HTML email sent successfully to {}. Email ID: {}", toEmail, response.getId());
		} catch (ResendException e) {
			log.error("Error while sending HTML email via Resend API", e);
		}
	}

	private void sendSimpleEmail(String toEmail, String subject, String text) {
		try {
			CreateEmailOptions options = CreateEmailOptions.builder().from(this.fromAddress).to(toEmail)
					.subject(subject).text(text).build();

			log.info("About to send simple email to {}", toEmail);
			CreateEmailResponse response = resend.emails().send(options);
			log.info("Mail sent successfully to {}. Email ID: {}", toEmail, response.getId());
		} catch (ResendException e) {
			log.error("Error while sending mail via Resend API", e);
		}
	}

	// Send mail with attachment
	// Note: Resend API v3 has limited attachment support. Consider using Resend's
	// attachment endpoint
	// or implementing multipart form data upload separately.
	@Async
	public CompletableFuture<String> sendMailWithAttachment(String toEmail, String subject, String text) {
		try {
			CreateEmailOptions options = CreateEmailOptions.builder().from(this.fromAddress).to(toEmail)
					.subject(subject).text(text).build();

			log.info("Sending email with attachment via Resend to {}", toEmail);
			CreateEmailResponse response = resend.emails().send(options);
			log.info("Email sent successfully. Email ID: {}", response.getId());

			return CompletableFuture.completedFuture("Mail Sent Successfully");

		} catch (ResendException e) {
			log.error("Error while sending mail via Resend API", e);
			return CompletableFuture.failedFuture(new RuntimeException("Error while sending mail", e));
		}
	}
}
