/*
 * Copyright (c) 2002-2014, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.rss.web.portlet;

import fr.paris.lutece.plugins.rss.business.portlet.RssListPortlet;
import fr.paris.lutece.plugins.rss.business.portlet.RssListPortletHome;
import fr.paris.lutece.portal.business.portlet.PortletHome;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.web.portlet.PortletJspBean;
import fr.paris.lutece.util.html.HtmlTemplate;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;


/**
 * This class provides the user interface to manage rss list portlets
 */
@RequestScoped
@Named
public class RssListPortletJspBean extends PortletJspBean
{
    public static final String RIGHT_MANAGE_ADMIN_SITE = "CORE_ADMIN_SITE";

    private static final long serialVersionUID = 1L;

    @Inject
    private RssListPortletHome _rssListPortletHome;

    private static final String MESSAGE_INVALID_TOKEN = "rss.message.invalidToken";
    private static final String MESSAGE_PORTLET_NOT_FOUND = "rss.message.portletNotFound";
    private static final String ACTION_CREATE_PORTLET = "rss.createRssListPortlet";
    private static final String ACTION_MODIFY_PORTLET = "rss.modifyRssListPortlet";

    /**
     * Returns the prefix of the portlet properties
     *
     * @return the prefix
     */
    public String getPropertiesPrefix(  )
    {
        return "portlet.rss.list";
    }

    /**
     * Returns the creation form of the portlet
     *
     * @param request the HTTP request
     * @return the HTML of the form
     */
    @Override
    public String getCreate( HttpServletRequest request )
    {
        String strPageId = request.getParameter( PARAMETER_PAGE_ID );
        String strPortletTypeId = request.getParameter( PARAMETER_PORTLET_TYPE_ID );
        Map<String, Object> model = new HashMap<>(  );
        model.put( SecurityTokenService.MARK_TOKEN, getSecurityTokenService( ).getToken( request, ACTION_CREATE_PORTLET ) );

        HtmlTemplate template = getCreateTemplate( strPageId, strPortletTypeId, model );

        return template.getHtml(  );
    }

    /**
     * Returns the modification form of the portlet
     *
     * @param request the HTTP request
     * @return the HTML of the form
     */
    @Override
    public String getModify( HttpServletRequest request )
    {
        RssListPortlet portlet = getPortlet( request );

        if ( portlet == null )
        {
            return I18nService.getLocalizedString( MESSAGE_PORTLET_NOT_FOUND, getLocale( ) );
        }

        Map<String, Object> model = new HashMap<>(  );
        model.put( SecurityTokenService.MARK_TOKEN, getSecurityTokenService( ).getToken( request, ACTION_MODIFY_PORTLET ) );

        HtmlTemplate template = getModifyTemplate( portlet, model );

        return template.getHtml(  );
    }

    /**
     * Creates the portlet
     *
     * @param request the HTTP request
     * @return the url of the page of the portlet, or of the error
     */
    @Override
    public String doCreate( HttpServletRequest request )
    {
        if ( !getSecurityTokenService( ).validate( request, ACTION_CREATE_PORTLET ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_INVALID_TOKEN, AdminMessage.TYPE_STOP );
        }

        RssListPortlet portlet = new RssListPortlet(  );
        String strErrorUrl = setPortletCommonData( request, portlet );

        if ( strErrorUrl != null )
        {
            return strErrorUrl;
        }

        portlet.setPageId( Integer.parseInt( request.getParameter( PARAMETER_PAGE_ID ) ) );
        _rssListPortletHome.create( portlet );

        return getPageUrl( portlet.getPageId(  ) );
    }

    /**
     * Modifies the portlet
     *
     * @param request the HTTP request
     * @return the url of the page of the portlet, or of the error
     */
    @Override
    public String doModify( HttpServletRequest request )
    {
        if ( !getSecurityTokenService( ).validate( request, ACTION_MODIFY_PORTLET ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_INVALID_TOKEN, AdminMessage.TYPE_STOP );
        }

        RssListPortlet portlet = getPortlet( request );

        if ( portlet == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_PORTLET_NOT_FOUND, AdminMessage.TYPE_STOP );
        }

        String strErrorUrl = setPortletCommonData( request, portlet );

        if ( strErrorUrl != null )
        {
            return strErrorUrl;
        }

        portlet.update(  );

        return getPageUrl( portlet.getPageId(  ) );
    }

    /**
     * Loads the rss list portlet named by the request
     *
     * @param request the HTTP request
     * @return the portlet, or null when the request names no rss list portlet
     */
    private RssListPortlet getPortlet( HttpServletRequest request )
    {
        String strPortletId = request.getParameter( PARAMETER_PORTLET_ID );

        if ( !StringUtils.isNumeric( strPortletId ) || !RssListPortletHome.checkPortletExists( Integer.parseInt( strPortletId ) ) )
        {
            return null;
        }

        return (RssListPortlet) PortletHome.findByPrimaryKey( Integer.parseInt( strPortletId ) );
    }
}
