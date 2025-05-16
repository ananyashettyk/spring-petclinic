package org.springframework.samples.petclinic.notification;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.Pet;

/**
 * Repository class for <code>Notification</code> domain objects.
 *
 * @author Claude
 */
public interface NotificationRepository extends Repository<Notification, Integer> {

	/**
	 * Save a notification to the data store.
	 * @param notification the notification to save
	 */
	void save(Notification notification);

	/**
	 * Retrieve a {@link Notification} by its id.
	 * @param id the id to search for
	 * @return the notification if found
	 */
	Notification findById(Integer id);

	/**
	 * Find all notifications.
	 * @return a collection of all notifications
	 */
	List<Notification> findAll();

	/**
	 * Find all pending notifications that should be sent.
	 * @param currentTime the current time to compare against scheduled time
	 * @return a collection of notifications to be sent
	 */
	@Query("SELECT n FROM Notification n WHERE n.status = 'PENDING' AND n.scheduledTime <= :currentTime")
	List<Notification> findPendingNotifications(@Param("currentTime") LocalDateTime currentTime);

	/**
	 * Find notifications by owner.
	 * @param owner the owner whose notifications to find
	 * @return a collection of notifications
	 */
	List<Notification> findByOwner(Owner owner);

	/**
	 * Find notifications by pet.
	 * @param pet the pet whose notifications to find
	 * @return a collection of notifications
	 */
	List<Notification> findByPet(Pet pet);

	/**
	 * Find notifications by status.
	 * @param status the status to find
	 * @return a collection of notifications with the given status
	 */
	List<Notification> findByStatus(NotificationStatus status);

	/**
	 * Find notifications by type.
	 * @param type the type to find
	 * @return a collection of notifications with the given type
	 */
	List<Notification> findByType(NotificationType type);

	/**
	 * Delete a notification from the data store.
	 * @param notification the notification to remove
	 */
	void delete(Notification notification);

}
