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

DROP TYPE IF EXISTS user_role CASCADE;
DROP TYPE IF EXISTS event_status CASCADE;
DROP TYPE IF EXISTS ticket_status CASCADE;

CREATE TYPE user_role AS ENUM (
   'ATTENDEE',
   'PRESENTER',
   'EVENT_ADMIN',
   'SYSTEM_ADMIN'
);

CREATE TYPE event_status AS ENUM ('SCHEDULED', 'ONGOING', 'COMPLETED', 'CANCELLED');

CREATE TYPE ticket_status AS ENUM ('ACTIVE', 'USED', 'CANCELLED');

CREATE TABLE person (
   id SERIAL PRIMARY KEY,
   username VARCHAR(100) UNIQUE NOT NULL,
   password VARCHAR(255) NOT NULL,
   full_name VARCHAR(255) NOT NULL,
   date_of_birth DATE,
   contact_information JSONB,
   role user_role NOT NULL
);

CREATE TABLE attendee (
   person_id INT PRIMARY KEY,
   history JSONB,
   FOREIGN KEY (person_id) REFERENCES person (id) ON DELETE CASCADE
);

CREATE TABLE presenter (
   person_id INT PRIMARY KEY,
   presenter_role VARCHAR(64),
   statistics JSONB,
   FOREIGN KEY (person_id) REFERENCES person (id) ON DELETE CASCADE
);

CREATE TABLE event (
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

CREATE TABLE session (
   session_id SERIAL PRIMARY KEY,
   event_id INT NOT NULL,
   title VARCHAR(255) NOT NULL,
   description TEXT NOT NULL,
   scheduled_date DATE NOT NULL,
   start_time TIME NOT NULL,
   end_time TIME NOT NULL,
   venue VARCHAR(100),
   capacity INT,
   FOREIGN KEY (event_id) REFERENCES event (event_id) ON DELETE CASCADE
);

CREATE TABLE session_material (
   material_id SERIAL PRIMARY KEY,
   session_id INT NOT NULL,
   title VARCHAR(255) NOT NULL,
   description TEXT,
   file_type VARCHAR(50),
   content_data BYTEA,
   content_url TEXT,
   FOREIGN KEY (session_id) REFERENCES session (session_id) ON DELETE CASCADE
);

CREATE TABLE session_presenter (
   session_id INT NOT NULL,
   presenter_id INT NOT NULL,
   PRIMARY KEY (session_id, presenter_id),
   FOREIGN KEY (session_id) REFERENCES session (session_id) ON DELETE CASCADE,
   FOREIGN KEY (presenter_id) REFERENCES presenter (person_id)
);

CREATE TABLE ticket (
   ticket_id SERIAL PRIMARY KEY,
   attendee_id INT NOT NULL,
   event_id INT NOT NULL,
   session_id INT,
   type VARCHAR(100) NOT NULL,
   price NUMERIC(10, 2) NOT NULL,
   status ticket_status NOT NULL,
   qr_code_data TEXT NOT NULL,
   FOREIGN KEY (attendee_id) REFERENCES attendee (person_id) ON DELETE CASCADE,
   FOREIGN KEY (event_id) REFERENCES event (event_id) ON DELETE CASCADE,
   FOREIGN KEY (session_id) REFERENCES session (session_id) ON DELETE CASCADE
);

CREATE TABLE schedule_entry (
   schedule_id SERIAL PRIMARY KEY,
   person_id INT NOT NULL,
   session_id INT NOT NULL,
   start_time TIMESTAMP NOT NULL,
   end_time TIMESTAMP NOT NULL,
   FOREIGN KEY (person_id) REFERENCES person (id) ON DELETE CASCADE,
   FOREIGN KEY (session_id) REFERENCES session (session_id) ON DELETE CASCADE
);

CREATE TABLE audit_log (
   log_id BIGSERIAL PRIMARY KEY,
   timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
   user_id INT,
   operation_type VARCHAR(100) NOT NULL,
   details JSONB,
   FOREIGN KEY (user_id) REFERENCES person (id) ON DELETE SET NULL
);
