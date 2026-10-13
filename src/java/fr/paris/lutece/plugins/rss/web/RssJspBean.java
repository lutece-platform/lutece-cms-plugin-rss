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
import fr.paris.lutece.plugins.document.service.publishing.PublishingService;
import fr.paris.lutece.plugins.rss.business.RssFeedHome;
import fr.paris.lutece.plugins.rss.business.RssGeneratedFile;
import fr.paris.lutece.plugins.rss.business.RssGeneratedFileHome;
import fr.paris.lutece.plugins.rss.service.RssGeneratorService;
import fr.paris.lutece.plugins.rss.service.RssService;
import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.portal.business.rss.IResourceRss;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.portlet.PortletService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.cdi.mvc.Models;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.util.IPager;
import fr.paris.lutece.portal.web.util.Pager;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.filesystem.UploadUtil;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;


/**
 * This class provides the user interface to manage the generated rss files
 */
@RequestScoped
@Named
@Controller( controllerJsp = "ManageRssFiles.jsp", controllerPath = "jsp/admin/plugins/rss/", right = RssJspBean.RIGHT_RSS_MANAGEMENT, securityTokenEnabled = true )
public class RssJspBean extends MVCAdminJspBean
{
    public static final String RIGHT_RSS_MANAGEMENT = "RSS_MANAGEMENT";

    private static final long serialVersionUID = 1L;

    private static final String VIEW_MANAGE_RSS_FILES = "manageRssFiles";
    private static final String VIEW_CREATE_RSS_FILE = "createRssFile";
    private static final String VIEW_CREATE_RSS_FILE_PORTLET = "createRssFilePortlet";
    private static final String VIEW_CREATE_RSS_FILE_RESOURCE = "createRssFileResource";
    private static final String VIEW_MODIFY_RSS_FILE = "modifyRssFile";
    private static final String VIEW_CONFIRM_REMOVE_RSS_FILE = "confirmRemoveRssFile";

    private static final String ACTION_CREATE_RSS_FILE_PORTLET = "createRssFilePortlet";
    private static final String ACTION_CREATE_RSS_FILE_RESOURCE = "createRssFileResource";
    private static final String ACTION_MODIFY_RSS_FILE = "modifyRssFile";
    private static final String ACTION_REMOVE_RSS_FILE = "removeRssFile";

    private static final String PARAMETER_PUSH_RSS_PORTLET_ID = "rss_portlet_id";
    private static final String PARAMETER_PUSH_RSS_ID = "rss_id";
    private static final String PARAMETER_PUSH_RSS_NAME = "rss_name";
    private static final String PARAMETER_PUSH_RSS_MAX_ITEMS = "rss_max_items";
    private static final String PARAMETER_PUSH_RSS_ENCODING = "rss_encoding";
    private static final String PARAMETER_PUSH_RSS_FEED_TYPE = "rss_feed_type";
    private static final String PARAMETER_PUSH_RSS_DESCRIPTION = "rss_description";
    private static final String PARAMETER_RSS_RESOURCE_KEY = "rss_resource_key";
    private static final String PARAMETER_WORKGROUP_KEY = "workgroup_key";
    private static final String PARAMETER_APPLY = "apply";

    private static final String TEMPLATE_MANAGE_RSS_FILE = "admin/plugins/rss/manage_rss_file.html";
    private static final String TEMPLATE_CREATE_PUSH_RSS_FILE = "admin/plugins/rss/create_rss_file.html";
    private static final String TEMPLATE_CREATE_PUSH_RSS_FILE_PORTLET = "admin/plugins/rss/create_rss_file_portlet.html";
    private static final String TEMPLATE_CREATE_PUSH_RSS_FILE_RESOURCE = "admin/plugins/rss/create_rss_file_resource.html";
    private static final String TEMPLATE_MODIFY_PUSH_RSS_FILE_PORTLET = "admin/plugins/rss/modify_rss_file_portlet.html";
    private static final String TEMPLATE_MODIFY_PUSH_RSS_FILE_RESOURCE = "admin/plugins/rss/modify_rss_file_resource.html";

    private static final String MARK_RSS_FILE = "rssFile";
    private static final String MARK_PUSH_RSS_LIST = "rss_files_list";
    private static final String MARK_PUSH_RSS_URL = "rss_file_url";
    private static final String MARK_PORTLET_LIST = "portlet_list";
    private static final String MARK_RESOURCE_LIST = "resource_list";
    private static final String MARK_USER_WORKGROUP_LIST = "user_workgroup_list";
    private static final String MARK_ENCODING_LIST = "encoding_list";
    private static final String MARK_FEED_TYPE_LIST = "feed_type_list";
    private static final String MARK_WORKGROUP_SELECTED = "selected_workgroup";
    private static final String MARK_RSS_NAME = "rss_name";
    private static final String MARK_RSS_MAX_ITEMS = "rss_max_items";
    private static final String MARK_RSS_ENCODING = "rss_encoding";
    private static final String MARK_RSS_FEED_TYPE = "rss_feed_type";
    private static final String MARK_RESOURCE_RSS = "resource_rss";
    private static final String MARK_RESOURCE_RSS_CONFIG = "resource_rss_config";

    private static final int STATE_OK = 0;
    private static final int STATE_PORTLET_MISSING = 1;

    private static final String PROPERTY_PAGE_TITLE_FILES = "rss.manage_rss_file.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE = "rss.create_rss_file.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY = "rss.modify_rss_file.pageTitle";
    private static final String PROPERTY_RSS_PER_PAGE = "rss.rssPerPage";

    private static final String MESSAGE_FILENAME_TOO_LONG = "rss.message.filenameTooLong";
    private static final String MESSAGE_FILENAME_ALREADY_EXISTS = "rss.message.filenameAlreadyExists";
    private static final String MESSAGE_NO_DOCUMENT_PORTLET = "rss.message.NoDocumentPortlet";
    private static final String MESSAGE_RESOURCE = "rss.message.noResource";
    private static final String MESSAGE_CONFIRM_DELETE_RSS_FILE = "rss.message.confirmRemoveRssFile";
    private static final String MESSAGE_RSS_LINKED_FEED = "rss.message.linkedToFeed";
    private static final String MESSAGE_MAX_ITEMS = "rss.message.maxItems";
    private static final String MESSAGE_NO_ENCODING = "rss.message.noEncoding";
    private static final String MESSAGE_NO_FEED_TYPE = "rss.message.noFeedType";
    private static final String MESSAGE_RSS_FILE_NOT_FOUND = "rss.message.rssFileNotFound";
    private static final String MESSAGE_ACCESS_DENIED = "rss.message.accesDenied";

    private static final String REGEX_ID = "^[\\d]+$";
    private static final String WORKGROUP_ALL = "all";

    @Inject
    @Pager( name = "rss.files", listBookmark = MARK_PUSH_RSS_LIST, defaultItemsPerPage = PROPERTY_RSS_PER_PAGE )
    private IPager<RssGeneratedFile, Void> _pager;

    @Inject
    private RssService _rssService;

    @Inject
    @ConfigProperty( name = "rss.name.max.length", defaultValue = "60" )
    private int _nNameMaxLength;

    @Inject
    @ConfigProperty( name = "rss.file.type", defaultValue = ".xml" )
    private String _strFileType;

    @Inject
    @ConfigProperty( name = "rss.encoding", defaultValue = "UTF-8" )
    private String _strEncodings;

    @Inject
    @ConfigProperty( name = "rss.feedType", defaultValue = "rss_2.0" )
    private String _strFeedTypes;

    @Inject
    @ConfigProperty( name = "rss.storage.directory.name", defaultValue = "rss" )
    private String _strStorageDirectoryName;

    @Inject
    @ConfigProperty( name = "path.plugins.warehouse", defaultValue = "/plugins" )
    private String _strPluginWarehouse;

    @Inject
    private PublishingService _publishingService;

    @Inject
    private PortletService _portletService;

    /**
     * Returns the plugin of the rss files, read by name since the request carries no plugin name
     *
     * @return the plugin
     */
    @Override
    public Plugin getPlugin( )
    {
        return PluginService.getPlugin( RssService.PLUGIN_NAME );
    }

    /**
     * Returns the list of the rss files
     *
     * @param model
     *            the model
     * @param request
     *            The Http request
     * @return the page
     */
    @View( value = VIEW_MANAGE_RSS_FILES, defaultView = true )
    public String getManageRssFiles( Models model, HttpServletRequest request )
    {
        List<RssGeneratedFile> listRssFile = new ArrayList<>( AdminWorkgroupService.getAuthorizedCollection( RssGeneratedFileHome.getRssFileList( ),
                (User) getUser( ) ) );
        _pager.withBaseUrl( getViewUrl( VIEW_MANAGE_RSS_FILES ) ).withListItem( listRssFile ).populateModels( request, model, getLocale( ) );
        model.put( MARK_PUSH_RSS_URL, getRssFileUrl( "", request ) );

        return getPage( PROPERTY_PAGE_TITLE_FILES, TEMPLATE_MANAGE_RSS_FILE, model );
    }

    /**
     * Returns the choice between a rss file of a portlet and a rss file of a resource
     *
     * @param model
     *            the model
     * @param request
     *            The Http request
     * @return Html form
     */
    @View( VIEW_CREATE_RSS_FILE )
    public String getCreateRssFile( Models model, HttpServletRequest request )
    {
        model.put( MARK_RESOURCE_LIST, _rssService.getRefListResourceRssType( getLocale( ) ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE, TEMPLATE_CREATE_PUSH_RSS_FILE, model );
    }

    /**
     * Returns the creation form of a rss file of a portlet
     *
     * @param model
     *            the model
     * @param request
     *            The Http request
     * @return Html form
     */
    @View( VIEW_CREATE_RSS_FILE_PORTLET )
    public String getCreateRssFilePortlet( Models model, HttpServletRequest request )
    {
        ReferenceList refListWorkGroups = AdminWorkgroupService.getUserWorkgroups( getUser( ), getLocale( ) );

        model.put( MARK_PORTLET_LIST, getPortletList( ) );
        model.put( MARK_USER_WORKGROUP_LIST, refListWorkGroups );
        model.put( MARK_RSS_MAX_ITEMS, StringUtils.EMPTY );
        model.put( MARK_ENCODING_LIST, getRefList( _strEncodings ) );
        model.put( MARK_RSS_ENCODING, StringUtils.EMPTY );
        model.put( MARK_FEED_TYPE_LIST, getRefList( _strFeedTypes ) );
        model.put( MARK_RSS_FEED_TYPE, StringUtils.EMPTY );

        if ( !refListWorkGroups.isEmpty( ) )
        {
            model.put( MARK_WORKGROUP_SELECTED, refListWorkGroups.get( 0 ).getCode( ) );
        }

        return getPage( PROPERTY_PAGE_TITLE_CREATE, TEMPLATE_CREATE_PUSH_RSS_FILE_PORTLET, model );
    }

    /**
     * Creates the rss file of a portlet
     *
     * @param request
     *            The Http request
     * @return The url of the list
     */
    @Action( ACTION_CREATE_RSS_FILE_PORTLET )
    public String doCreateRssFilePortlet( HttpServletRequest request )
    {
        String strPortletId = request.getParameter( PARAMETER_PUSH_RSS_PORTLET_ID );
        String strRssFileName = request.getParameter( PARAMETER_PUSH_RSS_NAME );
        String strRssFileDescription = request.getParameter( PARAMETER_PUSH_RSS_DESCRIPTION );
        String strError = null;

        if ( ( strPortletId == null ) || !strPortletId.matches( REGEX_ID ) || ( getPortletList( ).toMap( ).get( strPortletId ) == null ) )
        {
            strError = MESSAGE_NO_DOCUMENT_PORTLET;
        }
        else if ( StringUtils.isBlank( strRssFileName ) || StringUtils.isBlank( strRssFileDescription ) )
        {
            strError = Messages.MANDATORY_FIELDS;
        }
        else
        {
            strError = validateFileName( strRssFileName, true );
        }

        if ( strError == null )
        {
            strError = validateFeedFormat( request );
        }

        if ( strError != null )
        {
            addError( strError, getLocale( ) );

            return redirectView( request, VIEW_CREATE_RSS_FILE_PORTLET );
        }

        RssGeneratedFile rssFile = new RssGeneratedFile( );
        rssFile.setPortletId( Integer.parseInt( strPortletId ) );
        rssFile.setName( getFileName( strRssFileName ) );
        rssFile.setState( STATE_OK );
        rssFile.setDescription( strRssFileDescription );
        rssFile.setWorkgroup( request.getParameter( PARAMETER_WORKGROUP_KEY ) );
        setFeedFormat( rssFile, request );

        RssGeneratorService.createFileRss( rssFile.getName( ), RssGeneratorService.createRssDocument( rssFile.getPortletId( ), rssFile.getDescription( ),
                rssFile.getEncoding( ), rssFile.getFeedType( ), rssFile.getMaxItems( ), request ) );
        RssGeneratedFileHome.create( rssFile );

        return redirectView( request, VIEW_MANAGE_RSS_FILES );
    }

    /**
     * Returns the creation form of a rss file of a resource
     *
     * @param model
     *            the model
     * @param request
     *            The Http request
     * @return Html form
     */
    @View( VIEW_CREATE_RSS_FILE_RESOURCE )
    public String getCreateRssFileResource( Models model, HttpServletRequest request )
    {
        IResourceRss resourceRss = _rssService.getResourceRssInstance( request.getParameter( PARAMETER_RSS_RESOURCE_KEY ), getLocale( ) );

        if ( resourceRss == null )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_RESOURCE, AdminMessage.TYPE_STOP ) );
        }

        if ( !resourceRss.contentResourceRss( ) )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_RESOURCE, AdminMessage.TYPE_STOP ) );
        }

        ReferenceList refListWorkGroups = AdminWorkgroupService.getUserWorkgroups( getUser( ), getLocale( ) );

        model.put( MARK_RSS_NAME, StringUtils.defaultString( request.getParameter( PARAMETER_PUSH_RSS_NAME ) ) );
        model.put( MARK_RSS_MAX_ITEMS, StringUtils.defaultString( request.getParameter( PARAMETER_PUSH_RSS_MAX_ITEMS ) ) );
        model.put( MARK_RSS_FEED_TYPE, StringUtils.defaultString( request.getParameter( PARAMETER_PUSH_RSS_FEED_TYPE ) ) );
        model.put( MARK_RSS_ENCODING, StringUtils.defaultString( request.getParameter( PARAMETER_PUSH_RSS_ENCODING ) ) );
        model.put( MARK_USER_WORKGROUP_LIST, refListWorkGroups );
        model.put( MARK_ENCODING_LIST, getRefList( _strEncodings ) );
        model.put( MARK_FEED_TYPE_LIST, getRefList( _strFeedTypes ) );
        model.put( MARK_WORKGROUP_SELECTED, StringUtils.defaultIfEmpty( request.getParameter( PARAMETER_WORKGROUP_KEY ), WORKGROUP_ALL ) );
        model.put( MARK_RESOURCE_RSS, resourceRss.getResourceRssType( ) );
        model.put( MARK_RESOURCE_RSS_CONFIG, resourceRss.getDisplayCreateConfigForm( request, getLocale( ) ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE, TEMPLATE_CREATE_PUSH_RSS_FILE_RESOURCE, model );
    }

    /**
     * Creates the rss file of a resource, or applies the parameters of its configuration form
     *
     * @param request
     *            The Http request
     * @return The url of the list, of the form or of the error
     */
    @Action( ACTION_CREATE_RSS_FILE_RESOURCE )
    public String doCreateRssFileResource( HttpServletRequest request )
    {
        String strResourceRssKey = request.getParameter( PARAMETER_RSS_RESOURCE_KEY );
        IResourceRss resourceRss = _rssService.getResourceRssInstance( strResourceRssKey, getLocale( ) );

        if ( resourceRss == null )
        {
            addError( MESSAGE_RESOURCE, getLocale( ) );

            return redirectView( request, VIEW_CREATE_RSS_FILE );
        }

        Map<String, String> mapFormParameters = getResourceFormParameters( request, resourceRss );

        if ( request.getParameter( PARAMETER_APPLY ) != null )
        {
            return redirect( request, VIEW_CREATE_RSS_FILE_RESOURCE, mapFormParameters );
        }

        String strRssFileName = request.getParameter( PARAMETER_PUSH_RSS_NAME );
        String strError = StringUtils.isBlank( strRssFileName ) ? Messages.MANDATORY_FIELDS : validateFileName( strRssFileName, true );

        if ( strError == null )
        {
            strError = validateFeedFormat( request );
        }

        if ( strError != null )
        {
            addError( strError, getLocale( ) );

            return redirect( request, VIEW_CREATE_RSS_FILE_RESOURCE, mapFormParameters );
        }

        String strConfigError = resourceRss.doValidateConfigForm( request, getLocale( ) );

        if ( strConfigError != null )
        {
            return redirect( request, strConfigError );
        }

        RssGeneratedFile rssFile = new RssGeneratedFile( );
        rssFile.setName( getFileName( strRssFileName ) );
        rssFile.setState( STATE_OK );
        rssFile.setDescription( resourceRss.getDescription( ) );
        rssFile.setWorkgroup( request.getParameter( PARAMETER_WORKGROUP_KEY ) );
        rssFile.setTypeResourceRss( resourceRss.getResourceRssType( ).getKey( ) );
        setFeedFormat( rssFile, request );
        RssGeneratedFileHome.create( rssFile );

        setResourceFeedFormat( resourceRss, rssFile );
        resourceRss.doSaveConfig( request, getLocale( ) );
        RssGeneratorService.createFileRss( rssFile.getName( ), FeedUtil.getFeed( resourceRss ) );

        return redirectView( request, VIEW_MANAGE_RSS_FILES );
    }

    /**
     * Returns the form to update a rss file
     *
     * @param model
     *            the model
     * @param request
     *            The Http request
     * @return The HTML form to update info
     * @throws AccessDeniedException
     *             if the user may not see the rss file
     */
    @View( VIEW_MODIFY_RSS_FILE )
    public String getModifyRssFile( Models model, HttpServletRequest request ) throws AccessDeniedException
    {
        RssGeneratedFile rss = getAuthorizedRssFile( request );

        if ( rss == null )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_RSS_FILE_NOT_FOUND, AdminMessage.TYPE_STOP ) );
        }

        if ( request.getParameter( PARAMETER_WORKGROUP_KEY ) != null )
        {
            rss.setWorkgroup( request.getParameter( PARAMETER_WORKGROUP_KEY ) );
        }

        if ( request.getParameter( PARAMETER_PUSH_RSS_NAME ) != null )
        {
            rss.setName( request.getParameter( PARAMETER_PUSH_RSS_NAME ) );
        }

        model.put( MARK_RSS_FILE, rss );
        model.put( MARK_USER_WORKGROUP_LIST, AdminWorkgroupService.getUserWorkgroups( getUser( ), getLocale( ) ) );
        model.put( MARK_ENCODING_LIST, getRefList( _strEncodings ) );
        model.put( MARK_FEED_TYPE_LIST, getRefList( _strFeedTypes ) );

        if ( isResourceFile( rss ) )
        {
            IResourceRss resourceRss = _rssService.getResourceRssInstance( rss.getTypeResourceRss( ), getLocale( ) );

            if ( resourceRss == null )
            {
                return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_RESOURCE, AdminMessage.TYPE_STOP ) );
            }

            resourceRss.setId( rss.getId( ) );
            model.put( MARK_RESOURCE_RSS, resourceRss.getResourceRssType( ) );
            model.put( MARK_RESOURCE_RSS_CONFIG, resourceRss.getDisplayModifyConfigForm( request, getLocale( ) ) );

            return getPage( PROPERTY_PAGE_TITLE_MODIFY, TEMPLATE_MODIFY_PUSH_RSS_FILE_RESOURCE, model );
        }

        model.put( MARK_PORTLET_LIST, getPortletList( ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY, TEMPLATE_MODIFY_PUSH_RSS_FILE_PORTLET, model );
    }

    /**
     * Modifies a rss file, of a portlet or of a resource
     *
     * @param request
     *            The Http request
     * @return The url of the list, of the form or of the error
     * @throws AccessDeniedException
     *             if the user may not see the rss file
     */
    @Action( ACTION_MODIFY_RSS_FILE )
    public String doModifyRssFile( HttpServletRequest request ) throws AccessDeniedException
    {
        RssGeneratedFile rssFile = getAuthorizedRssFile( request );

        if ( rssFile == null )
        {
            addError( MESSAGE_RSS_FILE_NOT_FOUND, getLocale( ) );

            return redirectView( request, VIEW_MANAGE_RSS_FILES );
        }

        return isResourceFile( rssFile ) ? doModifyRssFileResource( request, rssFile ) : doModifyRssFilePortlet( request, rssFile );
    }

    /**
     * Modifies the rss file of a portlet
     *
     * @param request
     *            The Http request
     * @param rssFile
     *            the rss file
     * @return The url of the list or of the form
     */
    private String doModifyRssFilePortlet( HttpServletRequest request, RssGeneratedFile rssFile )
    {
        String strPortletId = request.getParameter( PARAMETER_PUSH_RSS_PORTLET_ID );
        String strRssFileName = request.getParameter( PARAMETER_PUSH_RSS_NAME );
        String strError = null;

        if ( ( strPortletId == null ) || !strPortletId.matches( REGEX_ID ) || ( getPortletList( ).toMap( ).get( strPortletId ) == null ) )
        {
            strError = MESSAGE_NO_DOCUMENT_PORTLET;
        }
        else if ( StringUtils.isBlank( strRssFileName ) )
        {
            strError = Messages.MANDATORY_FIELDS;
        }
        else
        {
            strError = validateFileName( strRssFileName, false );
        }

        if ( strError == null )
        {
            strError = validateFeedFormat( request );
        }

        if ( strError != null )
        {
            addError( strError, getLocale( ) );

            return redirect( request, VIEW_MODIFY_RSS_FILE, PARAMETER_PUSH_RSS_ID, rssFile.getId( ) );
        }

        String strOldName = rssFile.getName( );
        rssFile.setPortletId( Integer.parseInt( strPortletId ) );
        rssFile.setName( getFileName( strRssFileName ) );
        rssFile.setState( RssGeneratedFileHome.checkRssFilePortlet( rssFile.getPortletId( ) ) ? STATE_OK : STATE_PORTLET_MISSING );
        rssFile.setWorkgroup( request.getParameter( PARAMETER_WORKGROUP_KEY ) );
        rssFile.setDescription( request.getParameter( PARAMETER_PUSH_RSS_DESCRIPTION ) );
        setFeedFormat( rssFile, request );
        RssGeneratedFileHome.update( rssFile );

        renameFile( strOldName, rssFile.getName( ) );
        RssGeneratorService.createFileRss( rssFile.getName( ), RssGeneratorService.createRssDocument( rssFile.getPortletId( ), rssFile.getDescription( ),
                rssFile.getEncoding( ), rssFile.getFeedType( ), rssFile.getMaxItems( ), request ) );

        return redirectView( request, VIEW_MANAGE_RSS_FILES );
    }

    /**
     * Modifies the rss file of a resource, or applies the parameters of its configuration form
     *
     * @param request
     *            The Http request
     * @param rssFile
     *            the rss file
     * @return The url of the list, of the form or of the error
     */
    private String doModifyRssFileResource( HttpServletRequest request, RssGeneratedFile rssFile )
    {
        IResourceRss resourceRss = _rssService.getResourceRssInstance( rssFile.getTypeResourceRss( ), getLocale( ) );

        if ( resourceRss == null )
        {
            addError( MESSAGE_RESOURCE, getLocale( ) );

            return redirectView( request, VIEW_MANAGE_RSS_FILES );
        }

        Map<String, String> mapFormParameters = getResourceFormParameters( request, resourceRss );
        mapFormParameters.put( PARAMETER_PUSH_RSS_ID, Integer.toString( rssFile.getId( ) ) );

        if ( request.getParameter( PARAMETER_APPLY ) != null )
        {
            return redirect( request, VIEW_MODIFY_RSS_FILE, mapFormParameters );
        }

        String strRssFileName = request.getParameter( PARAMETER_PUSH_RSS_NAME );
        String strError = StringUtils.isBlank( strRssFileName ) ? Messages.MANDATORY_FIELDS : validateFileName( strRssFileName, false );

        if ( strError == null )
        {
            strError = validateFeedFormat( request );
        }

        if ( strError != null )
        {
            addError( strError, getLocale( ) );

            return redirect( request, VIEW_MODIFY_RSS_FILE, mapFormParameters );
        }

        String strConfigError = resourceRss.doValidateConfigForm( request, getLocale( ) );

        if ( strConfigError != null )
        {
            return redirect( request, strConfigError );
        }

        String strOldName = rssFile.getName( );
        rssFile.setName( getFileName( strRssFileName ) );
        rssFile.setWorkgroup( request.getParameter( PARAMETER_WORKGROUP_KEY ) );
        rssFile.setDescription( resourceRss.getDescription( ) );
        setFeedFormat( rssFile, request );
        setResourceFeedFormat( resourceRss, rssFile );
        rssFile.setState( resourceRss.checkResource( ) ? STATE_OK : STATE_PORTLET_MISSING );
        RssGeneratedFileHome.update( rssFile );

        resourceRss.doUpdateConfig( request, getLocale( ) );
        renameFile( strOldName, rssFile.getName( ) );
        RssGeneratorService.createFileRss( rssFile.getName( ), FeedUtil.getFeed( resourceRss ) );

        return redirectView( request, VIEW_MANAGE_RSS_FILES );
    }

    /**
     * Confirms the removal of a rss file
     *
     * @param request
     *            The Http request
     * @return The url of the confirmation message
     * @throws AccessDeniedException
     *             if the user may not see the rss file
     */
    @View( value = VIEW_CONFIRM_REMOVE_RSS_FILE, securityTokenAction = ACTION_REMOVE_RSS_FILE )
    public String getConfirmRemoveRssFile( HttpServletRequest request ) throws AccessDeniedException
    {
        RssGeneratedFile rssFile = getAuthorizedRssFile( request );

        if ( rssFile == null )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_RSS_FILE_NOT_FOUND, AdminMessage.TYPE_STOP ) );
        }

        if ( !checkNoRssExternalFeed( rssFile, request ) )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_RSS_LINKED_FEED, AdminMessage.TYPE_STOP ) );
        }

        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_RSS_FILE ) );
        url.addParameter( PARAMETER_PUSH_RSS_ID, rssFile.getId( ) );

        Object [ ] messageArgs = {
            rssFile.getName( )
        };

        return redirect( request,
                AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_DELETE_RSS_FILE, messageArgs, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION ) );
    }

    /**
     * Process removal of a file
     *
     * @param request
     *            The Http request
     * @return String The url of the list
     * @throws AccessDeniedException
     *             if the user may not see the rss file
     */
    @Action( ACTION_REMOVE_RSS_FILE )
    public String doRemoveRssFile( HttpServletRequest request ) throws AccessDeniedException
    {
        RssGeneratedFile rssGeneratedFile = getAuthorizedRssFile( request );

        if ( rssGeneratedFile == null )
        {
            addError( MESSAGE_RSS_FILE_NOT_FOUND, getLocale( ) );

            return redirectView( request, VIEW_MANAGE_RSS_FILES );
        }

        if ( isResourceFile( rssGeneratedFile ) )
        {
            IResourceRss resourceRss = _rssService.getResourceRssInstance( rssGeneratedFile.getTypeResourceRss( ), getLocale( ) );

            if ( resourceRss != null )
            {
                resourceRss.deleteResourceRssConfig( rssGeneratedFile.getId( ) );
            }
        }

        RssGeneratedFileHome.remove( rssGeneratedFile.getId( ) );
        RssGeneratorService.deleteFileRss( rssGeneratedFile.getName( ), RssService.PLUGIN_NAME );

        return redirectView( request, VIEW_MANAGE_RSS_FILES );
    }

    /**
     * Loads the rss file named by the request and checks the workgroup of the user
     *
     * @param request
     *            the request
     * @return the rss file, or null when the request names no existing file
     * @throws AccessDeniedException
     *             if the user may not see the rss file
     */
    private RssGeneratedFile getAuthorizedRssFile( HttpServletRequest request ) throws AccessDeniedException
    {
        String strId = request.getParameter( PARAMETER_PUSH_RSS_ID );

        if ( !StringUtils.isNumeric( strId ) )
        {
            return null;
        }

        RssGeneratedFile rssFile = RssGeneratedFileHome.findByPrimaryKey( Integer.parseInt( strId ) );

        if ( rssFile != null && !AdminWorkgroupService.isAuthorized( rssFile, (User) getUser( ) ) )
        {
            throw new AccessDeniedException( MESSAGE_ACCESS_DENIED );
        }

        return rssFile;
    }

    /**
     * Tells whether a rss file is generated from a resource rather than from a portlet
     *
     * @param rssFile
     *            the rss file
     * @return true for the rss file of a resource
     */
    private static boolean isResourceFile( RssGeneratedFile rssFile )
    {
        return ( rssFile.getTypeResourceRss( ) != null ) && ( rssFile.getPortletId( ) == 0 );
    }

    /**
     * Returns the published document portlets the user may see
     *
     * @return the portlets
     */
    private ReferenceList getPortletList( )
    {
        Collection<Portlet> listInvolvedPortlets = _portletService.getAuthorizedPortletCollection( _publishingService.getPublishedPortlets( ), getUser( ) );
        ReferenceList referenceList = new ReferenceList( );

        for ( Portlet portlet : listInvolvedPortlets )
        {
            referenceList.addItem( portlet.getId( ), portlet.getName( ) );
        }

        return referenceList;
    }

    /**
     * Builds the parameters a resource form keeps when it is displayed again
     *
     * @param request
     *            the request
     * @param resourceRss
     *            the resource
     * @return the parameters
     */
    private Map<String, String> getResourceFormParameters( HttpServletRequest request, IResourceRss resourceRss )
    {
        Map<String, String> mapParameters = new HashMap<>( resourceRss.getParameterToApply( request ) );

        for ( String strParameter : Arrays.asList( PARAMETER_PUSH_RSS_NAME, PARAMETER_WORKGROUP_KEY, PARAMETER_RSS_RESOURCE_KEY, PARAMETER_PUSH_RSS_MAX_ITEMS,
                PARAMETER_PUSH_RSS_FEED_TYPE, PARAMETER_PUSH_RSS_ENCODING ) )
        {
            String strValue = request.getParameter( strParameter );

            if ( strValue != null )
            {
                mapParameters.put( strParameter, strValue );
            }
        }

        return mapParameters;
    }

    /**
     * Checks the length and, for a new file, the uniqueness of a file name
     *
     * @param strRssFileName
     *            the name typed by the user
     * @param bNew
     *            true for a new file
     * @return the key of the error message, or null when the name is valid
     */
    private String validateFileName( String strRssFileName, boolean bNew )
    {
        if ( strRssFileName.length( ) > _nNameMaxLength )
        {
            return MESSAGE_FILENAME_TOO_LONG;
        }

        if ( bNew && RssGeneratedFileHome.checkRssFileFileName( getFileName( strRssFileName ) ) )
        {
            return MESSAGE_FILENAME_ALREADY_EXISTS;
        }

        return null;
    }

    /**
     * Checks the feed type, the encoding and the maximum number of items of the request
     *
     * @param request
     *            the request
     * @return the key of the error message, or null when they are valid
     */
    private String validateFeedFormat( HttpServletRequest request )
    {
        String strFeedType = request.getParameter( PARAMETER_PUSH_RSS_FEED_TYPE );
        String strMaxItems = request.getParameter( PARAMETER_PUSH_RSS_MAX_ITEMS );

        if ( StringUtils.isBlank( strFeedType ) || !getValues( _strFeedTypes ).contains( strFeedType ) )
        {
            return MESSAGE_NO_FEED_TYPE;
        }

        if ( StringUtils.isBlank( request.getParameter( PARAMETER_PUSH_RSS_ENCODING ) ) )
        {
            return MESSAGE_NO_ENCODING;
        }

        if ( StringUtils.isNotBlank( strMaxItems ) && !StringUtils.isNumeric( strMaxItems.trim( ) ) )
        {
            return MESSAGE_MAX_ITEMS;
        }

        return null;
    }

    /**
     * Sets the feed type, the encoding and the maximum number of items of a rss file from a validated request
     *
     * @param rssFile
     *            the rss file
     * @param request
     *            the request
     */
    private static void setFeedFormat( RssGeneratedFile rssFile, HttpServletRequest request )
    {
        String strMaxItems = request.getParameter( PARAMETER_PUSH_RSS_MAX_ITEMS );
        rssFile.setMaxItems( StringUtils.isBlank( strMaxItems ) ? 0 : Integer.parseInt( strMaxItems.trim( ) ) );
        rssFile.setFeedType( request.getParameter( PARAMETER_PUSH_RSS_FEED_TYPE ) );
        rssFile.setEncoding( request.getParameter( PARAMETER_PUSH_RSS_ENCODING ) );
    }

    /**
     * Copies the identifier and the format of a rss file to its resource
     *
     * @param resourceRss
     *            the resource
     * @param rssFile
     *            the rss file
     */
    private static void setResourceFeedFormat( IResourceRss resourceRss, RssGeneratedFile rssFile )
    {
        resourceRss.setId( rssFile.getId( ) );
        resourceRss.setEncoding( rssFile.getEncoding( ) );
        resourceRss.setFeedType( rssFile.getFeedType( ) );
        resourceRss.setMaxItems( rssFile.getMaxItems( ) );
    }

    /**
     * Builds the file name of a name typed by the user
     *
     * @param strRssFileName
     *            the name typed by the user
     * @return the cleaned name, with the file extension
     */
    private String getFileName( String strRssFileName )
    {
        String strName = UploadUtil.cleanFileName( strRssFileName.trim( ) );

        return strName.toLowerCase( ).endsWith( _strFileType ) ? strName : ( strName + _strFileType );
    }

    /**
     * Removes the file of the former name of a renamed rss file
     *
     * @param strOldName
     *            the former name
     * @param strNewName
     *            the new name
     */
    private static void renameFile( String strOldName, String strNewName )
    {
        if ( ( strOldName != null ) && !strOldName.equals( strNewName ) )
        {
            RssGeneratorService.deleteFileRss( strOldName, RssService.PLUGIN_NAME );
        }
    }

    /**
     * Returns the values of a comma separated list
     *
     * @param strValues
     *            the comma separated values
     * @return the values
     */
    private static List<String> getValues( String strValues )
    {
        return Arrays.asList( strValues.split( "," ) );
    }

    /**
     * Builds the reference list of the values of a comma separated list
     *
     * @param strValues
     *            the comma separated values
     * @return the ReferenceList
     */
    private static ReferenceList getRefList( String strValues )
    {
        ReferenceList refList = new ReferenceList( );

        for ( String strValue : getValues( strValues ) )
        {
            refList.addItem( strValue, strValue );
        }

        return refList;
    }

    /**
     * Utility method to build the URL of the given rssFile file
     *
     * @param strNameRssFile
     *            The name of the file
     * @param request
     *            The HttpServletRequest
     * @return the URL to get the rssFile file
     */
    private String getRssFileUrl( String strNameRssFile, HttpServletRequest request )
    {
        return AppPathService.getBaseUrl( request ) + Strings.CS.removeStart( _strPluginWarehouse, "/" ) + "/" + _strStorageDirectoryName + "/" + strNameRssFile;
    }

    /**
     * Checks whether the generated rss file is linked as an external rss feed
     *
     * @param rssFile
     *            the rss file
     * @param request
     *            The HttpRequest
     * @return true when no external feed reads the file
     */
    private boolean checkNoRssExternalFeed( RssGeneratedFile rssFile, HttpServletRequest request )
    {
        return RssFeedHome.checkUrlNotUsed( getRssFileUrl( rssFile.getName( ), request ) );
    }
}
