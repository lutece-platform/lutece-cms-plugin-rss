-- liquibase formatted sql
-- changeset rss:init_core_rss.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM core_admin_right WHERE id_right = 'RSS_MANAGEMENT'
--
-- Dumping data for table core_admin_right
--
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url) VALUES 
 ('RSS_MANAGEMENT','rss.adminFeature.rss_management.name',2,'jsp/admin/plugins/rss/ManageRssFiles.jsp','rss.adminFeature.rss_management.description',0,'rss','APPLICATIONS','ti ti-rss', NULL);
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url) VALUES 
 ('RSS_FEEDS_MANAGEMENT','rss.adminFeature.rss_feeds_management.name',2,'jsp/admin/plugins/rss/ManageRssFeeds.jsp','rss.adminFeature.rss_feeds_management.description',0,'rss','APPLICATIONS','ti ti-rss', NULL);


--
-- Dumping data for table core_portlet_type
--
INSERT INTO core_portlet_type (id_portlet_type,name,url_creation,url_update,home_class,plugin_name,url_docreate,create_script,create_specific,create_specific_form,url_domodify,modify_script,modify_specific,modify_specific_form) VALUES 
 ('RSS_PORTLET','rss.portlet.name','plugins/rss/CreatePortletRss.jsp','plugins/rss/ModifyPortletRss.jsp','fr.paris.lutece.plugins.rss.business.portlet.RssPortletHome','rss','plugins/rss/DoCreatePortletRss.jsp','/admin/portlet/script_create_portlet.html','/admin/plugins/rss/portlet/create_portlet_rss.html','','plugins/rss/DoModifyPortletRss.jsp','/admin/portlet/script_modify_portlet.html','/admin/plugins/rss/portlet/modify_portlet_rss.html','');
INSERT INTO core_portlet_type (id_portlet_type,name,url_creation,url_update,home_class,plugin_name,url_docreate,create_script,create_specific,create_specific_form,url_domodify,modify_script,modify_specific,modify_specific_form) VALUES 
 ('RSS_LIST_PORTLET','rss.list.portlet.name','plugins/rss/CreatePortletRssList.jsp','plugins/rss/ModifyPortletRssList.jsp','fr.paris.lutece.plugins.rss.business.portlet.RssListPortletHome','rss','plugins/rss/DoCreatePortletRssList.jsp','/admin/portlet/script_create_portlet.html','/admin/plugins/rss/portlet/create_portlet_rss_list.html','','plugins/rss/DoModifyPortletRssList.jsp','/admin/portlet/script_modify_portlet.html','/admin/plugins/rss/portlet/modify_portlet_rss_list.html','');


--
-- Dumping data for table core_user_right
--
INSERT INTO core_user_right (id_right,id_user) VALUES ('RSS_FEEDS_MANAGEMENT',1);
INSERT INTO core_user_right (id_right,id_user) VALUES ('RSS_MANAGEMENT',1);

-- changeset rss:init_core_rss-rev1.sql
-- preconditions onFail:MARK_RAN onError:WARN
UPDATE core_portlet_type SET create_specific = '/admin/plugins/rss/portlet/create_portlet_rss_list.html', modify_specific = '/admin/plugins/rss/portlet/modify_portlet_rss_list.html' WHERE id_portlet_type = 'RSS_LIST_PORTLET';
UPDATE core_admin_right SET icon_url = 'ti ti-rss' WHERE id_right IN ( 'RSS_MANAGEMENT', 'RSS_FEEDS_MANAGEMENT' ) AND ( icon_url IS NULL OR icon_url = '' );

-- changeset rss:init_core_rss-rev2.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM core_portlet_template WHERE id_portlet_type = 'RSS_PORTLET'
INSERT INTO core_portlet_template (id_portlet_type, description, template_path) VALUES ('RSS_PORTLET', 'Défaut', 'skin/plugins/rss/portlet/portlet_rss.html');

-- changeset rss:init_core_rss-rev3.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM core_portlet_template WHERE id_portlet_type = 'RSS_LIST_PORTLET'
INSERT INTO core_portlet_template (id_portlet_type, description, template_path) VALUES ('RSS_LIST_PORTLET', 'Défaut', 'skin/plugins/rss/portlet/portlet_rss_list.html');

-- changeset rss:init_core_rss-rev4.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- comment A v7 database whose rss SQL Liquibase never followed installs rss as new: its portlets leave the XSL styles 400 to 402 here too
UPDATE core_portlet SET id_style = 0 WHERE id_portlet_type IN ( 'RSS_PORTLET', 'RSS_LIST_PORTLET' );

-- changeset rss:init_core_rss-rev5.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:3 SELECT COUNT(1) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA=database() AND TABLE_NAME IN ('core_style_mode_stylesheet','core_stylesheet','core_style')
DELETE FROM core_style_mode_stylesheet WHERE id_style IN ( 400, 401, 402 );
DELETE FROM core_stylesheet WHERE id_stylesheet IN ( 9002, 9005 );
DELETE FROM core_style WHERE id_style IN ( 400, 401, 402 );

-- changeset rss:init_core_rss-rev6.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- comment The portlet types as the v8 descriptor declares them: a v7 install left NULL urls and templates (the list portlet declared none), which the v8 portlet forms cannot render
UPDATE core_portlet_type SET name = 'rss.portlet.name', url_creation = 'plugins/rss/CreatePortletRss.jsp', url_update = 'plugins/rss/ModifyPortletRss.jsp', home_class = 'fr.paris.lutece.plugins.rss.business.portlet.RssPortletHome', plugin_name = 'rss', url_docreate = 'plugins/rss/DoCreatePortletRss.jsp', create_script = '/admin/portlet/script_create_portlet.html', create_specific = '/admin/plugins/rss/portlet/create_portlet_rss.html', create_specific_form = '', url_domodify = 'plugins/rss/DoModifyPortletRss.jsp', modify_script = '/admin/portlet/script_modify_portlet.html', modify_specific = '/admin/plugins/rss/portlet/modify_portlet_rss.html', modify_specific_form = '' WHERE id_portlet_type = 'RSS_PORTLET';
UPDATE core_portlet_type SET name = 'rss.list.portlet.name', url_creation = 'plugins/rss/CreatePortletRssList.jsp', url_update = 'plugins/rss/ModifyPortletRssList.jsp', home_class = 'fr.paris.lutece.plugins.rss.business.portlet.RssListPortletHome', plugin_name = 'rss', url_docreate = 'plugins/rss/DoCreatePortletRssList.jsp', create_script = '/admin/portlet/script_create_portlet.html', create_specific = '/admin/plugins/rss/portlet/create_portlet_rss_list.html', create_specific_form = '', url_domodify = 'plugins/rss/DoModifyPortletRssList.jsp', modify_script = '/admin/portlet/script_modify_portlet.html', modify_specific = '/admin/plugins/rss/portlet/modify_portlet_rss_list.html', modify_specific_form = '' WHERE id_portlet_type = 'RSS_LIST_PORTLET';
