package org.springframework.samples.petclinic.notification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.Visit;

/**
 * Repository class for <code>NotificationSchedule</code> domain objects.
 *
 * @author Claude
 */
public interface NotificationScheduleRepository extends Repository<NotificationSchedule, Integer> {

	/**
	 * Save a notification schedule to the data store.
	 * @param notificationSchedule the notification schedule to save
	 */
	void save(NotificationSchedule notificationSchedule);

	/**
	 * Retrieve a {@link NotificationSchedule} by its id.
	 * @param id the id to search for
	 * @return the notification schedule if found
	 */
	NotificationSchedule findById(Integer id);

	/**
	 * Find all notification schedules.
	 * @return a collection of all notification schedules
	 */
	List<NotificationSchedule> findAll();

	/**
	 * Find active notification schedules that should generate notifications.
	 * @param currentTime the current time to compare against scheduled time
	 * @return a collection of notification schedules that need to be processed
	 */
	@Query("SELECT ns FROM NotificationSchedule ns WHERE ns.enabled = true AND ns.scheduledTime <= :currentTime")
	List<NotificationSchedule> findActiveSchedulesDue(@Param("currentTime") LocalDateTime currentTime);

	/**
	 * Find notification schedules by owner.
	 * @param owner the owner whose notification schedules to find
	 * @return a collection of notification schedules
	 */
	List<NotificationSchedule> findByOwner(Owner owner);

	/**
	 * Find notification schedules by pet.
	 * @param pet the pet whose notification schedules to find
	 * @return a collection of notification schedules
	 */
	List<NotificationSchedule> findByPet(Pet pet);

	/**
	 * Find notification schedules by visit.
	 * @param visit the visit whose notification schedules to find
	 * @return a collection of notification schedules
	 */
	List<NotificationSchedule> findByVisit(Visit visit);

	/**
	 * Find notification schedules by type.
	 * @param type the type to find
	 * @return a collection of notification schedules with the given type
	 */
	List<NotificationSchedule> findByType(NotificationType type);

	/**
	 * Find notification schedules for a specific pet and type.
	 * @param pet the pet whose notification schedules to find
	 * @param type the type of notification
	 * @return a collection of notification schedules
	 */
	List<NotificationSchedule> findByPetAndType(Pet pet, NotificationType type);

	/**
	 * Find notification schedules for upcoming visits.
	 * @param startDate the start date for the search range
	 * @param endDate the end date for the search range
	 * @return a collection of notification schedules for upcoming visits
	 */
	@Query("SELECT ns FROM NotificationSchedule ns JOIN ns.visit v WHERE v.date BETWEEN :startDate AND :endDate")
	List<NotificationSchedule> findUpcomingVisitSchedules(@Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate);

	/**
	 * Delete a notification schedule from the data store.
	 * @param notificationSchedule the notification schedule to remove
	 */
	void delete(NotificationSchedule notificationSchedule);

}
