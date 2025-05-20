package org.springframework.samples.petclinic.notification;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class NotificationControllerIntegrationTests {

	@TestConfiguration
	static class MockedBeansConfig {

		@Bean
		public EmailNotificationService emailNotificationService() {
			return mock(EmailNotificationService.class);
		}

		@Bean
		public SmsNotificationService smsNotificationService() {
			return mock(SmsNotificationService.class);
		}

	}

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private NotificationRepository notificationRepository;

	@Autowired
	private OwnerRepository ownerRepository;

	@Autowired
	private EmailNotificationService emailNotificationService;

	@Autowired
	private SmsNotificationService smsNotificationService;

	@Autowired
	private EntityManager entityManager;

	private Owner testOwner;

	@BeforeEach
	void setup() {
		entityManager.createNativeQuery("DELETE FROM notifications").executeUpdate();
		entityManager.createNativeQuery("ALTER TABLE notifications ALTER COLUMN id RESTART WITH 1").executeUpdate();
		reset(emailNotificationService, smsNotificationService);
		PetType dogType = entityManager.createQuery("SELECT pt FROM PetType pt WHERE pt.name = :name", PetType.class)
			.setParameter("name", "dog")
			.getSingleResult();

		testOwner = new Owner();
		testOwner.setFirstName("John");
		testOwner.setLastName("Doe");
		testOwner.setAddress("123 Main St");
		testOwner.setCity("New York");
		testOwner.setTelephone("1234567890");
		testOwner.setEmail("john.doe@example.com");
		testOwner.setNotificationPreference(NotificationPreference.EMAIL);

		Pet pet = new Pet();
		pet.setName("Fluffy");
		pet.setType(dogType); // Uses persisted PetType
		pet.setBirthDate(java.time.LocalDate.now().minusYears(2));
		testOwner.addPet(pet);

		ownerRepository.save(testOwner);

		when(emailNotificationService.send(any(Notification.class))).thenReturn(true);
		when(emailNotificationService.canHandle(any(Notification.class))).thenReturn(true);
		when(smsNotificationService.send(any(Notification.class))).thenReturn(true);
		when(smsNotificationService.canHandle(any(Notification.class))).thenReturn(false);
	}

	@Test
	void testShowNotificationPreferencesForm() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/notification-preferences", testOwner.getId()))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("owner"))
			.andExpect(view().name("owners/notificationPreferencesForm"));
	}

	@Test
	void testProcessNotificationPreferencesForm() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/notification-preferences", testOwner.getId())
				.param("firstName", testOwner.getFirstName())
				.param("lastName", testOwner.getLastName())
				.param("address", testOwner.getAddress())
				.param("city", testOwner.getCity())
				.param("telephone", testOwner.getTelephone())
				.param("email", "updated.email@example.com")
				.param("notificationPreference", NotificationPreference.SMS.toString()))
			.andExpect(status().is3xxRedirection())
			.andExpect(flash().attributeExists("message"))
			.andExpect(redirectedUrl("/owners/" + testOwner.getId()));

		// Verify the owner has been updated
		Optional<Owner> updatedOwnerOpt = ownerRepository.findById(testOwner.getId());
		assert updatedOwnerOpt.isPresent();
		Owner updatedOwner = updatedOwnerOpt.get();
		assert updatedOwner.getEmail().equals("updated.email@example.com");
		assert updatedOwner.getNotificationPreference() == NotificationPreference.SMS;
	}

	@Test
	void testGetNotificationsForOwner() throws Exception {
		// Create a notification for the test owner
		Notification notification = new Notification("Test message", NotificationType.APPOINTMENT_REMINDER,
				LocalDateTime.now(), testOwner);
		notificationRepository.save(notification);

		mockMvc.perform(get("/api/notifications").param("ownerId", testOwner.getId().toString()))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
			.andExpect(jsonPath("$[0].owner.id", is(testOwner.getId())))
			.andExpect(jsonPath("$[0].type", is(NotificationType.APPOINTMENT_REMINDER.toString())));
	}

	@Test
	void testGetNotificationsWithInvalidOwner() throws Exception {
		mockMvc.perform(get("/api/notifications").param("ownerId", "999999")).andExpect(status().isNotFound());
	}

	@Test
	void testUpdateNotificationPreference() throws Exception {
		mockMvc
			.perform(put("/api/owners/{ownerId}/notification-preferences", testOwner.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"preference\": \"BOTH\"}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.ownerId", is(testOwner.getId())))
			.andExpect(jsonPath("$.notificationPreference", is("BOTH")));

		// Verify the owner has been updated
		Optional<Owner> updatedOwnerOpt = ownerRepository.findById(testOwner.getId());
		assert updatedOwnerOpt.isPresent();
		Owner updatedOwner = updatedOwnerOpt.get();
		assert updatedOwner.getNotificationPreference() == NotificationPreference.BOTH;
	}

	@Test
	void testUpdateNotificationPreferenceWithInvalidValue() throws Exception {
		mockMvc
			.perform(put("/api/owners/{ownerId}/notification-preferences", testOwner.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"preference\": \"INVALID_VALUE\"}"))
			.andExpect(status().isBadRequest());
	}

	@Test
	void testUpdateNotificationPreferenceWithInvalidOwner() throws Exception {
		mockMvc
			.perform(put("/api/owners/{ownerId}/notification-preferences", 999999)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"preference\": \"SMS\"}"))
			.andExpect(status().isNotFound());
	}

	@Test
	void testSendTestNotificationWithEmailPreference() throws Exception {
		// Set owner preference to EMAIL
		testOwner.setNotificationPreference(NotificationPreference.EMAIL);
		ownerRepository.save(testOwner);

		// Test sending a notification
		mockMvc
			.perform(post("/api/notifications/test").contentType(MediaType.APPLICATION_JSON)
				.content("{\"ownerId\": " + testOwner.getId() + ", \"message\": \"Test notification\"}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.ownerId", is(testOwner.getId())))
			.andExpect(jsonPath("$.status", is("SENT")))
			.andExpect(jsonPath("$.emailSent", is(true)))
			.andExpect(jsonPath("$.smsSent", is(false)))
			.andExpect(jsonPath("$.preference", is("EMAIL")));

		// Verify email service was called but not SMS service
		verify(emailNotificationService, times(1)).send(any(Notification.class));
		verify(smsNotificationService, never()).send(any(Notification.class));
	}

	@Test
	void testSendTestNotificationWithSmsPreference() throws Exception {
		// Set owner preference to SMS
		testOwner.setNotificationPreference(NotificationPreference.SMS);
		ownerRepository.save(testOwner);

		// Configure SMS service to accept the notification
		when(smsNotificationService.canHandle(any(Notification.class))).thenReturn(true);

		// Test sending a notification
		mockMvc
			.perform(post("/api/notifications/test").contentType(MediaType.APPLICATION_JSON)
				.content("{\"ownerId\": " + testOwner.getId() + ", \"message\": \"Test notification\"}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.ownerId", is(testOwner.getId())))
			.andExpect(jsonPath("$.status", is("SENT")))
			.andExpect(jsonPath("$.emailSent", is(false)))
			.andExpect(jsonPath("$.smsSent", is(true)))
			.andExpect(jsonPath("$.preference", is("SMS")));

		// Verify SMS service was called but not email service
		verify(smsNotificationService, times(1)).send(any(Notification.class));
		verify(emailNotificationService, never()).send(any(Notification.class));
	}

	@Test
	void testSendTestNotificationWithBothPreference() throws Exception {
		// Set owner preference to BOTH
		testOwner.setNotificationPreference(NotificationPreference.BOTH);
		ownerRepository.save(testOwner);

		// Configure both services to accept the notification
		when(smsNotificationService.canHandle(any(Notification.class))).thenReturn(true);

		// Test sending a notification
		mockMvc
			.perform(post("/api/notifications/test").contentType(MediaType.APPLICATION_JSON)
				.content("{\"ownerId\": " + testOwner.getId() + ", \"message\": \"Test notification\"}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.ownerId", is(testOwner.getId())))
			.andExpect(jsonPath("$.status", is("SENT")))
			.andExpect(jsonPath("$.emailSent", is(true)))
			.andExpect(jsonPath("$.smsSent", is(true)))
			.andExpect(jsonPath("$.preference", is("BOTH")));

		// Verify both services were called
		verify(emailNotificationService, times(1)).send(any(Notification.class));
		verify(smsNotificationService, times(1)).send(any(Notification.class));
	}

	@Test
	void testSendTestNotificationWithNoPreference() throws Exception {
		// Set owner preference to NONE
		testOwner.setNotificationPreference(NotificationPreference.NONE);
		ownerRepository.save(testOwner);

		// Test sending a notification
		mockMvc
			.perform(post("/api/notifications/test").contentType(MediaType.APPLICATION_JSON)
				.content("{\"ownerId\": " + testOwner.getId() + ", \"message\": \"Test notification\"}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.ownerId", is(testOwner.getId())))
			.andExpect(jsonPath("$.status", is("PENDING")))
			.andExpect(jsonPath("$.emailSent", is(false)))
			.andExpect(jsonPath("$.smsSent", is(false)))
			.andExpect(jsonPath("$.preference", is("NONE")))
			.andExpect(jsonPath("$.message", containsString("No notification sent")));

		// Verify no services were called
		verify(emailNotificationService, never()).send(any(Notification.class));
		verify(smsNotificationService, never()).send(any(Notification.class));
	}

	@Test
	void testSendTestNotificationWithPet() throws Exception {
		// Get the pet ID from the test owner
		Pet pet = testOwner.getPets().get(0);

		// Test sending a notification with pet specified
		mockMvc
			.perform(post("/api/notifications/test").contentType(MediaType.APPLICATION_JSON)
				.content("{\"ownerId\": " + testOwner.getId() + ", \"petId\": " + pet.getId()
						+ ", \"message\": \"Test notification for {petName}\"}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.notificationId", notNullValue()));

		// Verify a notification was created with the pet
		verify(emailNotificationService, times(1)).send(argThat(
				notification -> notification.getPet() != null && notification.getPet().getId().equals(pet.getId())));
	}

	@Test
	void testSendTestNotificationWithInvalidOwner() throws Exception {
		mockMvc
			.perform(post("/api/notifications/test").contentType(MediaType.APPLICATION_JSON)
				.content("{\"ownerId\": 999999, \"message\": \"Test notification\"}"))
			.andExpect(status().isNotFound());
	}

}
