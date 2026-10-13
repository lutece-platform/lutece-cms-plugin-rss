<%@ page errorPage="../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.rss.web.portlet.RssPortletJspBean"%>

${ rssPortletJspBean.init( pageContext.request, RssPortletJspBean.RIGHT_MANAGE_ADMIN_SITE ) }
${ pageContext.response.sendRedirect( rssPortletJspBean.doModify( pageContext.request ) ) }
