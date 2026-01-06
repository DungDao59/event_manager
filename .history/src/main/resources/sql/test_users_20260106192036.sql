-- Test Users for Authentication Testing
-- Password for all users: "password123"
-- SHA-256 hash of "password123" = ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f

-- Insert a test System Admin
INSERT INTO person (username, password, full_name, date_of_birth, contact_information, role)
VALUES (
    'admin',
    'Khoai@12345',
    'Test Admin',
    '1990-01-01',
    '{"email": "admin@test.com"}',
    'SYSTEM_ADMIN'
) ON CONFLICT (username) DO UPDATE SET password = 'Khoai@12345';

-- Insert a test Event Admin
INSERT INTO person (username, password, full_name, date_of_birth, contact_information, role)
VALUES (
    'eventadmin',
    'Tuan@1234',
    'Test Event Admin',
    '1992-05-15',
    '{"email": "eventadmin@test.com"}',
    'EVENT_ADMIN'
) ON CONFLICT (username) DO UPDATE SET password = 'Tuan@1234';

-- Insert a test Presenter
INSERT INTO presenter (username, password, full_name, date_of_birth, contact_information, presenter_role, statistics)
VALUES (
    'presenter',
    '123a123',
    'Test Presenter',
    '1995-08-20',
    '{"email": "presenter@test.com"}',
    'Keynote Speaker',
    '{}'
) ON CONFLICT (username) DO UPDATE SET password = '123a123';

-- Insert a test Attendee
INSERT INTO attendee (username, password, full_name, date_of_birth, contact_information, attendance_history)
VALUES (
    'attendee',
    'Kyle@12345',
    'Test Attendee',
    '2000-03-10',
    '{"email": "attendee@test.com"}',
    '{}'
) ON CONFLICT (username) DO UPDATE SET password = 'Kyle@12345';
