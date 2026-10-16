ALTER TABLE rss_feed ADD COLUMN is_active SMALLINT DEFAULT 1 NOT NULL;
ALTER TABLE rss_feed ADD COLUMN nb_failures int DEFAULT 0 NOT NULL;

INSERT INTO rss_feed (id_rss_feed, name, url, last_fetch_date, last_fetch_status, last_fetch_error, workgroup_key, include_style, is_active)
SELECT id_rss_feed + ( SELECT COALESCE( MAX( id_rss_feed ), 0 ) FROM rss_feed ),
       name, url, last_fetch_date, last_fetch_status, last_fetch_error, workgroup_key, include_style, 0
FROM rss_feed_inactive;
DROP TABLE rss_feed_inactive;
