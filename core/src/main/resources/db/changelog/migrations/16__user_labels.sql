CREATE TABLE user_labels
(
    id         bigserial,
    gmail_id VARCHAR(255),
    name VARCHAR(255),
    unread_threads INT,
    app_user   VARCHAR(127),

    PRIMARY KEY (id),
    CONSTRAINT user_label_fk
        FOREIGN KEY(app_user)
            REFERENCES app_user(username)
);
