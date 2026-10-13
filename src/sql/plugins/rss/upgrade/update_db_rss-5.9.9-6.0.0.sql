-- liquibase formatted sql
-- changeset rss:update_db_rss-5.9.9-6.0.0.sql
-- preconditions onFail:MARK_RAN onError:WARN
UPDATE core_admin_right SET icon_url = 'ti ti-rss' WHERE id_right IN ( 'RSS_MANAGEMENT', 'RSS_FEEDS_MANAGEMENT' ) AND ( icon_url IS NULL OR icon_url = '' );

-- changeset rss:update_db_rss-5.9.9-6.0.0-rev1.sql
-- preconditions onFail:MARK_RAN onError:WARN
UPDATE core_portlet_type SET create_specific = '/admin/plugins/rss/portlet/create_portlet_rss_list.html', modify_specific = '/admin/plugins/rss/portlet/modify_portlet_rss_list.html' WHERE id_portlet_type = 'RSS_LIST_PORTLET';

-- changeset rss:update_db_rss-5.9.9-6.0.0-rev2.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM core_portlet_template WHERE id_portlet_type = 'RSS_PORTLET'
INSERT INTO core_portlet_template (id_portlet_type, description, template_path) VALUES ('RSS_PORTLET', 'Défaut', 'skin/plugins/rss/portlet/portlet_rss.html');

-- changeset rss:update_db_rss-5.9.9-6.0.0-rev3.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM core_portlet_template WHERE id_portlet_type = 'RSS_LIST_PORTLET'
INSERT INTO core_portlet_template (id_portlet_type, description, template_path) VALUES ('RSS_LIST_PORTLET', 'Défaut', 'skin/plugins/rss/portlet/portlet_rss_list.html');

-- changeset rss:update_db_rss-5.9.9-6.0.0-rev4.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- comment The rss portlets are rendered by their HTML template, the XSL styles 400 to 402 are gone
UPDATE core_portlet SET id_style = 0 WHERE id_portlet_type IN ( 'RSS_PORTLET', 'RSS_LIST_PORTLET' );

-- changeset rss:update_db_rss-5.9.9-6.0.0-rev5.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:3 SELECT COUNT(1) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA=database() AND TABLE_NAME IN ('core_style_mode_stylesheet','core_stylesheet','core_style')
DELETE FROM core_style_mode_stylesheet WHERE id_style IN ( 400, 401, 402 );
DELETE FROM core_stylesheet WHERE id_stylesheet IN ( 9002, 9005 );
DELETE FROM core_style WHERE id_style IN ( 400, 401, 402 );

-- changeset rss:update_db_rss-5.9.9-6.0.0-rev6.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- comment The portlet types as the v8 descriptor declares them: a v7 install left NULL urls and templates (the list portlet declared none), which the v8 portlet forms cannot render
UPDATE core_portlet_type SET name = 'rss.portlet.name', url_creation = 'plugins/rss/CreatePortletRss.jsp', url_update = 'plugins/rss/ModifyPortletRss.jsp', home_class = 'fr.paris.lutece.plugins.rss.business.portlet.RssPortletHome', plugin_name = 'rss', url_docreate = 'plugins/rss/DoCreatePortletRss.jsp', create_script = '/admin/portlet/script_create_portlet.html', create_specific = '/admin/plugins/rss/portlet/create_portlet_rss.html', create_specific_form = '', url_domodify = 'plugins/rss/DoModifyPortletRss.jsp', modify_script = '/admin/portlet/script_modify_portlet.html', modify_specific = '/admin/plugins/rss/portlet/modify_portlet_rss.html', modify_specific_form = '' WHERE id_portlet_type = 'RSS_PORTLET';
UPDATE core_portlet_type SET name = 'rss.list.portlet.name', url_creation = 'plugins/rss/CreatePortletRssList.jsp', url_update = 'plugins/rss/ModifyPortletRssList.jsp', home_class = 'fr.paris.lutece.plugins.rss.business.portlet.RssListPortletHome', plugin_name = 'rss', url_docreate = 'plugins/rss/DoCreatePortletRssList.jsp', create_script = '/admin/portlet/script_create_portlet.html', create_specific = '/admin/plugins/rss/portlet/create_portlet_rss_list.html', create_specific_form = '', url_domodify = 'plugins/rss/DoModifyPortletRssList.jsp', modify_script = '/admin/portlet/script_modify_portlet.html', modify_specific = '/admin/plugins/rss/portlet/modify_portlet_rss_list.html', modify_specific_form = '' WHERE id_portlet_type = 'RSS_LIST_PORTLET';
