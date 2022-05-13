CREATE TABLE app_user
(
    username         VARCHAR(127),
    rank             VARCHAR(127),
    total_experience INT DEFAULT 0,

    PRIMARY KEY ("username")
);

INSERT INTO app_user (username, rank)
VALUES ('testUser', 'NOOB');

CREATE TABLE user_week_stats
(
    id         bigserial not null,
    experience INT DEFAULT 0,
    start_date timestamp,
    end_date   timestamp,
    app_user   VARCHAR(127),
    PRIMARY KEY(id),
    CONSTRAINT user_fk
        FOREIGN KEY(app_user)
            REFERENCES app_user(username)
);


INSERT INTO user_week_stats (id, experience, start_date, end_date, app_user)
VALUES (1, 10, '2022-05-01 00:00:00.000000', '2022-05-07 00:00:00.000000', 'testUser');

INSERT INTO user_week_stats (id, experience, start_date, end_date, app_user)
VALUES (2, 25, '2022-05-08 00:00:00.000000', '2022-05-14 00:00:00.000000', 'testUser');
