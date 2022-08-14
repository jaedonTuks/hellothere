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
values ('Rookie Reader',
        'Read 20 emails to complete this challenge and claim your prize!',
        'READ',
        20,
        20);

insert into challenge(name, description, stat_category, goal, reward)
values ('Rookie Labeler',
        'Label 10 emails and climb the leaderboards!',
        'LABEL',
        10,
        20);

insert into challenge(name, description, stat_category, goal, reward)
values ('Beginner Reply',
        'Reply to your emails and gain more XP!',
        'REPLY',
        10,
        20);

alter table user_week_stats add column challenge_week_stat_id bigint;
alter table user_week_stats add constraint ws_read_stat_fk foreign key (read_week_stat_id) references week_stats_category(id);
alter table user_week_stats add constraint ws_label_stat_fk foreign key (label_week_stat_id) references week_stats_category(id);
alter table user_week_stats add constraint ws_reply_stat_fk foreign key (reply_week_stat_id) references week_stats_category(id);
alter table user_week_stats add constraint ws_challenge_stat_fk foreign key (challenge_week_stat_id) references week_stats_category(id);
