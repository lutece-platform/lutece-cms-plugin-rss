-- liquibase formatted sql
-- changeset rss:update_db_rss-2.0.14-2.1.0.sql
-- preconditions onFail:MARK_RAN onError:WARN
ALTER TABLE rss_feed ADD COLUMN workgroup_key varchar(50) DEFAULT NULL;
