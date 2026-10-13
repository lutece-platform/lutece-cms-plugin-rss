-- liquibase formatted sql
-- changeset rss:update_db_core_rss-2.0.10-2.0.11.sql
-- preconditions onFail:MARK_RAN onError:WARN
INSERT INTO core_portlet_type(id_portlet_type,name,url_creation,url_update,home_class,plugin_name,url_docreate,create_script,create_specific,create_specific_form,url_domodify,modify_script,modify_specific,modify_specific_form) VALUES
('RSS_PORTLET','rss.portlet.name','plugins/rss/CreatePortletRss.jsp','plugins/rss/ModifyPortletRss.jsp','fr.paris.lutece.plugins.rss.business.portlet.RssPortletHome','rss','plugins/rss/DoCreatePortletRss.jsp','/admin/portlet/script_create_portlet.html','/admin/plugins/rss/portlet/create_portlet_rss.html','','plugins/rss/DoModifyPortletRss.jsp','/admin/portlet/script_modify_portlet.html','/admin/plugins/rss/portlet/modify_portlet_rss.html','');
