<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../PortletAdminHeader.jsp" />

<%@page import="fr.paris.lutece.plugins.rss.web.portlet.RssListPortletJspBean"%>

${ rssListPortletJspBean.init( pageContext.request, RssListPortletJspBean.RIGHT_MANAGE_ADMIN_SITE ) }
${ rssListPortletJspBean.getModify( pageContext.request ) }

<%@ include file="../../AdminFooter.jsp" %>
