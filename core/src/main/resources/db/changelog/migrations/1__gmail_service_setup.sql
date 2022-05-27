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

CREATE TABLE user_email_summary
(
    id            bigserial,
    gmail_id         VARCHAR(255),
    subject VARCHAR(255),
    from_email         VARCHAR(2047),
    label_ids_string         VARCHAR(2047),
    date_sent       timestamp,
    app_user      VARCHAR(127),
    PRIMARY KEY (id),
    CONSTRAINT user_email_summary_user_fk
        FOREIGN KEY (app_user)
            REFERENCES app_user (username)
);

delete from user_week_stats;
