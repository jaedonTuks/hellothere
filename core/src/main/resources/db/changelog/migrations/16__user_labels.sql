CREATE TABLE user_labels
(
    id         bigserial,
    gmail_id VARCHAR(255),
    hidden boolean,
    name VARCHAR(255),
    unread_threads INT,
    app_user   VARCHAR(127),

    PRIMARY KEY (id),
    CONSTRAINT user_label_fk
        FOREIGN KEY(app_user)
            REFERENCES app_user(username)
);

CREATE TABLE email_labels
(
    email_id        bigint,
    label_id         bigint,

    PRIMARY KEY (email_id, label_id),
    CONSTRAINT email_label_thread_fk
        FOREIGN KEY(email_id)
            REFERENCES user_email_summary(id),
    CONSTRAINT email_label_label_fk
        FOREIGN KEY(label_id)
            REFERENCES user_labels(id)
);

alter table user_email_summary drop column label_ids_string;
