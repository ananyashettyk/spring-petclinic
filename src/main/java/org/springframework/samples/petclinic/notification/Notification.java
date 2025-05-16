/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.notification;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.Pet;

/**
 * Simple JavaBean domain object representing a notification sent to pet owners.
 *
 * @author Claude
 */
@Entity
@Table(name = "notifications")
public class Notification extends BaseEntity {

	@NotBlank
	@Column(name = "message")
	private String message;

	@NotNull
	@Column(name = "type")
	@Enumerated(EnumType.STRING)
	private NotificationType type;

	@NotNull
	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private NotificationStatus status = NotificationStatus.PENDING;

	@NotNull
	@Column(name = "scheduled_time")
	private LocalDateTime scheduledTime;

	@Column(name = "sent_time")
	private LocalDateTime sentTime;

	@ManyToOne
	@JoinColumn(name = "owner_id")
	@NotNull
	private Owner owner;

	@ManyToOne
	@JoinColumn(name = "pet_id")
	private Pet pet;

	/**
	 * Creates a new instance of Notification without setting any properties
	 */
	public Notification() {
	}

	/**
	 * Creates a new instance of Notification with essential properties
	 * @param message the notification message
	 * @param type the type of notification
	 * @param scheduledTime when the notification is scheduled to be sent
	 * @param owner the owner to receive the notification
	 */
	public Notification(String message, NotificationType type, LocalDateTime scheduledTime, Owner owner) {
		this.message = message;
		this.type = type;
		this.scheduledTime = scheduledTime;
		this.owner = owner;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public NotificationType getType() {
		return this.type;
	}

	public void setType(NotificationType type) {
		this.type = type;
	}

	public NotificationStatus getStatus() {
		return this.status;
	}

	public void setStatus(NotificationStatus status) {
		this.status = status;
	}

	public LocalDateTime getScheduledTime() {
		return this.scheduledTime;
	}

	public void setScheduledTime(LocalDateTime scheduledTime) {
		this.scheduledTime = scheduledTime;
	}

	public LocalDateTime getSentTime() {
		return this.sentTime;
	}

	public void setSentTime(LocalDateTime sentTime) {
		this.sentTime = sentTime;
	}

	public Owner getOwner() {
		return this.owner;
	}

	public void setOwner(Owner owner) {
		this.owner = owner;
	}

	public Pet getPet() {
		return this.pet;
	}

	public void setPet(Pet pet) {
		this.pet = pet;
	}

	@Override
	public String toString() {
		return "Notification{" + "id=" + getId() + ", message='" + message + '\'' + ", type=" + type + ", status="
				+ status + ", scheduledTime=" + scheduledTime + ", sentTime=" + sentTime + ", owner="
				+ (owner != null ? owner.getId() : null) + ", pet=" + (pet != null ? pet.getId() : null) + '}';
	}

}
