CREATE TABLE challenge
(
    id            bigserial,
    name          VARCHAR(127),
    description   VARCHAR(511),
    stat_category VARCHAR(127),
    goal          Int,
    reward        Int,
    PRIMARY KEY (id)
);

CREATE TABLE user_challenge
(
    challenge_id     bigint,
    app_user         VARCHAR(127),
    current_progress Int,
    is_reward_claimed bool,

    PRIMARY KEY (challenge_id, app_user),
    CONSTRAINT user_challenge_challenge_fk
        FOREIGN KEY (challenge_id)
            REFERENCES challenge (id),
    CONSTRAINT user_challenge_user_fk
        FOREIGN KEY (app_user)
            REFERENCES app_user (username)
);

insert into challenge(name, description, stat_category, goal, reward)
values ('Beginner Reader',
        'Read to a specific amount of emails to complete',
        'READ',
        20,
        20);

insert into challenge(name, description, stat_category, goal, reward)
values ('Beginner Label',
        'Label to a specific amount of emails to complete',
        'LABEL',
        10,
        20);

insert into challenge(name, description, stat_category, goal, reward)
values ('Beginner Reply',
        'Reply to a specific amount of emails to complete',
        'REPLY',
        10,
        20);
