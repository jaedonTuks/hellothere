alter table user_email_summary
    drop constraint user_email_thread_fk;
alter table user_thread
    drop constraint user_thread_pkey;
alter table user_thread
    add PRIMARY KEY (thread_id, app_user);

alter table user_email_summary
    add column app_user VARCHAR(127);
alter table user_email_summary
    add constraint user_email_thread_fk foreign key (thread_id, app_user) references user_thread;

drop table email_labels;

alter table user_email_summary
    add column id bigserial;
alter table user_email_summary
    add column email_to VARCHAR(1023);
alter table user_email_summary
    add column cc VARCHAR(1023);

alter table user_email_summary
    drop constraint user_email_summary_pkey;
alter table user_email_summary
    add PRIMARY KEY (id);

create table email_labels
(
    email_id bigint not null,
    label_id varchar(255) not null,
    app_user varchar(127) not null,
    primary key (email_id, label_id),
    constraint email_label_label_fk
        foreign key (label_id, app_user) references user_labels (gmail_id, app_user),
    constraint email_label_email_fk
        foreign key (email_id) references user_email_summary (id)
);
