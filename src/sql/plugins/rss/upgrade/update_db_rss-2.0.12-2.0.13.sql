-- liquibase formatted sql
-- changeset rss:update_db_rss-2.0.12-2.0.13.sql
-- preconditions onFail:MARK_RAN onError:WARN
ALTER TABLE rss_generation ADD COLUMN workgroup_key varchar(50) DEFAULT NULL;
