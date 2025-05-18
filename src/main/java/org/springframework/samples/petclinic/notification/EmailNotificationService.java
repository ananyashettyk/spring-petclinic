package org.springframework.samples.petclinic.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service for sending email notifications to pet owners.
 *
 * @author Claude
 */
@Service
public class EmailNotificationService implements NotificationService {

	private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);

	private final JavaMailSender emailSender;

	private final NotificationTemplateService templateService;

	@Value("${spring.mail.username:petclinic@example.com}")
	private String senderEmail;

	@Autowired
	public EmailNotificationService(JavaMailSender emailSender, NotificationTemplateService templateService) {
		this.emailSender = emailSender;
		this.templateService = templateService;
	}

	@Override
	public boolean send(Notification notification) {
		if (!canHandle(notification)) {
			log.debug("Email notification skipped for notification: {}", notification.getId());
			return false;
		}

		try {
			// Get the processed message from the template service
			String messageContent = templateService.processNotification(notification);

			// Create the email message
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom(senderEmail);
			message.setTo(notification.getOwner().getEmail());
			message.setSubject(getSubjectForNotificationType(notification.getType()));
			message.setText(messageContent);

			// Send the email
			emailSender.send(message);

			// Update notification status
			notification.setStatus(NotificationStatus.SENT);
			notification.setSentTime(java.time.LocalDateTime.now());

			log.info("Email notification sent successfully to: {}", notification.getOwner().getEmail());
			return true;
		}
		catch (MailException e) {
			// Handle the email sending failure
			notification.setStatus(NotificationStatus.FAILED);
			log.error("Failed to send email notification: {}", e.getMessage(), e);
			return false;
		}
	}

	@Override
	public boolean canHandle(Notification notification) {
		// Check if the owner has an email address and if their notification preference
		// includes email
		NotificationPreference preference = notification.getOwner().getNotificationPreference();
		String email = notification.getOwner().getEmail();

		return (email != null && !email.isEmpty())
				&& (preference == NotificationPreference.EMAIL || preference == NotificationPreference.BOTH);
	}

	/**
	 * Generate an appropriate subject line based on the notification type
	 * @param type the notification type
	 * @return a subject line for the email
	 */
	private String getSubjectForNotificationType(NotificationType type) {
		return switch (type) {
			case APPOINTMENT_REMINDER -> "Pet Clinic: Appointment Reminder";
			case MEDICATION_REMINDER -> "Pet Clinic: Medication Reminder";
			case VACCINATION_REMINDER -> "Pet Clinic: Vaccination Reminder";
			default -> "Pet Clinic Notification";
		};
	}

}
