<%@ page errorPage="../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.rss.web.portlet.RssListPortletJspBean"%>

${ rssListPortletJspBean.init( pageContext.request, RssListPortletJspBean.RIGHT_MANAGE_ADMIN_SITE ) }
${ pageContext.response.sendRedirect( rssListPortletJspBean.doModify( pageContext.request ) ) }
