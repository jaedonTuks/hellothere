CREATE TABLE user_access_token
(
    id            VARCHAR(2047),
    token         VARCHAR(255),
    refresh_token VARCHAR(255),
    scope         VARCHAR(2047),
    expires       timestamp,
    app_user      VARCHAR(127),
    PRIMARY KEY (id),
    CONSTRAINT user_access_token_user_fk
        FOREIGN KEY (app_user)
            REFERENCES app_user (username)
);

delete from user_week_stats;
