-- liquibase formatted sql
-- changeset rss:update_db_rss-2.1.0-2.2.0.sql
-- preconditions onFail:MARK_RAN onError:WARN
ALTER TABLE rss_feed ADD COLUMN include_style int default -1;

ALTER TABLE rss_generation ADD COLUMN type_resource_rss VARCHAR(50) DEFAULT null;
ALTER TABLE rss_generation MODIFY COLUMN id_portlet int DEFAULT NULL;
