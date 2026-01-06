-- Test Users for Authentication Testing
-- All passwords are SHA-256 hashed
-- Use the plain text password when logging in

-- SHA-256 Hashes:
-- 'Khoai@12345' = 8d0d7a7b0c6d6e5f4a3b2c1d0e9f8a7b6c5d4e3f2a1b0c9d8e7f6a5b4c3d2e1f (placeholder - will be updated)
-- For simplicity, using 'password123' for all test users
-- SHA-256 of 'password123' = ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f

-- Insert a test System Admin (password: password123)
INSERT INTO person (username, password, full_name, date_of_birth, contact_information, role)
VALUES (
    'admin',
    'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f',
    'Test Admin',
    '1990-01-01',
    '{"email": "admin@test.com"}',
    'SYSTEM_ADMIN'
) ON CONFLICT (username) DO UPDATE SET password = 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f';

-- Insert a test Event Admin (password: password123)
INSERT INTO person (username, password, full_name, date_of_birth, contact_information, role)
VALUES (
    'eventadmin',
    'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f',
    'Test Event Admin',
    '1992-05-15',
    '{"email": "eventadmin@test.com"}',
    'EVENT_ADMIN'
) ON CONFLICT (username) DO UPDATE SET password = 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f';

-- Insert a test Presenter (password: password123)
INSERT INTO presenter (username, password, full_name, date_of_birth, contact_information, presenter_role, statistics)
VALUES (
    'presenter',
    'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f',
    'Test Presenter',
    '1995-08-20',
    '{"email": "presenter@test.com"}',
    'Keynote Speaker',
    '{}'
) ON CONFLICT (username) DO UPDATE SET password = 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f';

-- Insert a test Attendee (password: password123)
INSERT INTO attendee (username, password, full_name, date_of_birth, contact_information, attendance_history)
VALUES (
    'attendee',
    'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f',
    'Test Attendee',
    '2000-03-10',
    '{"email": "attendee@test.com"}',
    '{}'
) ON CONFLICT (username) DO UPDATE SET password = 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f';
