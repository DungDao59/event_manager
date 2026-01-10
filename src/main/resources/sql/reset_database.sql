-- =============================================
-- DATABASE RESET SCRIPT
-- Run this entire file to reset the database
-- =============================================

-- Step 1: Drop all existing tables
DROP TABLE IF EXISTS audit_log CASCADE;
DROP TABLE IF EXISTS schedule_entry CASCADE;
DROP TABLE IF EXISTS ticket CASCADE;
DROP TABLE IF EXISTS session_presenter CASCADE;
DROP TABLE IF EXISTS session_material CASCADE;
DROP TABLE IF EXISTS session CASCADE;
DROP TABLE IF EXISTS event CASCADE;
DROP TABLE IF EXISTS presenter CASCADE;
DROP TABLE IF EXISTS attendee CASCADE;
DROP TABLE IF EXISTS person CASCADE;

-- Step 2: Create Tables

-- PERSON TABLE
CREATE TABLE IF NOT EXISTS person (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    date_of_birth DATE,
    contact_information JSONB,
    role user_role NOT NULL
);

-- ATTENDEE TABLE
CREATE TABLE IF NOT EXISTS attendee (
    person_id INT PRIMARY KEY,
    history JSONB,
    FOREIGN KEY (person_id) REFERENCES person (id) ON DELETE CASCADE
);

-- PRESENTER TABLE
CREATE TABLE IF NOT EXISTS presenter (
    person_id INT PRIMARY KEY,
    presenter_role VARCHAR(64),
    statistics JSONB,
    FOREIGN KEY (person_id) REFERENCES person (id) ON DELETE CASCADE
);

-- EVENT TABLE
CREATE TABLE IF NOT EXISTS event (
    event_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(100),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    location VARCHAR(255) NOT NULL,
    duration INT,
    status event_status NOT NULL,
    event_image TEXT
);

-- SESSION TABLE
CREATE TABLE IF NOT EXISTS session (
    session_id SERIAL PRIMARY KEY,
    event_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    venue VARCHAR(100),
    capacity INT,
    FOREIGN KEY (event_id) REFERENCES event (event_id) ON DELETE CASCADE
);

-- SESSION MATERIAL TABLE
CREATE TABLE IF NOT EXISTS session_material (
    material_id SERIAL PRIMARY KEY,
    session_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    file_type VARCHAR(50),
    content_data BYTEA,
    content_url TEXT,
    FOREIGN KEY (session_id) REFERENCES session (session_id) ON DELETE CASCADE
);

-- SESSION PRESENTER TABLE
CREATE TABLE IF NOT EXISTS session_presenter (
    session_id INT NOT NULL,
    presenter_id INT NOT NULL,
    position INT DEFAULT 0,
    PRIMARY KEY (session_id, presenter_id),
    FOREIGN KEY (session_id) REFERENCES session (session_id) ON DELETE CASCADE,
    FOREIGN KEY (presenter_id) REFERENCES presenter (person_id) ON DELETE CASCADE
);

-- TICKET TABLE
CREATE TABLE IF NOT EXISTS ticket (
    ticket_id SERIAL PRIMARY KEY,
    session_id INT NOT NULL,
    attendee_id INT NOT NULL,
    price DECIMAL(10, 2) DEFAULT 0,
    status ticket_status NOT NULL DEFAULT 'ACTIVE',
    purchase_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES session (session_id) ON DELETE CASCADE,
    FOREIGN KEY (attendee_id) REFERENCES attendee (person_id) ON DELETE CASCADE
);

-- SCHEDULE ENTRY TABLE
CREATE TABLE IF NOT EXISTS schedule_entry (
    entry_id SERIAL PRIMARY KEY,
    attendee_id INT NOT NULL,
    session_id INT NOT NULL,
    reminder_time TIMESTAMP,
    notes TEXT,
    FOREIGN KEY (attendee_id) REFERENCES attendee (person_id) ON DELETE CASCADE,
    FOREIGN KEY (session_id) REFERENCES session (session_id) ON DELETE CASCADE
);

-- AUDIT LOG TABLE
CREATE TABLE IF NOT EXISTS audit_log (
    log_id SERIAL PRIMARY KEY,
    actor_id INT,
    action VARCHAR(255) NOT NULL,
    target_type VARCHAR(100),
    target_id INT,
    details JSONB,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (actor_id) REFERENCES person (id) ON DELETE SET NULL
);

-- =============================================
-- Step 3: Insert Initial Data
-- =============================================

-- 1. PERSON DATA (Admins)
INSERT INTO person (username, password, full_name, date_of_birth, contact_information, role) VALUES
('admin_mallory', 'hashed_password_1', 'Mallory Admin', '1985-03-15', '{"email": "mallory@example.com", "phone": "123-456-7890"}', 'SYSTEM_ADMIN'),
('event_admin_1', 'hashed_password_2', 'Event Admin One', '1990-07-22', '{"email": "eventadmin1@example.com"}', 'EVENT_ADMIN');

-- 2. ATTENDEES
INSERT INTO person (username, password, full_name, date_of_birth, contact_information, role) VALUES
('attendee_alice', 'hashed_password_3', 'Alice Attendee', '1995-01-10', '{"email": "alice@example.com"}', 'ATTENDEE'),
('attendee_bob', 'hashed_password_4', 'Bob Attendee', '1992-05-20', '{"email": "bob@example.com"}', 'ATTENDEE'),
('attendee_carol', 'hashed_password_5', 'Carol Attendee', '1998-11-30', '{"email": "carol@example.com"}', 'ATTENDEE');

INSERT INTO attendee (person_id, history)
SELECT id, '[]'::jsonb FROM person WHERE role = 'ATTENDEE';

-- 3. PRESENTERS
INSERT INTO person (username, password, full_name, date_of_birth, contact_information, role) VALUES
('presenter_dan', 'hashed_password_6', 'Dan Presenter', '1988-09-05', '{"email": "dan@example.com"}', 'PRESENTER'),
('presenter_eve', 'hashed_password_7', 'Eve Presenter', '1991-12-15', '{"email": "eve@example.com"}', 'PRESENTER');

INSERT INTO presenter (person_id, presenter_role, statistics)
SELECT id, 'Speaker', '{}'::jsonb FROM person WHERE role = 'PRESENTER';

-- 4. EVENT DATA (Using only CONFERENCE, WORKSHOP, CONCERT, EXHIBITION)
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
INSERT INTO session (event_id, title, description, start_time, end_time, venue, capacity)
SELECT
    event_id,
    'Session for ' || name,
    'Detailed description for session at ' || name,
    start_date + TIME '09:00:00',
    start_date + TIME '11:00:00',
    'Room ' || event_id,
    50
FROM event;

-- 6. ASSIGN PRESENTERS TO SESSIONS
INSERT INTO session_presenter (session_id, presenter_id, position)
SELECT s.session_id, p.person_id, 0
FROM session s
CROSS JOIN presenter p
WHERE s.session_id <= 10 AND p.person_id = (SELECT MIN(person_id) FROM presenter);

-- 7. SAMPLE TICKETS
INSERT INTO ticket (session_id, attendee_id, price, status)
SELECT s.session_id, a.person_id, 25.00, 'ACTIVE'
FROM session s
CROSS JOIN attendee a
WHERE s.session_id <= 5;

-- Done!
SELECT 'Database reset completed!' AS status;
