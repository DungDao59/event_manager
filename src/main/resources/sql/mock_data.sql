-- Simple Mock Data for Testing Event UI
-- Run this in your PostgreSQL database

-- Clear existing data
TRUNCATE TABLE schedule_entry, ticket, session_presenter, session, event, presenter, attendee, person RESTART IDENTITY CASCADE;

-- Insert basic events for testing
INSERT INTO event (name, type, start_date, end_date, location, duration, status, event_image) VALUES
('Tech Conference 2025', 'CONFERENCE', '2025-02-15 09:00:00', '2025-02-17 18:00:00', 'RMIT Hanoi Campus', 3, 'SCHEDULED', 'https://images.unsplash.com/photo-1540575467063-178a50c2df87'),
('JavaFX Workshop', 'WORKSHOP', '2025-01-20 09:00:00', '2025-01-20 16:00:00', 'Building A - Room 201', 1, 'SCHEDULED', 'https://images.unsplash.com/photo-1517694712202-14dd9538aa97'),
('AI Summit 2025', 'CONFERENCE', '2025-03-10 09:00:00', '2025-03-12 17:00:00', 'Hanoi Convention Center', 3, 'SCHEDULED', 'https://images.unsplash.com/photo-1485827404703-89b55fcc595e'),
('Networking Night', 'NETWORKING', '2025-01-30 18:00:00', '2025-01-30 21:00:00', 'Tech Hub - Floor 3', 1, 'SCHEDULED', 'https://images.unsplash.com/photo-1511578314322-379afb476865'),
('Spring Dev Conf 2025', 'CONFERENCE', '2025-04-05 09:00:00', '2025-04-07 17:00:00', 'RMIT Vietnam', 3, 'SCHEDULED', 'https://images.unsplash.com/photo-1591115765373-5207764f72e7'),
('Data Science Workshop', 'WORKSHOP', '2025-02-25 10:00:00', '2025-02-25 16:00:00', 'Lab Building - Room 305', 1, 'SCHEDULED', 'https://images.unsplash.com/photo-1551288049-bebda4e38f71'),
('Cancelled Event', 'SEMINAR', '2025-01-15 14:00:00', '2025-01-15 17:00:00', 'Room 101', 1, 'CANCELLED', NULL),
('Completed Workshop 2024', 'WORKSHOP', '2024-12-10 09:00:00', '2024-12-10 16:00:00', 'Training Room A', 1, 'COMPLETED', NULL);

-- Insert sessions for events
INSERT INTO session (event_id, title, description, start_time, end_time, venue, capacity) VALUES
(1, 'Opening Keynote', 'Future of Technology', '2025-02-15 09:00:00', '2025-02-15 10:30:00', 'Main Hall', 500),
(1, 'AI Workshop', 'Hands-on AI Development', '2025-02-15 11:00:00', '2025-02-15 13:00:00', 'Lab 1', 50),
(2, 'JavaFX Basics', 'Introduction to JavaFX', '2025-01-20 09:00:00', '2025-01-20 12:00:00', 'Room 201', 30),
(3, 'Machine Learning Track', 'Advanced ML Techniques', '2025-03-10 09:00:00', '2025-03-10 12:00:00', 'Hall A', 200),
(4, 'Speed Networking', 'Fast networking session', '2025-01-30 19:00:00', '2025-01-30 21:00:00', 'Main Area', 100);

-- Verify data
SELECT 'Mock data loaded!' as status;
SELECT COUNT(*) as total_events FROM event;
SELECT COUNT(*) as total_sessions FROM session;
