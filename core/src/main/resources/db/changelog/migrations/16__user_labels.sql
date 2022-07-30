CREATE TABLE user_labels
(
    gmail_id       VARCHAR(255),
    app_user       VARCHAR(127),
    hidden         boolean,
    name           VARCHAR(255),
    unread_threads INT,

    PRIMARY KEY (gmail_id, app_user),
    CONSTRAINT user_label_fk
        FOREIGN KEY (app_user)
            REFERENCES app_user (username)
);

CREATE TABLE email_labels
(
    email_id bigint,
    gmail_id VARCHAR(255),
    app_user VARCHAR(127),

    PRIMARY KEY (email_id, gmail_id, app_user),
    CONSTRAINT email_label_thread_fk
        FOREIGN KEY (email_id)
            REFERENCES user_email_summary (id),
    CONSTRAINT email_label_label_fk
        FOREIGN KEY (gmail_id, app_user)
            REFERENCES user_labels (gmail_id, app_user)
);

alter table user_email_summary
    drop column label_ids_string;
