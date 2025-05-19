package org.springframework.samples.petclinic.notification;

/**
 * Class representing the request body for test notification.
 */
public class TestNotificationRequest {

	private Integer ownerId;

	private Integer petId;

	private String message;

	public Integer getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Integer ownerId) {
		this.ownerId = ownerId;
	}

	public Integer getPetId() {
		return petId;
	}

	public void setPetId(Integer petId) {
		this.petId = petId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
