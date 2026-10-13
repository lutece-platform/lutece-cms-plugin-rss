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
package fr.paris.lutece.plugins.rss.web;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.rss.business.RssFeed;
import fr.paris.lutece.plugins.rss.business.RssFeedHome;
import fr.paris.lutece.plugins.rss.business.parameter.RssFeedParameterHome;
import fr.paris.lutece.plugins.rss.business.portlet.RssPortletHome;
import fr.paris.lutece.plugins.rss.service.RssContentLoader;
import fr.paris.lutece.plugins.rss.service.RssParsingException;
import fr.paris.lutece.plugins.rss.service.RssService;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.user.AdminUserResourceIdService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.cdi.mvc.Models;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.util.IPager;
import fr.paris.lutece.portal.web.util.Pager;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;


/**
 * This class provides the user interface to manage the external rss feeds
 */
@RequestScoped
@Named
@Controller( controllerJsp = "ManageRssFeeds.jsp", controllerPath = "jsp/admin/plugins/rss/", right = RssFeedsJspBean.RIGHT_RSS_FEEDS_MANAGEMENT, securityTokenEnabled = true )
public class RssFeedsJspBean extends MVCAdminJspBean
{
    public static final String RIGHT_RSS_FEEDS_MANAGEMENT = "RSS_FEEDS_MANAGEMENT";

    private static final long serialVersionUID = 1L;

    private static final String TEMPLATE_FEEDS = "admin/plugins/rss/manage_rss_feeds.html";
    private static final String TEMPLATE_CREATE_RSS_FEED = "admin/plugins/rss/create_rss_feed.html";
    private static final String TEMPLATE_MODIFY_RSS_FEED = "admin/plugins/rss/modify_rss_feed.html";
    private static final String TEMPLATE_MANAGE_ADVANCED_PARAMETERS = "admin/plugins/rss/manage_advanced_parameters.html";

    private static final String VIEW_MANAGE_RSS_FEEDS = "manageRssFeeds";
    private static final String VIEW_CREATE_RSS_FEED = "createRssFeed";
    private static final String VIEW_MODIFY_RSS_FEED = "modifyRssFeed";
    private static final String VIEW_CONFIRM_REMOVE_RSS_FEED = "confirmRemoveRssFeed";
    private static final String VIEW_CONFIRM_ACTIVATE_RSS_FEED = "confirmActivateRssFeed";
    private static final String VIEW_MANAGE_ADVANCED_PARAMETERS = "manageAdvancedParameters";

    private static final String ACTION_UPDATE_RSS_FEEDS = "updateRssFeeds";
    private static final String ACTION_CREATE_RSS_FEED = "createRssFeed";
    private static final String ACTION_MODIFY_RSS_FEED = "modifyRssFeed";
    private static final String ACTION_REMOVE_RSS_FEED = "removeRssFeed";
    private static final String ACTION_ACTIVATE_RSS_FEED = "activateRssFeed";
    private static final String ACTION_MODIFY_ADVANCED_PARAMETERS = "modifyAdvancedParameters";

    private static final String PARAMETER_RSS_FEED_ID = "id_rss_feed";
    private static final String PARAMETER_RSS_FEED_NAME = "rss_feed_name";
    private static final String PARAMETER_RSS_FEED_URL = "rss_feed_url";
    private static final String PARAMETER_RSS_FEED_IS_ACTIVE = "rss_feed_is_active";
    private static final String PARAMETER_RSS_FEED_NEW_ORDER = "new_order";
    private static final String PARAMETER_WORKGROUP_KEY = "workgroup_key";
    private static final String PARAMETER_FETCH = "fetch";
    private static final String PARAMETER_IS_ACTIVE_BY_DEFAULT = "is_active";

    private static final String PROPERTY_PAGE_TITLE_FEEDS = "rss.manage_rss_feeds.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE = "rss.create_rss_feed.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY = "rss.modify_rss_feed.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_ADVANCED_PARAMETERS = "rss.manage_rss.labelAdvancedParameters";
    private static final String PROPERTY_RSS_PER_PAGE = "rss.rssPerPage";
    private static final String PROPERTY_RSS_INCLUDE_BEGIN = "rss.manage_rss_feeds.include.begin";
    private static final String PROPERTY_RSS_INCLUDE_END = "rss.manage_rss_feeds.include.end";

    private static final String MARK_RSS_FEEDS_LIST = "rss_feeds_list";
    private static final String MARK_RSS_FEED = "rss_feed";
    private static final String MARK_IS_ACTIVE = "is_active_list";
    private static final String MARK_ORDER_LIST = "order_list";
    private static final String MARK_USER_WORKGROUP_LIST = "user_workgroup_list";
    private static final String MARK_WORKGROUP_SELECTED = "selected_workgroup";
    private static final String MARK_PERMISSION_ADVANCED_PARAMETER = "permission_advanced_parameter";

    private static final String MESSAGE_CONFIRM_DELETE_RSS_FEED = "rss.message.confirmDeleteRssFeed";
    private static final String MESSAGE_CONFIRM_ACTIVATE_RSS_FEED = "rss.message.confirmActivateRssFeed";
    private static final String MESSAGE_CONFIRM_DEACTIVATE_RSS_FEED = "rss.message.confirmDeactivateRssFeed";
    private static final String MESSAGE_RSS_LINKED_PORTLET = "rss.message.linkedToPortlet";
    private static final String MESSAGE_RSS_FEED_NOT_FOUND = "rss.message.rssFeedNotFound";
    private static final String MESSAGE_INVALID_ORDER = "rss.message.invalidOrder";
    private static final String MESSAGE_ACCESS_DENIED = "rss.message.accesDenied";

    private static final String TRUE = "true";
    private static final String ACTIVE_BY_DEFAULT = "1";
    private static final String ZERO = "0";

    @Inject
    @Pager( name = "rss.feeds", listBookmark = MARK_RSS_FEEDS_LIST, defaultItemsPerPage = PROPERTY_RSS_PER_PAGE )
    private IPager<RssFeed, Void> _pager;

    @Inject
    private RssService _rssService;

    /**
     * Returns the plugin of the rss feeds, read by name since the request carries no plugin name
     *
     * @return the plugin
     */
    @Override
    public Plugin getPlugin( )
    {
        return PluginService.getPlugin( RssService.PLUGIN_NAME );
    }

    /**
     * Returns the list of the active or of the inactive external rss feeds
     *
     * @param model
     *            the model
     * @param request
     *            The Http request
     * @return the page
     */
    @View( value = VIEW_MANAGE_RSS_FEEDS, defaultView = true, securityTokenAction = ACTION_UPDATE_RSS_FEEDS )
    public String getManageRssFeeds( Models model, HttpServletRequest request )
    {
        boolean bActive = !Boolean.FALSE.toString( ).equals( request.getParameter( PARAMETER_RSS_FEED_IS_ACTIVE ) );
        User user = (User) getUser( );
        List<RssFeed> listRssFeed = new ArrayList<>( AdminWorkgroupService.getAuthorizedCollection( RssFeedHome.getRssFeeds( bActive ), user ) );
        String strIncludeBegin = I18nService.getLocalizedString( PROPERTY_RSS_INCLUDE_BEGIN, getLocale( ) );
        String strIncludeEnd = I18nService.getLocalizedString( PROPERTY_RSS_INCLUDE_END, getLocale( ) );

        for ( RssFeed rssFeed : listRssFeed )
        {
            rssFeed.setIncludeTag( strIncludeBegin + RssFeedInclude.getRssMarkerPrefix( ) + rssFeed.getId( ) + strIncludeEnd );
        }

        UrlItem url = new UrlItem( getViewUrl( VIEW_MANAGE_RSS_FEEDS ) );
        url.addParameter( PARAMETER_RSS_FEED_IS_ACTIVE, Boolean.toString( bActive ) );
        _pager.withBaseUrl( url.getUrl( ) ).withListItem( listRssFeed ).populateModels( request, model, getLocale( ) );

        model.put( MARK_IS_ACTIVE, bActive );
        ReferenceList listOrder = new ReferenceList( );

        for ( int nOrder = 1; nOrder <= RssFeedHome.getRssFeeds( true ).size( ); nOrder++ )
        {
            listOrder.addItem( nOrder, Integer.toString( nOrder ) );
        }

        model.put( MARK_ORDER_LIST, listOrder );
        model.put( MARK_PERMISSION_ADVANCED_PARAMETER, isAdvancedParametersAuthorized( ) );

        return getPage( PROPERTY_PAGE_TITLE_FEEDS, TEMPLATE_FEEDS, model );
    }

    /**
     * Reloads a feed or changes the order of an active feed, the two operations of the list
     *
     * @param request
     *            The Http request
     * @return the url of the list
     * @throws AccessDeniedException
     *             if the user may not see the feed
     */
    @Action( ACTION_UPDATE_RSS_FEEDS )
    public String doUpdateRssFeeds( HttpServletRequest request ) throws AccessDeniedException
    {
        RssFeed rssFeed = getAuthorizedRssFeed( request, true );

        if ( rssFeed == null )
        {
            addError( MESSAGE_RSS_FEED_NOT_FOUND, getLocale( ) );

            return redirectView( request, VIEW_MANAGE_RSS_FEEDS );
        }

        if ( request.getParameter( PARAMETER_FETCH ) != null )
        {
            try
            {
                RssContentLoader.fetchRssFeed( rssFeed.getId( ) );
            }
            catch( RssParsingException ex )
            {
                AppLogService.error( ex.getMessage( ), ex );
            }
        }
        else
        {
            try
            {
                RssFeedHome.updateOrder( rssFeed, Integer.parseInt( request.getParameter( PARAMETER_RSS_FEED_NEW_ORDER ) ) );
            }
            catch( IndexOutOfBoundsException | NumberFormatException e )
            {
                addError( MESSAGE_INVALID_ORDER, getLocale( ) );
            }
        }

        return redirectView( request, VIEW_MANAGE_RSS_FEEDS );
    }

    /**
     * Returns the creation form of a feed
     *
     * @param model
     *            the model
     * @param request
     *            The Http request
     * @return Html form
     */
    @View( VIEW_CREATE_RSS_FEED )
    public String getCreateRssFeed( Models model, HttpServletRequest request )
    {
        ReferenceList refListWorkGroups = AdminWorkgroupService.getUserWorkgroups( getUser( ), getLocale( ) );
        model.put( MARK_USER_WORKGROUP_LIST, refListWorkGroups );

        if ( !refListWorkGroups.isEmpty( ) )
        {
            model.put( MARK_WORKGROUP_SELECTED, refListWorkGroups.get( 0 ).getCode( ) );
        }

        for ( ReferenceItem param : RssFeedParameterHome.findAll( getPlugin( ) ) )
        {
            model.put( param.getCode( ), param.getName( ) );
        }

        return getPage( PROPERTY_PAGE_TITLE_CREATE, TEMPLATE_CREATE_RSS_FEED, model );
    }

    /**
     * Process the data capture form of a new rss feed
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_CREATE_RSS_FEED )
    public String doCreateRssFeed( HttpServletRequest request )
    {
        String strName = request.getParameter( PARAMETER_RSS_FEED_NAME );
        String strUrl = getUrlParameter( request );

        if ( StringUtils.isBlank( strName ) || StringUtils.isBlank( strUrl ) )
        {
            addError( Messages.MANDATORY_FIELDS, getLocale( ) );

            return redirectView( request, VIEW_CREATE_RSS_FEED );
        }

        RssFeed rss = new RssFeed( );
        rss.setName( strName );
        rss.setUrl( strUrl );
        rss.setWorkgroup( request.getParameter( PARAMETER_WORKGROUP_KEY ) );
        rss.setIdIncludeStyle( -1 );
        rss.setIsActive( isActiveByDefault( ) );

        RssFeedHome.create( rss );

        if ( rss.getIsActive( ) )
        {
            _rssService.getRssContent( rss.getId( ) );
        }

        return redirectToList( request, rss.getIsActive( ) );
    }

    /**
     * Returns the form to update info about a rss feed
     *
     * @param model
     *            the model
     * @param request
     *            The Http request
     * @return The HTML form to update info
     * @throws AccessDeniedException
     *             if the user may not see the feed
     */
    @View( VIEW_MODIFY_RSS_FEED )
    public String getModifyRssFeed( Models model, HttpServletRequest request ) throws AccessDeniedException
    {
        RssFeed rssFeed = getAuthorizedRssFeed( request, isActiveParameter( request ) );

        if ( rssFeed == null )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_RSS_FEED_NOT_FOUND, AdminMessage.TYPE_STOP ) );
        }

        model.put( MARK_USER_WORKGROUP_LIST, AdminWorkgroupService.getUserWorkgroups( getUser( ), getLocale( ) ) );
        model.put( MARK_RSS_FEED, rssFeed );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY, TEMPLATE_MODIFY_RSS_FEED, model );
    }

    /**
     * Process modification of a feed
     *
     * @param request
     *            The Http request
     * @return String The url of the administration console
     * @throws AccessDeniedException
     *             if the user may not see the feed
     */
    @Action( ACTION_MODIFY_RSS_FEED )
    public String doModifyRssFeed( HttpServletRequest request ) throws AccessDeniedException
    {
        boolean bActive = isActiveParameter( request );
        RssFeed rss = getAuthorizedRssFeed( request, bActive );

        if ( rss == null )
        {
            addError( MESSAGE_RSS_FEED_NOT_FOUND, getLocale( ) );

            return redirectView( request, VIEW_MANAGE_RSS_FEEDS );
        }

        String strName = request.getParameter( PARAMETER_RSS_FEED_NAME );
        String strUrl = getUrlParameter( request );

        if ( StringUtils.isBlank( strName ) || StringUtils.isBlank( strUrl ) )
        {
            addError( Messages.MANDATORY_FIELDS, getLocale( ) );

            return redirect( request, VIEW_MODIFY_RSS_FEED, Map.of( PARAMETER_RSS_FEED_ID, Integer.toString( rss.getId( ) ), PARAMETER_RSS_FEED_IS_ACTIVE, Boolean.toString( bActive ) ) );
        }

        rss.setName( strName );
        rss.setUrl( strUrl );
        rss.setWorkgroup( request.getParameter( PARAMETER_WORKGROUP_KEY ) );
        RssFeedHome.update( rss );

        return redirectToList( request, bActive );
    }

    /**
     * Confirms the removal of a feed
     *
     * @param request
     *            The Http request
     * @return The url of the confirmation message
     * @throws AccessDeniedException
     *             if the user may not see the feed
     */
    @View( value = VIEW_CONFIRM_REMOVE_RSS_FEED, securityTokenAction = ACTION_REMOVE_RSS_FEED )
    public String getConfirmRemoveRssFeed( HttpServletRequest request ) throws AccessDeniedException
    {
        return redirect( request, getConfirmationUrl( request, ACTION_REMOVE_RSS_FEED, MESSAGE_CONFIRM_DELETE_RSS_FEED ) );
    }

    /**
     * Process removal of a feed
     *
     * @param request
     *            The Http request
     * @return String The url of the administration console
     * @throws AccessDeniedException
     *             if the user may not see the feed
     */
    @Action( ACTION_REMOVE_RSS_FEED )
    public String doRemoveRssFeed( HttpServletRequest request ) throws AccessDeniedException
    {
        boolean bActive = isActiveParameter( request );
        RssFeed rssFeed = getAuthorizedRssFeed( request, bActive );

        if ( rssFeed == null )
        {
            addError( MESSAGE_RSS_FEED_NOT_FOUND, getLocale( ) );
        }
        else if ( !RssPortletHome.checkNoPortletLinked( rssFeed.getId( ) ) )
        {
            addError( MESSAGE_RSS_LINKED_PORTLET, getLocale( ) );
        }
        else
        {
            RssFeedHome.remove( rssFeed );
        }

        return redirectToList( request, bActive );
    }

    /**
     * Confirms the activation or the deactivation of a feed
     *
     * @param request
     *            The Http request
     * @return The url of the confirmation message
     * @throws AccessDeniedException
     *             if the user may not see the feed
     */
    @View( value = VIEW_CONFIRM_ACTIVATE_RSS_FEED, securityTokenAction = ACTION_ACTIVATE_RSS_FEED )
    public String getConfirmActivateRssFeed( HttpServletRequest request ) throws AccessDeniedException
    {
        String strMessage = isActiveParameter( request ) ? MESSAGE_CONFIRM_DEACTIVATE_RSS_FEED : MESSAGE_CONFIRM_ACTIVATE_RSS_FEED;

        return redirect( request, getConfirmationUrl( request, ACTION_ACTIVATE_RSS_FEED, strMessage ) );
    }

    /**
     * Process the activation or the deactivation of a feed
     *
     * @param request
     *            The Http request
     * @return String The url of the administration console
     * @throws AccessDeniedException
     *             if the user may not see the feed
     */
    @Action( ACTION_ACTIVATE_RSS_FEED )
    public String doActivateRssFeed( HttpServletRequest request ) throws AccessDeniedException
    {
        boolean bActive = isActiveParameter( request );
        RssFeed rssFeed = getAuthorizedRssFeed( request, bActive );

        if ( rssFeed == null )
        {
            addError( MESSAGE_RSS_FEED_NOT_FOUND, getLocale( ) );
        }
        else if ( bActive && !RssPortletHome.checkNoPortletLinked( rssFeed.getId( ) ) )
        {
            addError( MESSAGE_RSS_LINKED_PORTLET, getLocale( ) );
        }
        else
        {
            RssFeedHome.setActive( rssFeed, !bActive );
        }

        return redirectToList( request, bActive );
    }

    /**
     * Return RSS advanced parameters
     *
     * @param model
     *            the model
     * @param request
     *            The Http request
     * @return Html form
     * @throws AccessDeniedException
     *             if the user is not authorized to manage advanced parameters
     */
    @View( value = VIEW_MANAGE_ADVANCED_PARAMETERS, securityTokenAction = ACTION_MODIFY_ADVANCED_PARAMETERS )
    public String getManageAdvancedParameters( Models model, HttpServletRequest request ) throws AccessDeniedException
    {
        checkAdvancedParametersAuthorized( );

        for ( ReferenceItem param : RssFeedParameterHome.findAll( getPlugin( ) ) )
        {
            model.put( param.getCode( ), param.getName( ) );
        }

        return getPage( PROPERTY_PAGE_TITLE_ADVANCED_PARAMETERS, TEMPLATE_MANAGE_ADVANCED_PARAMETERS, model );
    }

    /**
     * Modify RSS feed parameters default values
     *
     * @param request
     *            the request
     * @return the url of the list
     * @throws AccessDeniedException
     *             if the user is not authorized to manage advanced parameters
     */
    @Action( ACTION_MODIFY_ADVANCED_PARAMETERS )
    public String doModifyAdvancedParameters( HttpServletRequest request ) throws AccessDeniedException
    {
        checkAdvancedParametersAuthorized( );

        for ( ReferenceItem param : RssFeedParameterHome.findAll( getPlugin( ) ) )
        {
            String strParamValue = request.getParameter( param.getCode( ) );
            param.setName( StringUtils.isBlank( strParamValue ) ? ZERO : strParamValue );
            RssFeedParameterHome.update( param, getPlugin( ) );
        }

        return redirectView( request, VIEW_MANAGE_RSS_FEEDS );
    }

    /**
     * Redirects to the list of the active or of the inactive feeds
     *
     * @param request
     *            the request
     * @param bActive
     *            true for the list of the active feeds
     * @return the url of the list
     */
    private String redirectToList( HttpServletRequest request, boolean bActive )
    {
        return redirect( request, VIEW_MANAGE_RSS_FEEDS, Map.of( PARAMETER_RSS_FEED_IS_ACTIVE, Boolean.toString( bActive ) ) );
    }

    /**
     * Builds the url of a confirmation message whose form posts an action on the feed of the request
     *
     * @param request
     *            the request
     * @param strAction
     *            the action to confirm
     * @param strMessageKey
     *            the key of the message
     * @return the url of the confirmation message
     * @throws AccessDeniedException
     *             if the user may not see the feed
     */
    private String getConfirmationUrl( HttpServletRequest request, String strAction, String strMessageKey ) throws AccessDeniedException
    {
        boolean bActive = isActiveParameter( request );
        RssFeed rssFeed = getAuthorizedRssFeed( request, bActive );

        if ( rssFeed == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_RSS_FEED_NOT_FOUND, AdminMessage.TYPE_STOP );
        }

        if ( bActive && !RssPortletHome.checkNoPortletLinked( rssFeed.getId( ) ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_RSS_LINKED_PORTLET, AdminMessage.TYPE_STOP );
        }

        UrlItem url = new UrlItem( getActionUrl( strAction ) );
        url.addParameter( PARAMETER_RSS_FEED_ID, rssFeed.getId( ) );
        url.addParameter( PARAMETER_RSS_FEED_IS_ACTIVE, Boolean.toString( bActive ) );

        Object [ ] messageArgs = {
            rssFeed.getName( )
        };

        return AdminMessageService.getMessageUrl( request, strMessageKey, messageArgs, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Loads the feed named by the request and checks the workgroup of the user
     *
     * @param request
     *            the request
     * @param bActive
     *            true to look for the feed among the active feeds
     * @return the feed, or null when the request names no existing feed
     * @throws AccessDeniedException
     *             if the user may not see the feed
     */
    private RssFeed getAuthorizedRssFeed( HttpServletRequest request, boolean bActive ) throws AccessDeniedException
    {
        String strId = request.getParameter( PARAMETER_RSS_FEED_ID );

        if ( !StringUtils.isNumeric( strId ) )
        {
            return null;
        }

        RssFeed rssFeed = RssFeedHome.findByPrimaryKey( Integer.parseInt( strId ), bActive );

        if ( rssFeed != null && !AdminWorkgroupService.isAuthorized( rssFeed, (User) getUser( ) ) )
        {
            throw new AccessDeniedException( MESSAGE_ACCESS_DENIED );
        }

        return rssFeed;
    }

    /**
     * Tells whether the request names an active feed
     *
     * @param request
     *            the request
     * @return true unless the request says the feed is inactive
     */
    private boolean isActiveParameter( HttpServletRequest request )
    {
        return TRUE.equalsIgnoreCase( request.getParameter( PARAMETER_RSS_FEED_IS_ACTIVE ) );
    }

    /**
     * Reads the url of the feed, decoded from the html entities the request filter adds
     *
     * @param request
     *            the request
     * @return the url, or null when absent
     */
    private String getUrlParameter( HttpServletRequest request )
    {
        String strUrl = request.getParameter( PARAMETER_RSS_FEED_URL );

        return ( strUrl == null ) ? null : StringEscapeUtils.unescapeHtml4( strUrl ).trim( );
    }

    /**
     * Tells whether a new feed is active, from the advanced parameters
     *
     * @return true when the feeds are active by default
     */
    private boolean isActiveByDefault( )
    {
        for ( ReferenceItem param : RssFeedParameterHome.findAll( getPlugin( ) ) )
        {
            if ( PARAMETER_IS_ACTIVE_BY_DEFAULT.equals( param.getCode( ) ) )
            {
                return ACTIVE_BY_DEFAULT.equals( param.getName( ) );
            }
        }

        return false;
    }

    /**
     * Tells whether the user may manage the advanced parameters
     *
     * @return true when the user has the permission
     */
    private boolean isAdvancedParametersAuthorized( )
    {
        return RBACService.isAuthorized( AdminUser.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID, AdminUserResourceIdService.PERMISSION_MANAGE_ADVANCED_PARAMETERS,
                (User) getUser( ) );
    }

    /**
     * Refuses a user who may not manage the advanced parameters
     *
     * @throws AccessDeniedException
     *             if the user is not authorized to manage advanced parameters
     */
    private void checkAdvancedParametersAuthorized( ) throws AccessDeniedException
    {
        if ( !isAdvancedParametersAuthorized( ) )
        {
            throw new AccessDeniedException( MESSAGE_ACCESS_DENIED );
        }
    }
}
