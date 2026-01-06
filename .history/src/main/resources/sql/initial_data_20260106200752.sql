SET
    time zone 'UTC';

INSERT INTO
    person (
        id,
        username,
        password,
        full_name,
        date_of_birth,
        contact_information,
        role
    )
VALUES
    (
        1,
        'sysadmin_a',
        'hash_sys_a',
        'Dr. Sarah System',
        '1980-01-01',
        '{"email":"sys1@corp.com"}',
        'SYSTEM_ADMIN'
    ),
    (
        2,
        'eventadmin_a',
        'hash_evn_a',
        'Elias Event Planner',
        '1990-02-02',
        '{"email":"evn1@corp.com"}',
        'EVENT_ADMIN'
    ),
    (
        3,
        'p_smith',
        'hash_p_sm',
        'Alice Smith',
        '1985-03-03',
        '{"email":"a.smith@pres.com"}',
        'PRESENTER'
    ),
    (
        4,
        'p_jones',
        'hash_p_jo',
        'Ben Jones',
        '1975-04-04',
        '{"email":"b.jones@pres.com"}',
        'PRESENTER'
    ),
    (
        5,
        'p_kim',
        'hash_p_ki',
        'Chloe Kim',
        '1995-05-05',
        '{"email":"c.kim@pres.com"}',
        'PRESENTER'
    ),
    (
        6,
        'p_data',
        'hash_p_dat',
        'David Data',
        '1988-06-06',
        '{"email":"d.data@pres.com"}',
        'PRESENTER'
    ),
    (
        7,
        'p_ui',
        'hash_p_ui',
        'Emily UI',
        '1998-07-07',
        '{"email":"e.ui@pres.com"}',
        'PRESENTER'
    ),
    (
        8,
        'a_dawn',
        'hash_a_da',
        'Fiona Dawn',
        '2000-08-08',
        '{"email":"f.dawn@att.com"}',
        'ATTENDEE'
    ),
    (
        9,
        'a_greg',
        'hash_a_gr',
        'Greg Hall',
        '2001-09-09',
        '{"email":"g.hall@att.com"}',
        'ATTENDEE'
    ),
    (
        10,
        'a_ian',
        'hash_a_ia',
        'Ian Jack',
        '2002-10-10',
        '{"email":"i.jack@att.com"}',
        'ATTENDEE'
    ),
    (
        11,
        'a_kelly',
        'hash_a_ke',
        'Kelly Lane',
        '2003-11-11',
        '{"email":"k.lane@att.com"}',
        'ATTENDEE'
    ),
    (
        12,
        'a_mike',
        'hash_a_mi',
        'Mike North',
        '2004-12-12',
        '{"email":"m.north@att.com"}',
        'ATTENDEE'
    ),
    (
        13,
        'a_olivia',
        'hash_a_ol',
        'Olivia Page',
        '2005-01-13',
        '{"email":"o.page@att.com"}',
        'ATTENDEE'
    ),
    (
        14,
        'a_ryan',
        'hash_a_ry',
        'Ryan Quill',
        '2006-02-14',
        '{"email":"r.quill@att.com"}',
        'ATTENDEE'
    ),
    (
        15,
        'a_sara',
        'hash_a_sa',
        'Sara Turk',
        '2007-03-15',
        '{"email":"s.turk@att.com"}',
        'ATTENDEE'
    ),
    (
        16,
        'a_victor',
        'hash_a_vi',
        'Victor Wolf',
        '2008-04-16',
        '{"email":"v.wolf@att.com"}',
        'ATTENDEE'
    ),
    (
        17,
        'a_yara',
        'hash_a_ya',
        'Yara Xylos',
        '2009-05-17',
        '{"email":"y.xylos@att.com"}',
        'ATTENDEE'
    ),
    (
        18,
        'a_zane',
        'hash_a_za',
        'Zane Yu',
        '2010-06-18',
        '{"email":"z.yu@att.com"}',
        'ATTENDEE'
    ),
    (
        19,
        'a_extra1',
        'hash_a_e1',
        'Extra Att 1',
        '2011-07-19',
        '{"email":"e1@att.com"}',
        'ATTENDEE'
    ),
    (
        20,
        'a_extra2',
        'hash_a_e2',
        'Extra Att 2',
        '2012-08-20',
        '{"email":"e2@att.com"}',
        'ATTENDEE'
    );

INSERT INTO
    attendee (person_id, history)
VALUES
    (8, '{"registered":5,"type":"Gold"}'),
    (9, '{"registered":3,"type":"Silver"}'),
    (10, '{"registered":7,"type":"Gold"}'),
    (11, '{"registered":2,"type":"Bronze"}'),
    (12, '{"registered":4,"type":"Silver"}'),
    (13, '{"registered":1,"type":"Bronze"}'),
    (14, '{"registered":6,"type":"Gold"}'),
    (15, '{"registered":8,"type":"Silver"}'),
    (16, '{"registered":3,"type":"Bronze"}'),
    (17, '{"registered":9,"type":"Gold"}'),
    (18, '{"registered":1,"type":"Bronze"}'),
    (19, '{"registered":4,"type":"Silver"}'),
    (20, '{"registered":2,"type":"Bronze"}');

INSERT INTO
    presenter (person_id, presenter_role, statistics)
VALUES
    (
        3,
        'Keynote Speaker',
        '{"sessions_total":5,"avg_audience":300}'
    ),
    (
        4,
        'Panel Moderator',
        '{"sessions_total":4,"avg_audience":150}'
    ),
    (
        5,
        'Senior Trainer',
        '{"sessions_total":6,"avg_audience":50}'
    ),
    (
        6,
        'Data Scientist',
        '{"sessions_total":2,"avg_audience":80}'
    ),
    (
        7,
        'UX Designer',
        '{"sessions_total":3,"avg_audience":70}'
    );

INSERT INTO
    event (
        event_id,
        name,
        type,
        start_date,
        end_date,
        location,
        duration,
        status,
        event_image
    )
VALUES
    (
        101,
        'Global Tech Conference',
        'Conference',
        '2026-04-10',
        '2026-04-12',
        'City Center',
        2880,
        'SCHEDULED',
        'img_e101'
    ),
    (
        102,
        'Startup Founders Summit',
        'Summit',
        '2026-05-01',
        '2026-05-02',
        'Innovation Hub',
        1440,
        'SCHEDULED',
        'img_e102'
    ),
    (
        103,
        'JavaFX GUI Workshop',
        'Workshop',
        '2026-06-05',
        '2026-06-05',
        'Online',
        480,
        'SCHEDULED',
        'img_e103'
    ),
    (
        104,
        'Advanced Database Seminar',
        'Seminar',
        '2026-07-15',
        '2026-07-15',
        'University Hall',
        240,
        'SCHEDULED',
        'img_e104'
    ),
    (
        105,
        'Cybersecurity Expo',
        'Expo',
        '2026-08-20',
        '2026-08-21',
        'Convention Hall B',
        1000,
        'SCHEDULED',
        'img_e105'
    ),
    (
        106,
        'Agile Project Management',
        'Training',
        '2026-09-01',
        '2026-09-03',
        'Office Suite',
        1800,
        'SCHEDULED',
        'img_e106'
    ),
    (
        107,
        'Cloud Computing Basics',
        'Webinar',
        '2026-10-10',
        NULL,
        'Online',
        120,
        'SCHEDULED',
        'img_e107'
    ),
    (
        108,
        'UX Design Bootcamp',
        'Bootcamp',
        '2026-11-05',
        '2026-11-07',
        'Design Studio',
        2000,
        'SCHEDULED',
        'img_e108'
    ),
    (
        109,
        'Financial Planning Meeting',
        'Internal',
        '2026-12-01',
        NULL,
        'HQ Boardroom',
        60,
        'SCHEDULED',
        'img_e109'
    ),
    (
        110,
        'Networking Mixer',
        'Social',
        '2026-12-31',
        NULL,
        'Rooftop Bar',
        180,
        'SCHEDULED',
        'img_e110'
    ),
    (
        111,
        'Q1 Product Review',
        'Internal',
        '2025-03-01',
        NULL,
        'HQ Boardroom',
        90,
        'COMPLETED',
        'img_e111'
    ),
    (
        112,
        'Big Data Conference',
        'Conference',
        '2025-05-15',
        '2025-05-17',
        'City Center',
        2880,
        'COMPLETED',
        'img_e112'
    ),
    (
        113,
        'Frontend Frameworks',
        'Workshop',
        '2025-07-20',
        NULL,
        'Online',
        300,
        'COMPLETED',
        'img_e113'
    ),
    (
        114,
        'Hiring Fair',
        'Recruitment',
        '2025-09-01',
        NULL,
        'Job Center',
        360,
        'COMPLETED',
        'img_e114'
    ),
    (
        115,
        'Cancelled Sales Pitch',
        'Marketing',
        '2026-01-01',
        NULL,
        'Theater 1',
        60,
        'CANCELLED',
        'img_e115'
    ),
    (
        116,
        'Cancelled Partnership Event',
        'Social',
        '2026-02-01',
        NULL,
        'Club Hall',
        120,
        'CANCELLED',
        'img_e116'
    ),
    (
        117,
        'Ongoing Beta Testing',
        'Internal',
        '2026-03-20',
        '2026-04-20',
        'Remote',
        10080,
        'ONGOING',
        'img_e117'
    ),
    (
        118,
        'The Great Hackathon',
        'Competition',
        '2026-04-05',
        '2026-04-06',
        'Main Campus',
        1800,
        'ONGOING',
        'img_e118'
    ),
    (
        119,
        'Project X Kickoff',
        'Internal',
        '2026-01-05',
        NULL,
        'Room 101',
        120,
        'COMPLETED',
        'img_e119'
    ),
    (
        120,
        'Summer Party',
        'Social',
        '2025-08-15',
        NULL,
        'Beach Venue',
        360,
        'COMPLETED',
        'img_e120'
    );

INSERT INTO
    session (
        session_id,
        event_id,
        title,
        description,
        scheduled_date,
        start_time,
        end_time,
        venue,
        capacity
    )
VALUES
    (
        201,
        101,
        'Keynote: Future of Coding',
        'AI and Developer Tools.',
        '2026-04-10',
        '09:00',
        '10:00',
        'Main Stage',
        500
    ),
    (
        202,
        101,
        'Serverless Architecture Panel',
        'AWS vs Azure vs GCP.',
        '2026-04-10',
        '10:30',
        '11:30',
        'Room Alpha',
        150
    ),
    (
        203,
        101,
        'Closing Remarks',
        'Wrap up of the conference.',
        '2026-04-12',
        '16:00',
        '17:00',
        'Main Stage',
        500
    ),
    (
        204,
        102,
        'Funding Your Startup',
        'Advice from VCs.',
        '2026-05-01',
        '14:00',
        '15:30',
        'Room Beta',
        100
    ),
    (
        205,
        102,
        'Scaling Team Culture',
        'Maintaining values as you grow.',
        '2026-05-02',
        '09:00',
        '10:30',
        'Room Gamma',
        80
    ),
    (
        206,
        103,
        'Intro to JavaFX Layouts',
        'Building the initial UI.',
        '2026-06-05',
        '09:00',
        '11:00',
        'Zoom Link 1',
        50
    ),
    (
        207,
        103,
        'FXMLLoader & Controllers',
        'Connecting logic and UI.',
        '2026-06-05',
        '11:30',
        '13:30',
        'Zoom Link 1',
        50
    ),
    (
        208,
        103,
        'Database Integration Demo',
        'JDBC and FX.',
        '2026-06-05',
        '14:30',
        '16:30',
        'Zoom Link 1',
        50
    ),
    (
        209,
        104,
        'PostgreSQL Performance Tuning',
        'Optimizing queries.',
        '2026-07-15',
        '10:00',
        '14:00',
        'Lab 1A',
        40
    ),
    (
        210,
        105,
        'Threat Landscape 2026',
        'Key threats.',
        '2026-08-20',
        '09:00',
        '10:30',
        'Theater 1',
        200
    ),
    (
        211,
        105,
        'Zero-Trust Architectures',
        'Implementation strategies.',
        '2026-08-20',
        '11:00',
        '12:30',
        'Theater 1',
        200
    ),
    (
        212,
        106,
        'Scrum Fundamentals',
        'Roles and ceremonies.',
        '2026-09-01',
        '09:00',
        '12:00',
        'Room 201',
        30
    ),
    (
        213,
        106,
        'Kanban and Flow',
        'Visualizing work.',
        '2026-09-02',
        '09:00',
        '12:00',
        'Room 201',
        30
    ),
    (
        214,
        106,
        'Scaling Agile (SAFe/LeSS)',
        'Managing large programs.',
        '2026-09-03',
        '09:00',
        '12:00',
        'Room 201',
        30
    ),
    (
        215,
        111,
        'Financial Review Q1',
        'Budget vs Actuals.',
        '2025-03-01',
        '09:00',
        '10:30',
        'Boardroom',
        20
    ),
    (
        216,
        111,
        'Next Quarter Goals',
        'Setting KPIs.',
        '2025-03-01',
        '10:30',
        '12:00',
        'Boardroom',
        20
    ),
    (
        217,
        118,
        'Hacking Begins',
        'Official start time.',
        '2026-04-05',
        '10:00',
        '11:00',
        'Main Hall',
        100
    ),
    (
        218,
        118,
        'Team Mentoring Hour',
        'Expert Q&A.',
        '2026-04-05',
        '15:00',
        '16:00',
        'Breakout Rooms',
        100
    ),
    (
        219,
        115,
        'Sales Strategy Presentation',
        'New market approach.',
        '2026-01-01',
        '10:00',
        '11:00',
        'Theater 1',
        50
    );

INSERT INTO
    session_presenter (session_id, presenter_id)
VALUES
    (201, 3),
    (202, 4),
    (202, 5),
    (203, 3),
    (204, 4),
    (205, 5),
    (206, 5),
    (207, 7),
    (208, 6),
    (209, 6),
    (210, 3),
    (211, 4),
    (212, 5),
    (213, 5),
    (214, 4),
    (217, 3),
    (218, 7);

INSERT INTO
    session_material (
        material_id,
        session_id,
        title,
        description,
        file_type,
        content_data,
        content_url
    )
VALUES
    (
        301,
        201,
        'AI Keynote Slides',
        'Official presentation deck.',
        'PDF',
        decode ('201', 'hex'),
        NULL
    ),
    (
        302,
        201,
        'Keynote Recording',
        'Video of the session.',
        'VIDEO',
        NULL,
        'http://video.com/201'
    ),
    (
        303,
        202,
        'Serverless Cost Analysis',
        'Comparative pricing paper.',
        'DOCX',
        decode ('202', 'hex'),
        NULL
    ),
    (
        304,
        202,
        'Panelist Contact Info',
        'Contact details for networking.',
        'TEXT',
        decode ('203', 'hex'),
        NULL
    ),
    (
        305,
        203,
        'Thank You Note',
        'Closing message.',
        'PDF',
        decode ('204', 'hex'),
        NULL
    ),
    (
        306,
        204,
        'Startup Pitch Template',
        'Template for pitch deck.',
        'PPTX',
        decode ('205', 'hex'),
        NULL
    ),
    (
        307,
        205,
        'Team Culture Handbook',
        'Guide to scaling culture.',
        'PDF',
        decode ('206', 'hex'),
        NULL
    ),
    (
        308,
        206,
        'JavaFX Layout Code',
        'Initial layout code files.',
        'CODE',
        decode ('207', 'hex'),
        NULL
    ),
    (
        309,
        207,
        'Controller Example',
        'Sample FXML controller.',
        'CODE',
        decode ('208', 'hex'),
        NULL
    ),
    (
        310,
        208,
        'JDBC Utility Class',
        'The data access utility.',
        'CODE',
        decode ('209', 'hex'),
        NULL
    ),
    (
        311,
        209,
        'Query Optimization Guide',
        'PDF guide for performance.',
        'PDF',
        decode ('210', 'hex'),
        NULL
    ),
    (
        312,
        210,
        'Threat Report Summary',
        'Top findings.',
        'PDF',
        decode ('211', 'hex'),
        NULL
    ),
    (
        313,
        211,
        'Zero-Trust Diagram',
        'Architecture blueprint.',
        'IMAGE',
        decode ('212', 'hex'),
        NULL
    ),
    (
        314,
        212,
        'Scrum Guide Link',
        'Official external resource.',
        'LINK',
        NULL,
        'http://scrumguides.org'
    ),
    (
        315,
        213,
        'Kanban Board Template',
        'Trello/Jira template.',
        'LINK',
        NULL,
        'http://template.com/kanban'
    ),
    (
        316,
        214,
        'Scaling Whitepaper',
        'SAFe/LeSS comparison.',
        'PDF',
        decode ('213', 'hex'),
        NULL
    ),
    (
        317,
        215,
        'Q1 P&L Spreadsheet',
        'Financial details.',
        'XLSX',
        decode ('214', 'hex'),
        NULL
    ),
    (
        318,
        216,
        'Next Quarter OKRs',
        'Objectives and Key Results.',
        'DOCX',
        decode ('215', 'hex'),
        NULL
    ),
    (
        319,
        217,
        'Hackathon Rules',
        'Rules and regulations.',
        'PDF',
        decode ('216', 'hex'),
        NULL
    ),
    (
        320,
        218,
        'Mentoring FAQs',
        'Common questions/answers.',
        'TEXT',
        decode ('217', 'hex'),
        NULL
    );

INSERT INTO
    ticket (
        ticket_id,
        attendee_id,
        event_id,
        session_id,
        type,
        price,
        status,
        qr_code_data
    )
VALUES
    (
        401,
        8,
        101,
        201,
        'VIP Pass',
        499.00,
        'ACTIVE',
        'TKT-401-E101'
    ),
    (
        402,
        9,
        101,
        202,
        'General',
        299.00,
        'ACTIVE',
        'TKT-402-E101'
    ),
    (
        403,
        10,
        101,
        203,
        'General',
        0.00,
        'CANCELLED',
        'TKT-403-E101'
    ),
    (
        404,
        11,
        101,
        NULL,
        'Event Pass',
        299.00,
        'ACTIVE',
        'TKT-404-E101'
    ),
    (
        405,
        12,
        101,
        202,
        'General',
        299.00,
        'USED',
        'TKT-405-E101'
    ),
    (
        406,
        8,
        103,
        206,
        'Workshop Day',
        99.00,
        'ACTIVE',
        'TKT-406-E103-S206'
    ),
    (
        407,
        9,
        103,
        207,
        'Workshop Day',
        0.00,
        'ACTIVE',
        'TKT-407-E103-S207'
    ),
    (
        408,
        10,
        103,
        208,
        'Workshop Day',
        99.00,
        'ACTIVE',
        'TKT-408-E103-S208'
    ),
    (
        409,
        13,
        103,
        206,
        'Workshop Day',
        99.00,
        'ACTIVE',
        'TKT-409-E103-S206'
    ),
    (
        410,
        14,
        103,
        207,
        'Workshop Day',
        99.00,
        'ACTIVE',
        'TKT-410-E103-S207'
    ),
    (
        411,
        15,
        106,
        212,
        'Full Course',
        199.00,
        'ACTIVE',
        'TKT-411-E106'
    ),
    (
        412,
        16,
        106,
        213,
        'Full Course',
        199.00,
        'ACTIVE',
        'TKT-412-E106'
    ),
    (
        413,
        17,
        106,
        214,
        'Full Course',
        199.00,
        'ACTIVE',
        'TKT-413-E106'
    ),
    (
        414,
        18,
        106,
        212,
        'Full Course',
        199.00,
        'ACTIVE',
        'TKT-414-E106'
    ),
    (
        415,
        19,
        106,
        213,
        'Full Course',
        199.00,
        'ACTIVE',
        'TKT-415-E106'
    ),
    (
        416,
        20,
        112,
        NULL,
        'Day 1 Pass',
        100.00,
        'USED',
        'TKT-416-E112'
    ),
    (
        417,
        8,
        114,
        NULL,
        'Visitor',
        0.00,
        'USED',
        'TKT-417-E114'
    ),
    (
        418,
        9,
        115,
        219,
        'Promo Ticket',
        0.00,
        'CANCELLED',
        'TKT-418-E115'
    ),
    (
        419,
        10,
        118,
        217,
        'Hacker Pass',
        50.00,
        'ACTIVE',
        'TKT-419-E118'
    ),
    (
        420,
        1,
        109,
        NULL,
        'Staff',
        0.00,
        'ACTIVE',
        'TKT-420-E109'
    );

INSERT INTO
    schedule_entry (person_id, session_id, start_time, end_time)
VALUES
    (8, 201, '2026-04-10 09:00', '2026-04-10 10:00'),
    (9, 202, '2026-04-10 10:30', '2026-04-10 11:30'),
    (8, 206, '2026-06-05 09:00', '2026-06-05 11:00'),
    (9, 207, '2026-06-05 11:30', '2026-06-05 13:30'),
    (10, 208, '2026-06-05 14:30', '2026-06-05 16:30'),
    (13, 206, '2026-06-05 09:00', '2026-06-05 11:00'),
    (14, 207, '2026-06-05 11:30', '2026-06-05 13:30'),
    (3, 201, '2026-04-10 09:00', '2026-04-10 10:00'),
    (4, 202, '2026-04-10 10:30', '2026-04-10 11:30'),
    (5, 205, '2026-05-02 09:00', '2026-05-02 10:30'),
    (5, 206, '2026-06-05 09:00', '2026-06-05 11:00'),
    (7, 207, '2026-06-05 11:30', '2026-06-05 13:30'),
    (6, 208, '2026-06-05 14:30', '2026-06-05 16:30'),
    (5, 212, '2026-09-01 09:00', '2026-09-01 12:00'),
    (4, 214, '2026-09-03 09:00', '2026-09-03 12:00'),
    (2, 216, '2025-03-01 10:30', '2025-03-01 12:00'),
    (3, 217, '2026-04-05 10:00', '2026-04-05 11:00'),
    (1, 215, '2025-03-01 09:00', '2025-03-01 10:30');

INSERT INTO
    audit_log (user_id, operation_type, details)
VALUES
    (
        1,
        'CREATE_PERSON',
        '{"person_id":10,"role":"ATTENDEE"}'
    ),
    (
        1,
        'UPDATE_PERSON_ROLE',
        '{"person_id":5,"old_role":"ATTENDEE","new_role":"PRESENTER"}'
    ),
    (
        2,
        'CREATE_EVENT',
        '{"event_id":104,"name":"Advanced Database Seminar"}'
    ),
    (
        2,
        'UPDATE_EVENT_STATUS',
        '{"event_id":115,"old_status":"SCHEDULED","new_status":"CANCELLED"}'
    ),
    (
        2,
        'UPDATE_SESSION',
        '{"session_id":201,"new_capacity":600}'
    ),
    (
        2,
        'CREATE_SESSION',
        '{"event_id":103,"session_id":206}'
    ),
    (
        2,
        'CREATE_TICKET',
        '{"ticket_id":401,"attendee_id":8}'
    ),
    (
        1,
        'DELETE_PERSON',
        '{"person_id":20,"name":"Extra Att 2"}'
    ),
    (
        2,
        'ASSIGN_PRESENTER',
        '{"session_id":202,"presenter_id":4}'
    ),
    (
        1,
        'CREATE_EVENT',
        '{"event_id":110,"name":"Networking Mixer"}'
    ),
    (
        2,
        'UPDATE_EVENT_LOCATION',
        '{"event_id":101,"new_location":"New Convention Hall"}'
    ),
    (
        2,
        'UPDATE_TICKET_STATUS',
        '{"ticket_id":405,"old_status":"ACTIVE","new_status":"USED"}'
    ),
    (
        1,
        'CREATE_PERSON',
        '{"person_id":1,"role":"SYSTEM_ADMIN"}'
    ),
    (
        2,
        'CREATE_SESSION',
        '{"event_id":106,"session_id":212}'
    ),
    (1, 'DELETE_PERSON', '{"person_id":19}'),
    (
        2,
        'UPDATE_SESSION',
        '{"session_id":208,"new_venue":"Online Room B"}'
    ),
    (1, 'CREATE_TICKET', '{"ticket_id":419}'),
    (2, 'DELETE_SESSION', '{"session_id":219}'),
    (1, 'SYSTEM_MAINTENANCE', '{"duration":"2 hours"}'),
    (
        2,
        'CREATE_SESSION_MATERIAL',
        '{"material_id":320,"session_id":218}'
    );