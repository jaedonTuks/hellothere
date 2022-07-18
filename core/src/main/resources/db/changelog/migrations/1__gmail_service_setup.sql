CREATE TABLE user_access_token
(
    app_user      VARCHAR(2047),
    token         VARCHAR(255),
    refresh_token VARCHAR(255),
    scope         VARCHAR(2047),
    expires       timestamp,
    PRIMARY KEY (app_user)
);


CREATE TABLE user_thread
(
    thread_id         VARCHAR(255),
    subject          VARCHAR(255),
    app_user         VARCHAR(127),
    PRIMARY KEY (thread_id),
    CONSTRAINT user_thread_user_fk
        FOREIGN KEY (app_user)
            REFERENCES app_user (username)
);

CREATE TABLE user_email_summary
(
    id               bigserial,
    gmail_id         VARCHAR(255),
    thread_id        VARCHAR(255),
    mime_message_id  VARCHAR(511),
    from_email       VARCHAR(255),
    label_ids_string VARCHAR(2047),
    date_sent        timestamp,

    PRIMARY KEY (id),
    CONSTRAINT user_email_thread_fk
        FOREIGN KEY (thread_id)
            REFERENCES user_thread (thread_id)
);


delete
from user_week_stats;
