package org.springframework.samples.petclinic.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

/**
 * Service for sending SMS notifications to pet owners using Twilio API.
 *
 * @author Claude
 */
@Service
public class SmsNotificationService implements NotificationService {

	private static final Logger log = LoggerFactory.getLogger(SmsNotificationService.class);

	private final NotificationTemplateService templateService;

	@Value("${twilio.account.sid}")
	private String twilioAccountSid;

	@Value("${twilio.auth.token}")
	private String twilioAuthToken;

	@Value("${twilio.phone.number}")
	private String twilioPhoneNumber;

	@Value("${twilio.enabled:false}")
	private boolean twilioEnabled;

	@Autowired
	public SmsNotificationService(NotificationTemplateService templateService) {
		this.templateService = templateService;
	}

	@Override
	public boolean send(Notification notification) {
		if (!canHandle(notification)) {
			log.debug("SMS notification skipped for notification: {}", notification.getId());
			return false;
		}

		// Initialize Twilio client if SMS is enabled
		if (!twilioEnabled) {
			log.warn("Twilio SMS service is disabled. Set twilio.enabled=true in application properties to enable.");
			notification.setStatus(NotificationStatus.SKIPPED);
			return false;
		}

		try {
			// Initialize Twilio with account credentials
			Twilio.init(twilioAccountSid, twilioAuthToken);

			// Process the notification message content
			String messageContent = templateService.processNotification(notification);

			// Format phone number for Twilio (add '+' prefix if not present)
			String phoneNumber = formatPhoneNumber(notification.getOwner().getTelephone());

			// Create and send the SMS message
			Message message = Message
				.creator(new PhoneNumber(phoneNumber), new PhoneNumber(twilioPhoneNumber),
						truncateIfNeeded(messageContent))
				.create();

			// Log the message SID from Twilio and update status
			log.info("SMS sent to {}, Twilio message SID: {}", phoneNumber, message.getSid());
			notification.setStatus(NotificationStatus.SENT);
			notification.setSentTime(java.time.LocalDateTime.now());

			return true;
		}
		catch (ApiException e) {
			// Handle Twilio API exceptions
			notification.setStatus(NotificationStatus.FAILED);
			log.error("Failed to send SMS notification: {}", e.getMessage(), e);
			return false;
		}
		catch (Exception e) {
			// Handle other unexpected exceptions
			notification.setStatus(NotificationStatus.FAILED);
			log.error("Unexpected error sending SMS notification: {}", e.getMessage(), e);
			return false;
		}
	}

	@Override
	public boolean canHandle(Notification notification) {
		// Check if the owner has a phone number and if their notification preference
		// includes SMS
		NotificationPreference preference = notification.getOwner().getNotificationPreference();
		String phone = notification.getOwner().getTelephone();

		return (phone != null && !phone.isEmpty())
				&& (preference == NotificationPreference.SMS || preference == NotificationPreference.BOTH);
	}

	/**
	 * Formats the phone number to be compatible with Twilio API.
	 * @param phoneNumber The raw phone number from the owner entity
	 * @return A properly formatted phone number for Twilio
	 */
	private String formatPhoneNumber(String phoneNumber) {
		// Basic formatting - add country code if not present
		if (phoneNumber != null && !phoneNumber.isEmpty()) {
			// Remove any non-digit characters
			String digitsOnly = phoneNumber.replaceAll("\\D", "");

			// If it's a 10-digit number (US/Canada), add the country code
			if (digitsOnly.length() == 10) {
				return "+1" + digitsOnly;
			}

			// If it already has a country code, ensure it starts with '+'
			if (!digitsOnly.startsWith("+")) {
				return "+" + digitsOnly;
			}

			return digitsOnly;
		}

		return phoneNumber;
	}

	/**
	 * Truncate the message if it exceeds SMS character limits.
	 * @param message The message to send
	 * @return Truncated message if needed, or the original message
	 */
	private String truncateIfNeeded(String message) {
		// Standard SMS has a limit of 160 characters for a single message
		final int SMS_CHAR_LIMIT = 160;

		if (message != null && message.length() > SMS_CHAR_LIMIT) {
			// Truncate and add ellipsis
			return message.substring(0, SMS_CHAR_LIMIT - 3) + "...";
		}

		return message;
	}

}
