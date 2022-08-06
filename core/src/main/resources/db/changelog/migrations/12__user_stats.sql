INSERT INTO ff4j_properties (property_id, clazz, currentvalue, fixedvalues, description)
VALUES ('ACTION_XP', 'org.ff4j.property.PropertyInt', '10', null, null);

CREATE TABLE week_stats_category
(
    id bigserial,
    stat_type VARCHAR(255),
    experience int,

    PRIMARY KEY (id)
);

alter table user_week_stats add column label_week_stat_id bigint;
alter table user_week_stats add column read_week_stat_id bigint;
alter table user_week_stats add column reply_week_stat_id bigint;
alter table user_week_stats drop column experience;
alter table app_user drop column total_experience;

alter table email_labels drop constraint email_label_thread_fk;
alter table user_email_summary drop constraint user_email_summary_pkey;
alter table user_email_summary drop column id;
alter table user_email_summary add primary key (gmail_id);

alter table email_labels alter column email_id type VARCHAR(255);
alter table email_labels add  CONSTRAINT email_label_thread_fk
    FOREIGN KEY (email_id)
        REFERENCES user_email_summary (gmail_id);

alter table user_email_summary add column has_had_read_xp_allocated boolean default false;
alter table user_email_summary add column has_had_label_xp_allocated boolean default false;
alter table user_email_summary add column has_had_reply_xp_allocated boolean default false;
