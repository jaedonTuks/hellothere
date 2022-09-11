alter table app_user
    add column title VARCHAR(127) default 'Noob';

alter table challenge
    add column title VARCHAR(127);

alter table challenge
    add column level VARCHAR(127);

alter table challenge
    add column color VARCHAR(7);

UPDATE challenge
SET name          = 'Beginner Reader',
    description   = 'Read emails to complete this challenge and claim your prize!',
    stat_category = 'READ',
    goal          = 10,
    reward        = 20,
    title         = 'Beginner Reader',
    level         = 'Beginner',
    color         = '#0ACD8F'
WHERE id = 1;
UPDATE challenge
SET name          = 'Beginner Reply',
    description   = 'Reply to your emails and gain more XP!',
    stat_category = 'REPLY',
    goal          = 10,
    reward        = 20,
    title         = 'Beginner Reply',
    level         = 'Beginner',
    color         = '#E2A224'
WHERE id = 3;
UPDATE challenge
SET name          = 'Beginner Labeler',
    description   = 'Label emails and climb the leaderboards!',
    stat_category = 'LABEL',
    goal          = 10,
    reward        = 20,
    title         = 'Beginner Labeler',
    level         = 'Beginner',
    color         = '#0A86E5'
WHERE id = 2;


INSERT INTO challenge (id, name, description, stat_category, goal, reward, title, level, color)
VALUES (4, 'Apprentice Reader', 'Read emails to complete this challenge and claim your prize!', 'READ', 25, 30,
        'Apprentice Reader', 'Apprentice', '#0e9c4c');

INSERT INTO challenge (id, name, description, stat_category, goal, reward, title, level, color)
VALUES (6, 'Apprentice Labeler', 'Label emails and climb the leaderboards!', 'LABEL', 30, 30,
        'Apprentice Labeler', 'Apprentice', '#0d28bf');

INSERT INTO challenge (id, name, description, stat_category, goal, reward, title, level, color)
VALUES (5, 'Apprentice Reply', 'Reply to your emails and gain more XP!', 'REPLY', 20, 30, 'Apprentice Reply',
        'Apprentice',
        '#e27324');

INSERT INTO challenge (id, name, description, stat_category, goal, reward, title, level, color)
VALUES (7, 'Pro Reader', 'Read emails to complete this challenge and claim your prize!', 'READ', 50, 50,
        'Pro Reader', 'Pro', '#62bf7f');


INSERT INTO challenge (id, name, description, stat_category, goal, reward, title, level, color)
VALUES (9, 'Pro Labeler', 'Label emails and climb the leaderboards!', 'LABEL', 60, 50,
        'Pro Labeler', 'Pro', '#a70dbf');

INSERT INTO challenge (id, name, description, stat_category, goal, reward, title, level, color)
VALUES (8, 'Pro Reply', 'Reply to your emails and gain more XP!', 'REPLY', 35, 50, 'Pro Reply', 'Pro',
        '#b51919');

INSERT INTO challenge (id, name, description, stat_category, goal, reward, title, level, color)
VALUES (10, 'Legendary Reader', 'Read emails to complete this challenge and claim your prize!', 'READ', 100, 100,
        'Legendary Reader', 'Legendary', '#325e21');

INSERT INTO challenge (id, name, description, stat_category, goal, reward, title, level, color)
VALUES (12, 'Legendary Labeler', 'Label emails and climb the leaderboards!', 'LABEL', 120, 100,
        'Legendary Labeler', 'Legendary', '#5a0dbf');

INSERT INTO challenge (id, name, description, stat_category, goal, reward, title, level, color)
VALUES (11, 'Legendary Reply', 'Reply to your emails and gain more XP!', 'REPLY', 70, 100, 'Legendary Reply',
        'Legendary',
        '#963423');
