BEGIN;

-- 1. PERSON DATA (20 Records)
INSERT INTO person (username, password, full_name, date_of_birth, contact_information, role) VALUES
('jdoe', 'pass123', 'John Doe', '1990-05-15', '{"email": "john@example.com"}', 'ATTENDEE'),
('asmith', 'pass123', 'Alice Smith', '1988-11-22', '{"email": "alice@example.com"}', 'ATTENDEE'),
('bwilliams', 'pass123', 'Bob Williams', '1995-02-10', '{"email": "bob@example.com"}', 'ATTENDEE'),
('cclark', 'pass123', 'Charlie Clark', '1992-07-30', '{"email": "charlie@example.com"}', 'ATTENDEE'),
('davis_m', 'pass123', 'Mary Davis', '1985-03-12', '{"email": "mary@example.com"}', 'ATTENDEE'),
('ejohnson', 'pass123', 'Edward Johnson', '1991-09-05', '{"email": "ed@example.com"}', 'ATTENDEE'),
('fgarcia', 'pass123', 'Fiona Garcia', '1994-12-01', '{"email": "fiona@example.com"}', 'ATTENDEE'),
('gmiller', 'pass123', 'George Miller', '1987-06-18', '{"email": "george@example.com"}', 'ATTENDEE'),
('hlee', 'pass123', 'Hannah Lee', '1993-01-25', '{"email": "hannah@example.com"}', 'ATTENDEE'),
('iwhite', 'pass123', 'Ian White', '1989-10-14', '{"email": "ian@example.com"}', 'ATTENDEE'),
('pro_speaker1', 'pass123', 'Dr. Sarah Connor', '1980-04-20', '{"bio": "AI Specialist"}', 'PRESENTER'),
('pro_speaker2', 'pass123', 'James Bond', '1975-01-01', '{"bio": "Security Expert"}', 'PRESENTER'),
('pro_speaker3', 'pass123', 'Ada Lovelace', '1982-12-10', '{"bio": "Software Architect"}', 'PRESENTER'),
('pro_speaker4', 'pass123', 'Grace Hopper', '1984-08-15', '{"bio": "Systems Specialist"}', 'PRESENTER'),
('pro_speaker5', 'pass123', 'Alan Turing', '1981-06-23', '{"bio": "Cryptography Pro"}', 'PRESENTER'),
('admin_eve', 'pass123', 'Eve Admin', '1986-11-30', '{"email": "eve@event.com"}', 'EVENT_ADMIN'),
('admin_mallory', 'pass123', 'Mallory Admin', '1983-02-14', '{"email": "mallory@event.com"}', 'EVENT_ADMIN'),
('admin_trent', 'pass123', 'Trent Admin', '1990-03-03', '{"email": "trent@event.com"}', 'EVENT_ADMIN'),
('sys_root', 'pass123', 'Root User', '1970-01-01', '{"email": "root@system.com"}', 'SYSTEM_ADMIN'),
('sys_dev', 'pass123', 'Dev Ops', '1992-05-05', '{"email": "dev@system.com"}', 'SYSTEM_ADMIN');

-- 2. ATTENDEE DATA
INSERT INTO attendee (person_id, history)
SELECT id, '{"joined_events": []}' FROM person WHERE role = 'ATTENDEE';

-- 3. PRESENTER DATA
INSERT INTO presenter (person_id, presenter_role, statistics)
SELECT id, 'Guest Speaker', '{"rating": 5.0}' FROM person WHERE role = 'PRESENTER';

-- 4. EVENT DATA (20 Records)
INSERT INTO event (name, type, start_date, end_date, location, duration, status) VALUES
('TechConf 2026', 'CONFERENCE', '2026-03-01', '2026-03-03', 'San Francisco', 3, 'SCHEDULED'),
('Design Summit', 'WORKSHOP', '2026-04-10', '2026-04-11', 'New York', 2, 'SCHEDULED'),
('AI Expo', 'EXHIBITION', '2026-05-20', '2026-05-22', 'London', 3, 'SCHEDULED'),
('Cyber Security Forum', 'CONFERENCE', '2026-06-15', '2026-06-16', 'Berlin', 2, 'SCHEDULED'),
('HealthTech 2026', 'CONFERENCE', '2026-07-01', '2026-07-02', 'Tokyo', 2, 'SCHEDULED'),
('Green Energy Meet', 'WORKSHOP', '2026-08-05', '2026-08-05', 'Oslo', 1, 'SCHEDULED'),
('FinTech Days', 'CONCERT', '2026-09-10', '2026-09-12', 'Singapore', 3, 'SCHEDULED'),
('Mobile Dev Week', 'WORKSHOP', '2026-10-01', '2026-10-05', 'Austin', 5, 'SCHEDULED'),
('Cloud Native Con', 'CONFERENCE', '2026-11-12', '2026-11-14', 'Seattle', 3, 'SCHEDULED'),
('Game Dev Gala', 'EXHIBITION', '2026-12-01', '2026-12-03', 'Los Angeles', 3, 'SCHEDULED'),
('Open Source Summit', 'CONFERENCE', '2026-01-15', '2026-01-17', 'Paris', 3, 'COMPLETED'),
('Blockchain Blast', 'WORKSHOP', '2026-02-10', '2026-02-10', 'Dubai', 1, 'COMPLETED'),
('UX Masters', 'WORKSHOP', '2026-03-20', '2026-03-21', 'Toronto', 2, 'SCHEDULED'),
('Data Science Day', 'CONFERENCE', '2026-04-05', '2026-04-05', 'Boston', 1, 'SCHEDULED'),
('E-commerce Expo', 'EXHIBITION', '2026-05-12', '2026-05-13', 'Madrid', 2, 'SCHEDULED'),
('IoT World', 'CONFERENCE', '2026-06-25', '2026-06-27', 'Seoul', 3, 'SCHEDULED'),
('Robotics Rally', 'WORKSHOP', '2026-07-15', '2026-07-17', 'Pittsburgh', 3, 'SCHEDULED'),
('Future of Food', 'EXHIBITION', '2026-08-20', '2026-08-21', 'Amsterdam', 2, 'SCHEDULED'),
('Space Explorers', 'CONFERENCE', '2026-09-05', '2026-09-05', 'Houston', 1, 'SCHEDULED'),
('LegalTech Meetup', 'CONFERENCE', '2026-10-10', '2026-10-11', 'Sydney', 2, 'SCHEDULED');

-- 5. SESSION DATA
INSERT INTO session (
    event_id,
    title,
    description,
    start_time,
    end_time,
    venue,
    capacity
)
SELECT
    event_id,
    'Session Title ' || event_id,
    'Detailed description for session ' || event_id,
    start_date + TIME '09:00:00',
    start_date + TIME '11:00:00',
    'Room ' || event_id,
    50
FROM event;


-- 6. SESSION MATERIAL
INSERT INTO session_material (session_id, title, description, file_type, content_url)
SELECT session_id, 'Resource ' || session_id, 'PDF Notes', 'PDF', 'http://example.com' FROM session;

-- 7. SESSION PRESENTER (Maps the 5 presenters to 20 sessions)
INSERT INTO session_presenter (session_id, presenter_id)
SELECT s.session_id, p.person_id
FROM session s, presenter p
WHERE p.person_id = (SELECT person_id FROM presenter OFFSET (s.session_id % 5) LIMIT 1);

-- 8. TICKET DATA
INSERT INTO ticket (
    attendee_id,
    event_id,
    session_id,
    type,
    price,
    status,
    qr_code_data
)
VALUES
(1, 4, 4, 'EARLYBIRD', 35.00, 'ACTIVE', '{"ticketId":1,"attendeeId":1,"eventId":4,"sessionId":4}'),
(2, 4, 4, 'EARLYBIRD', 35.00, 'ACTIVE', '{"ticketId":2,"attendeeId":2,"eventId":4,"sessionId":4}'),
(3, 4, 4, 'EARLYBIRD', 35.00, 'ACTIVE', '{"ticketId":3,"attendeeId":3,"eventId":4,"sessionId":4}'),
(4, 4, 4, 'EARLYBIRD', 35.00, 'ACTIVE', '{"ticketId":4,"attendeeId":4,"eventId":4,"sessionId":4}'),
(5, 4, 4, 'GENERAL', 50.00, 'ACTIVE', '{"ticketId":5,"attendeeId":5,"eventId":4,"sessionId":4}'),
(6, 4, 4, 'GENERAL', 50.00, 'ACTIVE', '{"ticketId":6,"attendeeId":6,"eventId":4,"sessionId":4}'),
(7, 4, 4, 'GENERAL', 50.00, 'ACTIVE', '{"ticketId":7,"attendeeId":7,"eventId":4,"sessionId":4}'),
(8, 4, 4, 'GENERAL', 50.00, 'ACTIVE', '{"ticketId":8,"attendeeId":8,"eventId":4,"sessionId":4}'),
(9, 4, 4, 'GENERAL', 50.00, 'ACTIVE', '{"ticketId":9,"attendeeId":9,"eventId":4,"sessionId":4}'),
(10, 4, 4, 'GENERAL', 50.00, 'ACTIVE', '{"ticketId":10,"attendeeId":10,"eventId":4,"sessionId":4}'),
(11, 4, 4, 'VIP', 120.00, 'ACTIVE', '{"ticketId":11,"attendeeId":11,"eventId":4,"sessionId":4}'),
(12, 4, 4, 'VIP', 120.00, 'ACTIVE', '{"ticketId":12,"attendeeId":12,"eventId":4,"sessionId":4}'),
(13, 4, 4, 'VIP', 120.00, 'ACTIVE', '{"ticketId":13,"attendeeId":13,"eventId":4,"sessionId":4}'),
(14, 4, 4, 'VIP', 120.00, 'ACTIVE', '{"ticketId":14,"attendeeId":14,"eventId":4,"sessionId":4}'),
(15, 4, 4, 'EARLYBIRD', 35.00, 'ACTIVE', '{"ticketId":15,"attendeeId":15,"eventId":4,"sessionId":4}'),
(16, 4, 4, 'GENERAL', 50.00, 'ACTIVE', '{"ticketId":16,"attendeeId":16,"eventId":4,"sessionId":4}'),
(17, 4, 4, 'VIP', 120.00, 'ACTIVE', '{"ticketId":17,"attendeeId":17,"eventId":4,"sessionId":4}'),
(18, 4, 4, 'GENERAL', 50.00, 'ACTIVE', '{"ticketId":18,"attendeeId":18,"eventId":4,"sessionId":4}'),
(19, 4, 4, 'EARLYBIRD', 35.00, 'ACTIVE', '{"ticketId":19,"attendeeId":19,"eventId":4,"sessionId":4}'),
(20, 4, 4, 'VIP', 120.00, 'ACTIVE', '{"ticketId":20,"attendeeId":20,"eventId":4,"sessionId":4}');

-- Tickets for Session 20 (pro_speaker1) - 6 attendees
INSERT INTO ticket (attendee_id, event_id, session_id, type, price, status, qr_code_data) VALUES
(4, 20, 20, 'GENERAL', 50.00, 'ACTIVE', '{"ticketId":26,"attendeeId":4,"eventId":20,"sessionId":20}'),
(5, 20, 20, 'VIP', 120.00, 'USED', '{"ticketId":27,"attendeeId":5,"eventId":20,"sessionId":20}'),
(6, 20, 20, 'EARLYBIRD', 35.00, 'ACTIVE', '{"ticketId":28,"attendeeId":6,"eventId":20,"sessionId":20}'),
(7, 20, 20, 'GENERAL', 50.00, 'ACTIVE', '{"ticketId":29,"attendeeId":7,"eventId":20,"sessionId":20}'),
(8, 20, 20, 'VIP', 120.00, 'USED', '{"ticketId":30,"attendeeId":8,"eventId":20,"sessionId":20}'),
(9, 20, 20, 'GENERAL', 50.00, 'ACTIVE', '{"ticketId":31,"attendeeId":9,"eventId":20,"sessionId":20}');

-- 9. SCHEDULE ENTRY
INSERT INTO schedule_entry (person_id, session_id, start_time, end_time)
SELECT
    (SELECT id FROM person OFFSET (i % 20) LIMIT 1),
    (SELECT session_id FROM session OFFSET (i % 20) LIMIT 1),
    '2026-01-01 09:00:00',
    '2026-01-01 11:00:00'
FROM generate_series(1, 20) AS i;

-- 10. AUDIT LOG
INSERT INTO audit_log (user_id, operation_type, details)
SELECT
    (SELECT id FROM person OFFSET (i % 20) LIMIT 1),
    'DATA_SEED',
    '{"status": "initialized"}'
FROM generate_series(1, 20) AS i;

COMMIT;