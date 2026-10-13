-- liquibase formatted sql
-- changeset rss:update_db_rss-2.2.2-2.2.3.sql
-- preconditions onFail:MARK_RAN onError:WARN
ALTER TABLE rss_generation ADD COLUMN max_items int DEFAULT 0;
ALTER TABLE rss_generation ADD COLUMN feed_type varchar(50) DEFAULT 'rss_2.0';
ALTER TABLE rss_generation ADD COLUMN feed_encoding varchar(50) DEFAULT 'UTF-8';

CREATE TABLE rss_feed_inactive (
  id_rss_feed int DEFAULT '0' NOT NULL ,
  name varchar(255) DEFAULT NULL,
  url varchar(255) DEFAULT NULL,
  last_fetch_date datetime DEFAULT NULL,
  last_fetch_status int DEFAULT NULL,
  last_fetch_error varchar(255) DEFAULT NULL,
  workgroup_key varchar(50) DEFAULT NULL,
  include_style int default -1,
  PRIMARY KEY  (id_rss_feed)
);

RENAME TABLE rss_feed TO tmp_table,
             rss_feed_inactive TO rss_feed,
             tmp_table TO rss_feed_inactive;

CREATE TABLE rss_feed_parameter (
    parameter_key varchar(100) NOT NULL,
    parameter_value varchar(100) NOT NULL,
    PRIMARY KEY (parameter_key)
);
