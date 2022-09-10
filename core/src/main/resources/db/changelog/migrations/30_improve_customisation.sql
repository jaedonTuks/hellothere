alter table app_user
    add column title VARCHAR(127) default 'Noob';

alter table challenge
    add column title VARCHAR(127);

alter table challenge
    add column level VARCHAR(127);

UPDATE challenge
SET name          = 'Rookie Labeler',
    description   = 'Label 10 emails and climb the leaderboards!',
    stat_category = 'LABEL',
    goal          = 10,
    reward        = 20,
    title         = 'Rookie Labeler',
    level         = 'Rookie'
WHERE id = 2;

UPDATE challenge
SET name          = 'Rookie Reply',
    description   = 'Reply to your emails and gain more XP!',
    stat_category = 'REPLY',
    goal          = 10,
    reward        = 20,
    title         = 'Beginner Reply',
    level         = 'Rookie'
WHERE id = 3;

UPDATE challenge
SET name          = 'Rookie Reader',
    description   = 'Read 20 emails to complete this challenge and claim your prize!',
    stat_category = 'READ',
    goal          = 10,
    reward        = 20,
    title         = 'Rookie Reader',
    level         = 'Rookie'
WHERE id = 1;
