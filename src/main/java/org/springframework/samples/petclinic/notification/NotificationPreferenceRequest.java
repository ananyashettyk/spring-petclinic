package org.springframework.samples.petclinic.notification;

/**
 * Class representing the request body for notification preference update.
 */
public class NotificationPreferenceRequest {

	private String preference;

	public String getPreference() {
		return preference;
	}

	public void setPreference(String preference) {
		this.preference = preference;
	}

}
