package org.springframework.samples.petclinic.notification;

/**
 * Interface for notification services that can send notifications to pet owners.
 *
 * @author Claude
 */
public interface NotificationService {

	/**
	 * Send a notification to the recipient.
	 * @param notification the notification to send
	 * @return true if the notification was sent successfully, false otherwise
	 */
	boolean send(Notification notification);

	/**
	 * Check if the service can handle the specified notification based on the owner's
	 * preferences.
	 * @param notification the notification to check
	 * @return true if this service can handle the notification, false otherwise
	 */
	boolean canHandle(Notification notification);

}
