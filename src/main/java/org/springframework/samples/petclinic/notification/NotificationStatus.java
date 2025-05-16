package org.springframework.samples.petclinic.notification;

/**
 * Enum representing the status of a notification.
 */
public enum NotificationStatus {

	/**
	 * Notification is waiting to be sent
	 */
	PENDING,

	/**
	 * Notification was successfully sent
	 */
	SENT,

	/**
	 * Notification failed to send
	 */
	FAILED,

	/**
	 * Notification was skipped
	 */
	SKIPPED

}
