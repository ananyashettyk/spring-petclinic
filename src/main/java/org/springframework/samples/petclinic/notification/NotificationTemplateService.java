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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.Visit;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

/**
 * Service class for managing notification templates and processing placeholders.
 *
 * @author Claude
 */
@Service
public class NotificationTemplateService {

	private static final Logger log = LoggerFactory.getLogger(NotificationTemplateService.class);

	private static final String TEMPLATE_BASE_PATH = "templates/notifications/";

	private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{([^}]+)\\}");

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM d, yyyy");

	private final Map<NotificationType, String> templateCache = new HashMap<>();

	/**
	 * Load a template for the given notification type.
	 * @param type the notification type
	 * @return the template content
	 */
	public String loadTemplate(NotificationType type) {
		if (templateCache.containsKey(type)) {
			return templateCache.get(type);
		}

		String templateFileName = getTemplateFileName(type);
		String templateContent = loadTemplateFromFile(templateFileName);
		templateCache.put(type, templateContent);
		return templateContent;
	}

	/**
	 * Process a notification message by replacing placeholders with actual values.
	 * @param templateContent the template content with placeholders
	 * @param owner the pet owner
	 * @param pet the pet (can be null)
	 * @param visit the visit (can be null)
	 * @return the processed message with placeholders replaced
	 */
	public String processTemplate(String templateContent, Owner owner, Pet pet, Visit visit) {
		Map<String, String> placeholders = new HashMap<>();

		// Add owner-related placeholders
		placeholders.put("ownerFirstName", owner.getFirstName());
		placeholders.put("ownerLastName", owner.getLastName());
		placeholders.put("ownerFullName", owner.getFirstName() + " " + owner.getLastName());

		// Add pet-related placeholders if pet is provided
		if (pet != null) {
			placeholders.put("petName", pet.getName());
			placeholders.put("petType", pet.getType() != null ? pet.getType().getName() : "pet");
			placeholders.put("petBirthDate",
					pet.getBirthDate() != null ? pet.getBirthDate().format(DATE_FORMATTER) : "");
		}

		// Add visit-related placeholders if visit is provided
		if (visit != null) {
			placeholders.put("visitDate", visit.getDate() != null ? visit.getDate().format(DATE_FORMATTER) : "");
			placeholders.put("visitDescription", visit.getDescription());
		}

		return replacePlaceholders(templateContent, placeholders);
	}

	/**
	 * Process a notification template for a specific notification.
	 * @param notification the notification containing the message template and associated
	 * entities
	 * @return the processed message with placeholders replaced
	 */
	public String processNotification(Notification notification) {
		String template = notification.getMessage();
		Owner owner = notification.getOwner();
		Pet pet = notification.getPet();

		// We don't have a direct link to Visit in Notification, so we'll pass null
		return processTemplate(template, owner, pet, null);
	}

	/**
	 * Process a notification schedule to generate a concrete message.
	 * @param schedule the notification schedule containing the message template
	 * @return the processed message with placeholders replaced
	 */
	public String processSchedule(NotificationSchedule schedule) {
		String template = schedule.getMessageTemplate();
		Owner owner = schedule.getOwner();
		Pet pet = schedule.getPet();
		Visit visit = schedule.getVisit();

		return processTemplate(template, owner, pet, visit);
	}

	/**
	 * Replace placeholders in the template content with actual values.
	 * @param content the template content
	 * @param placeholders a map of placeholder names to their values
	 * @return the processed content
	 */
	private String replacePlaceholders(String content, Map<String, String> placeholders) {
		if (content == null) {
			return "";
		}

		Matcher matcher = PLACEHOLDER_PATTERN.matcher(content);
		StringBuffer sb = new StringBuffer();

		while (matcher.find()) {
			String placeholderName = matcher.group(1);
			String value = placeholders.getOrDefault(placeholderName, matcher.group(0));
			// Escape $ and \ for the replacement string in appendReplacement
			value = value.replace("\\", "\\\\").replace("$", "\\$");
			matcher.appendReplacement(sb, value);
		}
		matcher.appendTail(sb);

		return sb.toString();
	}

	/**
	 * Get the template file name based on notification type.
	 * @param type the notification type
	 * @return the template file name
	 */
	private String getTemplateFileName(NotificationType type) {
		return switch (type) {
			case APPOINTMENT_REMINDER -> "appointment_reminder.txt";
			case MEDICATION_REMINDER -> "medication_reminder.txt";
			case VACCINATION_REMINDER -> "vaccination_reminder.txt";
			default -> "default_notification.txt";
		};
	}

	/**
	 * Load template content from a file.
	 * @param fileName the template file name
	 * @return the template content
	 */
	private String loadTemplateFromFile(String fileName) {
		String path = TEMPLATE_BASE_PATH + fileName;
		Resource resource = new ClassPathResource(path);

		try (InputStream inputStream = resource.getInputStream()) {
			return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
		}
		catch (IOException e) {
			log.error("Failed to load notification template: {}", path, e);
			return getDefaultTemplate();
		}
	}

	/**
	 * Provides a default template when the specific template cannot be loaded.
	 * @return a default template content
	 */
	private String getDefaultTemplate() {
		return "Dear {ownerFirstName}, this is a notification from the Pet Clinic.";
	}

}
