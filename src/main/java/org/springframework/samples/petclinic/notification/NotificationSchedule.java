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
import org.springframework.samples.petclinic.owner.Visit;

/**
 * Simple JavaBean domain object representing a schedule for notifications to be sent.
 * This differs from Notification in that it represents a plan for notifications, rather
 * than the notifications themselves.
 *
 * @author Claude
 */
@Entity
@Table(name = "notification_schedules")
public class NotificationSchedule extends BaseEntity {

	@NotBlank
	@Column(name = "message_template")
	private String messageTemplate;

	@NotNull
	@Column(name = "type")
	@Enumerated(EnumType.STRING)
	private NotificationType type;

	@NotNull
	@Column(name = "scheduled_time")
	private LocalDateTime scheduledTime;

	@Column(name = "days_before")
	private Integer daysBefore;

	@Column(name = "enabled")
	private boolean enabled = true;

	@ManyToOne
	@JoinColumn(name = "owner_id")
	@NotNull
	private Owner owner;

	@ManyToOne
	@JoinColumn(name = "pet_id")
	private Pet pet;

	@ManyToOne
	@JoinColumn(name = "visit_id")
	private Visit visit;

	/**
	 * Creates a new instance of NotificationSchedule without setting any properties
	 */
	public NotificationSchedule() {
	}

	/**
	 * Creates a new instance of NotificationSchedule with essential properties
	 * @param messageTemplate the template message for notifications to be generated
	 * @param type the type of notification
	 * @param scheduledTime when the notification is scheduled to be sent
	 * @param owner the owner to receive the notification
	 */
	public NotificationSchedule(String messageTemplate, NotificationType type, LocalDateTime scheduledTime,
			Owner owner) {
		this.messageTemplate = messageTemplate;
		this.type = type;
		this.scheduledTime = scheduledTime;
		this.owner = owner;
	}

	/**
	 * Creates a new instance of NotificationSchedule for visit reminders
	 * @param messageTemplate the template message for notifications to be generated
	 * @param type the type of notification
	 * @param visit the visit associated with this notification schedule
	 * @param daysBefore days before the visit to send the notification
	 * @param owner the owner to receive the notification
	 * @param pet the pet associated with the visit
	 */
	public NotificationSchedule(String messageTemplate, NotificationType type, Visit visit, Integer daysBefore,
			Owner owner, Pet pet) {
		this.messageTemplate = messageTemplate;
		this.type = type;
		this.visit = visit;
		this.daysBefore = daysBefore;
		this.owner = owner;
		this.pet = pet;
		// Will need to calculate scheduledTime when the visit date is known
		if (visit != null && visit.getDate() != null && daysBefore != null) {
			this.scheduledTime = visit.getDate().atStartOfDay().minusDays(daysBefore);
		}
	}

	public String getMessageTemplate() {
		return this.messageTemplate;
	}

	public void setMessageTemplate(String messageTemplate) {
		this.messageTemplate = messageTemplate;
	}

	public NotificationType getType() {
		return this.type;
	}

	public void setType(NotificationType type) {
		this.type = type;
	}

	public LocalDateTime getScheduledTime() {
		return this.scheduledTime;
	}

	public void setScheduledTime(LocalDateTime scheduledTime) {
		this.scheduledTime = scheduledTime;
	}

	public Integer getDaysBefore() {
		return this.daysBefore;
	}

	public void setDaysBefore(Integer daysBefore) {
		this.daysBefore = daysBefore;
		// Update scheduledTime if visit date is available
		if (this.visit != null && this.visit.getDate() != null && daysBefore != null) {
			this.scheduledTime = this.visit.getDate().atStartOfDay().minusDays(daysBefore);
		}
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
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

	public Visit getVisit() {
		return this.visit;
	}

	public void setVisit(Visit visit) {
		this.visit = visit;
		// Update scheduledTime if daysBefore is available
		if (visit != null && visit.getDate() != null && this.daysBefore != null) {
			this.scheduledTime = visit.getDate().atStartOfDay().minusDays(this.daysBefore);
		}
	}

	/**
	 * Generate a notification from this schedule
	 * @return a new Notification instance based on this schedule
	 */
	public Notification generateNotification() {
		Notification notification = new Notification(this.messageTemplate, // Using
																			// template as
																			// initial
																			// message
				this.type, this.scheduledTime, this.owner);

		if (this.pet != null) {
			notification.setPet(this.pet);
		}

		return notification;
	}

	@Override
	public String toString() {
		return "NotificationSchedule{" + "id=" + getId() + ", messageTemplate='" + messageTemplate + '\'' + ", type="
				+ type + ", scheduledTime=" + scheduledTime + ", daysBefore=" + daysBefore + ", enabled=" + enabled
				+ ", owner=" + (owner != null ? owner.getId() : null) + ", pet=" + (pet != null ? pet.getId() : null)
				+ ", visit=" + (visit != null ? visit.getId() : null) + '}';
	}

}
