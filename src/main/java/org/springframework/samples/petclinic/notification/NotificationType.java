package org.springframework.samples.petclinic.notification;

/**
 * Enum representing the type of notification.
 */
public enum NotificationType {

	/**
	 * Reminder for an upcoming appointment
	 */
	APPOINTMENT_REMINDER,

	/**
	 * Reminder to take medication
	 */
	MEDICATION_REMINDER,

	/**
	 * Reminder for upcoming vaccination
	 */
	VACCINATION_REMINDER

}
