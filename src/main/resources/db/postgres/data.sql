INSERT INTO vets (first_name, last_name) SELECT 'James', 'Carter' WHERE NOT EXISTS (SELECT * FROM vets WHERE id=1);
INSERT INTO vets (first_name, last_name) SELECT 'Helen', 'Leary' WHERE NOT EXISTS (SELECT * FROM vets WHERE id=2);
INSERT INTO vets (first_name, last_name) SELECT 'Linda', 'Douglas' WHERE NOT EXISTS (SELECT * FROM vets WHERE id=3);
INSERT INTO vets (first_name, last_name) SELECT 'Rafael', 'Ortega' WHERE NOT EXISTS (SELECT * FROM vets WHERE id=4);
INSERT INTO vets (first_name, last_name) SELECT 'Henry', 'Stevens' WHERE NOT EXISTS (SELECT * FROM vets WHERE id=5);
INSERT INTO vets (first_name, last_name) SELECT 'Sharon', 'Jenkins' WHERE NOT EXISTS (SELECT * FROM vets WHERE id=6);

INSERT INTO specialties (name) SELECT 'radiology' WHERE NOT EXISTS (SELECT * FROM specialties WHERE name='radiology');
INSERT INTO specialties (name) SELECT 'surgery' WHERE NOT EXISTS (SELECT * FROM specialties WHERE name='surgery'); 
INSERT INTO specialties (name) SELECT 'dentistry' WHERE NOT EXISTS (SELECT * FROM specialties WHERE name='dentistry');

INSERT INTO vet_specialties VALUES (2, 1) ON CONFLICT (vet_id, specialty_id) DO NOTHING;
INSERT INTO vet_specialties VALUES (3, 2) ON CONFLICT (vet_id, specialty_id) DO NOTHING;
INSERT INTO vet_specialties VALUES (3, 3) ON CONFLICT (vet_id, specialty_id) DO NOTHING;
INSERT INTO vet_specialties VALUES (4, 2) ON CONFLICT (vet_id, specialty_id) DO NOTHING;
INSERT INTO vet_specialties VALUES (5, 1) ON CONFLICT (vet_id, specialty_id) DO NOTHING;

INSERT INTO types (name) SELECT 'cat' WHERE NOT EXISTS (SELECT * FROM types WHERE name='cat');
INSERT INTO types (name) SELECT 'dog' WHERE NOT EXISTS (SELECT * FROM types WHERE name='dog');
INSERT INTO types (name) SELECT 'lizard' WHERE NOT EXISTS (SELECT * FROM types WHERE name='lizard');
INSERT INTO types (name) SELECT 'snake' WHERE NOT EXISTS (SELECT * FROM types WHERE name='snake');
INSERT INTO types (name) SELECT 'bird' WHERE NOT EXISTS (SELECT * FROM types WHERE name='bird');
INSERT INTO types (name) SELECT 'hamster' WHERE NOT EXISTS (SELECT * FROM types WHERE name='hamster');

INSERT INTO owners (first_name, last_name, address, city, telephone, email, notification_preference)
  SELECT 'George', 'Franklin', '110 W. Liberty St.', 'Madison', '6085551023', 'george.franklin@example.com', 'EMAIL'
  WHERE NOT EXISTS (SELECT * FROM owners WHERE id=1);
INSERT INTO owners (first_name, last_name, address, city, telephone, email, notification_preference)
  SELECT 'Betty', 'Davis', '638 Cardinal Ave.', 'Sun Prairie', '6085551749', 'betty.davis@example.com', 'NONE'
  WHERE NOT EXISTS (SELECT * FROM owners WHERE id=2);
INSERT INTO owners (first_name, last_name, address, city, telephone, email, notification_preference)
  SELECT 'Eduardo', 'Rodriquez', '2693 Commerce St.', 'McFarland', '6085558763', 'eduardo.rodriguez@example.com', 'SMS'
  WHERE NOT EXISTS (SELECT * FROM owners WHERE id=3);
INSERT INTO owners (first_name, last_name, address, city, telephone, email, notification_preference)
  SELECT 'Harold', 'Davis', '563 Friendly St.', 'Windsor', '6085553198', 'harold.davis@example.com', 'BOTH'
  WHERE NOT EXISTS (SELECT * FROM owners WHERE id=4);
INSERT INTO owners (first_name, last_name, address, city, telephone, email, notification_preference)
  SELECT 'Peter', 'McTavish', '2387 S. Fair Way', 'Madison', '6085552765', 'peter.mctavish@example.com', 'EMAIL'
  WHERE NOT EXISTS (SELECT * FROM owners WHERE id=5);
INSERT INTO owners (first_name, last_name, address, city, telephone, email, notification_preference)
  SELECT 'Jean', 'Coleman', '105 N. Lake St.', 'Monona', '6085552654', 'jean.coleman@example.com', 'NONE'
  WHERE NOT EXISTS (SELECT * FROM owners WHERE id=6);
INSERT INTO owners (first_name, last_name, address, city, telephone, email, notification_preference)
  SELECT 'Jeff', 'Black', '1450 Oak Blvd.', 'Monona', '6085555387', 'jeff.black@example.com', 'SMS'
  WHERE NOT EXISTS (SELECT * FROM owners WHERE id=7);
INSERT INTO owners (first_name, last_name, address, city, telephone, email, notification_preference)
  SELECT 'Maria', 'Escobito', '345 Maple St.', 'Madison', '6085557683', 'maria.escobito@example.com', 'BOTH'
  WHERE NOT EXISTS (SELECT * FROM owners WHERE id=8);
INSERT INTO owners (first_name, last_name, address, city, telephone, email, notification_preference)
  SELECT 'David', 'Schroeder', '2749 Blackhawk Trail', 'Madison', '6085559435', 'david.schroeder@example.com', 'EMAIL'
  WHERE NOT EXISTS (SELECT * FROM owners WHERE id=9);
INSERT INTO owners (first_name, last_name, address, city, telephone, email, notification_preference)
  SELECT 'Carlos', 'Estaban', '2335 Independence La.', 'Waunakee', '6085555487', 'carlos.estaban@example.com', 'NONE'
  WHERE NOT EXISTS (SELECT * FROM owners WHERE id=10);

INSERT INTO pets (name, birth_date, type_id, owner_id) SELECT 'Leo', '2000-09-07', 1, 1 WHERE NOT EXISTS (SELECT * FROM pets WHERE id=1);
INSERT INTO pets (name, birth_date, type_id, owner_id) SELECT 'Basil', '2002-08-06', 6, 2 WHERE NOT EXISTS (SELECT * FROM pets WHERE id=2);
INSERT INTO pets (name, birth_date, type_id, owner_id) SELECT 'Rosy', '2001-04-17', 2, 3 WHERE NOT EXISTS (SELECT * FROM pets WHERE id=3);
INSERT INTO pets (name, birth_date, type_id, owner_id) SELECT 'Jewel', '2000-03-07', 2, 3 WHERE NOT EXISTS (SELECT * FROM pets WHERE id=4);
INSERT INTO pets (name, birth_date, type_id, owner_id) SELECT 'Iggy', '2000-11-30', 3, 4 WHERE NOT EXISTS (SELECT * FROM pets WHERE id=5);
INSERT INTO pets (name, birth_date, type_id, owner_id) SELECT 'George', '2000-01-20', 4, 5 WHERE NOT EXISTS (SELECT * FROM pets WHERE id=6);
INSERT INTO pets (name, birth_date, type_id, owner_id) SELECT 'Samantha', '1995-09-04', 1, 6 WHERE NOT EXISTS (SELECT * FROM pets WHERE id=7);
INSERT INTO pets (name, birth_date, type_id, owner_id) SELECT 'Max', '1995-09-04', 1, 6 WHERE NOT EXISTS (SELECT * FROM pets WHERE id=8);
INSERT INTO pets (name, birth_date, type_id, owner_id) SELECT 'Lucky', '1999-08-06', 5, 7 WHERE NOT EXISTS (SELECT * FROM pets WHERE id=9);
INSERT INTO pets (name, birth_date, type_id, owner_id) SELECT 'Mulligan', '1997-02-24', 2, 8 WHERE NOT EXISTS (SELECT * FROM pets WHERE id=10);
INSERT INTO pets (name, birth_date, type_id, owner_id) SELECT 'Freddy', '2000-03-09', 5, 9 WHERE NOT EXISTS (SELECT * FROM pets WHERE id=11);
INSERT INTO pets (name, birth_date, type_id, owner_id) SELECT 'Lucky', '2000-06-24', 2, 10 WHERE NOT EXISTS (SELECT * FROM pets WHERE id=12);
INSERT INTO pets (name, birth_date, type_id, owner_id) SELECT 'Sly', '2002-06-08', 1, 10 WHERE NOT EXISTS (SELECT * FROM pets WHERE id=13);

INSERT INTO visits (pet_id, visit_date, description) SELECT 7, '2010-03-04', 'rabies shot' WHERE NOT EXISTS (SELECT * FROM visits WHERE id=1);
INSERT INTO visits (pet_id, visit_date, description) SELECT 8, '2011-03-04', 'rabies shot' WHERE NOT EXISTS (SELECT * FROM visits WHERE id=2);
INSERT INTO visits (pet_id, visit_date, description) SELECT 8, '2009-06-04', 'neutered' WHERE NOT EXISTS (SELECT * FROM visits WHERE id=3);
INSERT INTO visits (pet_id, visit_date, description) SELECT 7, '2008-09-04', 'spayed' WHERE NOT EXISTS (SELECT * FROM visits WHERE id=4);

-- Sample data for notifications table
INSERT INTO notifications (message, type, status, scheduled_time, sent_time, owner_id, pet_id)
  SELECT 'Reminder: Your appointment with Dr. Carter for Leo is tomorrow at 10:00 AM', 'EMAIL', 'PENDING', '2025-05-20 10:00:00', NULL, 1, 1
  WHERE NOT EXISTS (SELECT * FROM notifications WHERE id=1);

INSERT INTO notifications (message, type, status, scheduled_time, sent_time, owner_id, pet_id)
  SELECT 'Reminder: Basil is due for annual checkup next week', 'SMS', 'SENT', '2025-05-15 09:00:00', '2025-05-15 09:05:23', 2, 2
  WHERE NOT EXISTS (SELECT * FROM notifications WHERE id=2);

INSERT INTO notifications (message, type, status, scheduled_time, sent_time, owner_id, pet_id)
  SELECT 'Time for Rosy''s heartworm medication', 'EMAIL', 'PENDING', '2025-05-25 08:00:00', NULL, 3, 3
  WHERE NOT EXISTS (SELECT * FROM notifications WHERE id=3);

INSERT INTO notifications (message, type, status, scheduled_time, sent_time, owner_id, pet_id)
  SELECT 'Reminder: Jewel has an appointment on Monday for vaccinations', 'SMS', 'FAILED', '2025-05-18 11:00:00', '2025-05-18 11:02:45', 3, 4
  WHERE NOT EXISTS (SELECT * FROM notifications WHERE id=4);

INSERT INTO notifications (message, type, status, scheduled_time, sent_time, owner_id, pet_id)
  SELECT 'Reminder: Iggy''s dental cleaning is scheduled for next Friday', 'EMAIL', 'SENT', '2025-05-10 14:00:00', '2025-05-10 14:01:12', 4, 5
  WHERE NOT EXISTS (SELECT * FROM notifications WHERE id=5);

-- Sample data for notification_schedules table
INSERT INTO notification_schedules (message_template, type, scheduled_time, days_before, enabled, owner_id, pet_id, visit_id)
  SELECT 'Reminder: Your pet {petName} has an appointment tomorrow', 'EMAIL', '2025-06-15 09:00:00', 1, TRUE, 1, 1, 1
  WHERE NOT EXISTS (SELECT * FROM notification_schedules WHERE id=1);

INSERT INTO notification_schedules (message_template, type, scheduled_time, days_before, enabled, owner_id, pet_id, visit_id)
  SELECT 'Time for {petName}''s annual checkup', 'SMS', '2025-06-20 10:00:00', NULL, TRUE, 2, 2, NULL
  WHERE NOT EXISTS (SELECT * FROM notification_schedules WHERE id=2);

INSERT INTO notification_schedules (message_template, type, scheduled_time, days_before, enabled, owner_id, pet_id, visit_id)
  SELECT '{petName} is due for heartworm medication', 'BOTH', '2025-06-10 08:00:00', NULL, TRUE, 3, 3, NULL
  WHERE NOT EXISTS (SELECT * FROM notification_schedules WHERE id=3);

INSERT INTO notification_schedules (message_template, type, scheduled_time, days_before, enabled, owner_id, pet_id, visit_id)
  SELECT 'Reminder: {petName}''s vaccines are due', 'EMAIL', '2025-07-05 11:00:00', 7, TRUE, 4, 5, 2
  WHERE NOT EXISTS (SELECT * FROM notification_schedules WHERE id=4);

INSERT INTO notification_schedules (message_template, type, scheduled_time, days_before, enabled, owner_id, pet_id, visit_id)
  SELECT 'Reminder: {petName} has a follow-up appointment scheduled', 'SMS', '2025-07-15 14:00:00', 2, FALSE, 6, 7, 4
  WHERE NOT EXISTS (SELECT * FROM notification_schedules WHERE id=5);
