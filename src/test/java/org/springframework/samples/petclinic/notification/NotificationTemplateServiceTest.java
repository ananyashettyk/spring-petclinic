package org.springframework.samples.petclinic.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.samples.petclinic.owner.Visit;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class NotificationTemplateServiceTest {

	@InjectMocks
	private NotificationTemplateService templateService;

	private Owner owner;

	private Pet pet;

	private Visit visit;

	private Notification notification;

	private NotificationSchedule schedule;

	@BeforeEach
	void setUp() {
		// Set up test data for owner
		owner = new Owner();
		owner.setFirstName("John");
		owner.setLastName("Doe");
		owner.setEmail("john.doe@example.com");

		// Set up test data for pet
		pet = new Pet();
		pet.setName("Fluffy");
		PetType catType = new PetType();
		catType.setName("Cat");
		pet.setType(catType);
		pet.setBirthDate(LocalDate.of(2020, 1, 15));

		// Set up test data for visit
		visit = new Visit();
		visit.setDate(LocalDate.of(2023, 5, 10));
		visit.setDescription("Annual checkup");

		// Set up notification
		notification = new Notification("Hello {ownerFirstName}, your {petType} {petName} has an appointment.",
				NotificationType.APPOINTMENT_REMINDER, LocalDateTime.now(), owner);
		notification.setPet(pet);

		// Set up notification schedule
		schedule = new NotificationSchedule(
				"Reminder: {ownerFirstName}, your {petType} {petName} has a {visitDescription} on {visitDate}.",
				NotificationType.APPOINTMENT_REMINDER, LocalDateTime.now(), owner);
		schedule.setPet(pet);
		schedule.setVisit(visit);
	}

	@Test
	void shouldProcessTemplateWithOwnerPlaceholders() {
		// Given
		String template = "Hello {ownerFirstName} {ownerLastName}, this is a test message.";

		// When
		String processed = templateService.processTemplate(template, owner, null, null);

		// Then
		assertEquals("Hello John Doe, this is a test message.", processed);
	}

	@Test
	void shouldProcessTemplateWithPetPlaceholders() {
		// Given
		String template = "Your {petType} {petName} was born on {petBirthDate}.";

		// When
		String processed = templateService.processTemplate(template, owner, pet, null);

		// Then
		assertEquals("Your Cat Fluffy was born on January 15, 2020.", processed);
	}

	@Test
	void shouldProcessTemplateWithVisitPlaceholders() {
		// Given
		String template = "Upcoming visit on {visitDate}: {visitDescription}";

		// When
		String processed = templateService.processTemplate(template, owner, null, visit);

		// Then
		assertEquals("Upcoming visit on May 10, 2023: Annual checkup", processed);
	}

	@Test
	void shouldProcessTemplateWithAllPlaceholders() {
		// Given
		String template = "Hello {ownerFirstName}, your {petType} {petName} has a {visitDescription} scheduled on {visitDate}.";

		// When
		String processed = templateService.processTemplate(template, owner, pet, visit);

		// Then
		assertEquals("Hello John, your Cat Fluffy has a Annual checkup scheduled on May 10, 2023.", processed);
	}

	@Test
	void shouldHandleMissingPlaceholderValues() {
		// Given
		String template = "Hello {ownerFirstName}, your {petType} {petName} is {petAge} years old.";

		// When
		String processed = templateService.processTemplate(template, owner, pet, null);

		// Then - should preserve unrecognized placeholders
		assertEquals("Hello John, your Cat Fluffy is {petAge} years old.", processed);
	}

	@Test
	void shouldHandleNullTemplate() {
		// Given
		String template = null;

		// When
		String processed = templateService.processTemplate(template, owner, pet, null);

		// Then
		assertEquals("", processed);
	}

	@Test
	void shouldProcessNotification() {
		// When
		String processed = templateService.processNotification(notification);

		// Then
		assertEquals("Hello John, your Cat Fluffy has an appointment.", processed);
	}

	@Test
	void shouldProcessSchedule() {
		// When
		String processed = templateService.processSchedule(schedule);

		// Then
		assertEquals("Reminder: John, your Cat Fluffy has a Annual checkup on May 10, 2023.", processed);
	}

	@Test
	void shouldHandleNullPet() {
		// Given
		notification.setPet(null);

		// When
		String processed = templateService.processNotification(notification);

		// Then - should not include pet information
		assertEquals("Hello John, your {petType} {petName} has an appointment.", processed);
	}

	@Test
	void shouldHandleMultipleOccurrencesOfSamePlaceholder() {
		// Given
		String template = "Dear {ownerFirstName}, this is for {ownerFirstName} {ownerLastName}.";

		// When
		String processed = templateService.processTemplate(template, owner, null, null);

		// Then
		assertEquals("Dear John, this is for John Doe.", processed);
	}

}
