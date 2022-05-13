CREATE TABLE app_user
(
    username VARCHAR(127),
    rank     VARCHAR(127),

    PRIMARY KEY ("username")
);

INSERT INTO app_user (username, rank) VALUES ('testUser', 'NOOB')
