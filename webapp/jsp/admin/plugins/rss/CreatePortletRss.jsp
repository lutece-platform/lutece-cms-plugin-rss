<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../PortletAdminHeader.jsp" />

<%@page import="fr.paris.lutece.plugins.rss.web.portlet.RssPortletJspBean"%>

${ rssPortletJspBean.init( pageContext.request, RssPortletJspBean.RIGHT_MANAGE_ADMIN_SITE ) }
${ rssPortletJspBean.getCreate( pageContext.request ) }

<%@ include file="../../AdminFooter.jsp" %>
