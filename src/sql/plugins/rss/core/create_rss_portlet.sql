-- liquibase formatted sql
-- changeset rss:create_rss_portlet.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM rss_portlet WHERE 1 = 0
--
-- Structure for table rss_portlet
--
DROP TABLE IF EXISTS rss_portlet;
CREATE TABLE rss_portlet (
  id_portlet int default '0' NOT NULL,
  rss_feed_id varchar(100) default NULL,
  PRIMARY KEY  (id_portlet)
);

--
-- Structure for table rss_liste_portlet
--
DROP TABLE IF EXISTS rss_list_portlet;
CREATE TABLE rss_list_portlet (
  id_portlet int default '0' NOT NULL,
  PRIMARY KEY  (id_portlet)
);
