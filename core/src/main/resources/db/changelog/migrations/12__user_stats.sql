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
