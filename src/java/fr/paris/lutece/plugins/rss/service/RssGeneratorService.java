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
package fr.paris.lutece.plugins.rss.service;

import fr.paris.lutece.plugins.document.business.Document;
import fr.paris.lutece.plugins.rss.business.RssGeneratedFile;
import fr.paris.lutece.plugins.rss.business.RssGeneratedFileHome;
import fr.paris.lutece.plugins.rss.web.FeedUtil;
import fr.paris.lutece.portal.business.rss.FeedResource;
import fr.paris.lutece.portal.business.rss.FeedResourceImage;
import fr.paris.lutece.portal.business.rss.FeedResourceItem;
import fr.paris.lutece.portal.business.rss.IFeedResource;
import fr.paris.lutece.portal.business.rss.IFeedResourceImage;
import fr.paris.lutece.portal.business.rss.IFeedResourceItem;
import fr.paris.lutece.portal.service.portal.PortalService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.html.HtmlTemplate;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;


/**
 * This class provides utilities to create RSS documents.
 */
public final class RssGeneratorService
{
    /* Constants */

    /**
     * The path which points the rss files are stored
     */
    public static final String PROPERTY_RSS_STORAGE_FOLDER_PATH = "rss.storage.folder.path";
    public static final String PROPERTY_STORAGE_DIRECTORY_NAME = "rss.storage.directory.name";
    private static final String TEMPLATE_PUSH_RSS_XML = "admin/plugins/rss/rss_xml.html";
    private static final String TEMPLATE_FEED_LINK = "admin/plugins/rss/feed_link.html";
    private static final String MARK_ITEM_LIST = "itemList";
    private static final String MARK_RSS_SITE_NAME = "site_name";
    private static final String MARK_RSS_FILE_LANGUAGE = "file_language";
    private static final String MARK_RSS_SITE_URL = "site_url";
    private static final String MARK_DOCUMENT_ID = "document_id";
    private static final String MARK_RSS_SITE_DESCRIPTION = "site_description";
    private static final String MARK_ID_PORTLET = "id_portlet";
    private static final String PROPERTY_SITE_LANGUAGE = "rss.language";
    private static final String CONSTANT_IMAGE_RSS = "/images/local/skin/plugins/rss/rss-image.png";

    /**
     * Private constructor
     */
    private RssGeneratorService(  )
    {
    }

    ///////////////////////////////////////////////////////////////////////////
    // Create RSS document

    /**
     * Creates the push RSS document corresponding to the given portlet
     *
     * @param nIdPortlet the portlet id for wich the file is created
     * @param strRssFileDescription the Description
     * @param strEncoding encoding
     * @param strFeedType feed type
     * @param nMaxItems max items
     * @return String the XML content of the RSS document
     */
    public static String createRssDocument( int nIdPortlet, String strRssFileDescription, String strEncoding,
        String strFeedType, int nMaxItems )
    {
        return createRssDocument( nIdPortlet, strRssFileDescription, strEncoding, strFeedType, nMaxItems, null );
    }

    /**
     * Creates the push RSS document corresponding to the given portlet
     *
     * @param nIdPortlet the portlet id for wich the file is created
     * @param strRssFileDescription the Description
     * @param strEncoding encoding
     * @param strFeedType feed type
     * @param nMaxItems max items
     * @param request The HTTP request
     * @return String the XML content of the RSS document
     */
    public static String createRssDocument( int nIdPortlet, String strRssFileDescription, String strEncoding,
        String strFeedType, int nMaxItems, HttpServletRequest request )
    {
        String strRssFileSiteName = PortalService.getSiteName();
        String strRssFileLanguage = AppPropertiesService.getProperty( PROPERTY_SITE_LANGUAGE );
        String strIdPortlet = Integer.toString( nIdPortlet );
        String strSiteUrl = AppPathService.getProdUrl( request );

        IFeedResource resource = new FeedResource(  );
        resource.setTitle( strRssFileSiteName );
        resource.setLanguage( strRssFileLanguage );
        resource.setLink( strSiteUrl );
        resource.setDescription( strRssFileDescription );

        IFeedResourceImage image = new FeedResourceImage(  );
        image.setLink( strSiteUrl );
        image.setTitle( strRssFileDescription );
        image.setUrl( strSiteUrl + CONSTANT_IMAGE_RSS );
        resource.setImage( image );

        Locale locale = new Locale( strRssFileLanguage );

        List<Document> listDocuments = RssGeneratedFileHome.findDocumentsByPortlet( nIdPortlet );

        List<IFeedResourceItem> listItems = new ArrayList<IFeedResourceItem>(  );

        for ( Document document : listDocuments )
        {
            IFeedResourceItem item = new FeedResourceItem(  );
            item.setTitle( document.getTitle(  ) );
            item.setDescription( document.getSummary(  ) );
            item.setDate( document.getDateModification(  ) );

            // link creation
            Map<String, Object> model = new HashMap<String, Object>(  );
            model.put( MARK_ID_PORTLET, strIdPortlet );
            model.put( MARK_DOCUMENT_ID, document.getId(  ) );
            model.put( MARK_RSS_SITE_URL, strSiteUrl );

            HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_FEED_LINK, locale, model );
            String strLink = template.getHtml(  );
            item.setGUID( strLink );
            item.setLink( strLink );

            listItems.add( item );
        }

        resource.setItems( listItems );

        return FeedUtil.getFeed( resource, strFeedType, strEncoding, nMaxItems );
    }

    /**
     * Creates the push RSS document corresponding to the given portlet
     *
     * @param listRssItem The RSS List
     * @param strRssFileDescription the Description
     * @return String the XML content of the RSS document
     */
    public static String createRssDocument( List listRssItem, String strRssFileDescription )
    {
        HashMap model = new HashMap(  );

        // Update the head of the document
        String strRssFileSiteName = PortalService.getSiteName();
        String strRssFileLanguage = AppPropertiesService.getProperty( PROPERTY_SITE_LANGUAGE );

        String strSiteUrl = AppPathService.getProdUrl( (HttpServletRequest) null );
        model.put( MARK_RSS_SITE_NAME, strRssFileSiteName );
        model.put( MARK_RSS_FILE_LANGUAGE, strRssFileLanguage );
        model.put( MARK_RSS_SITE_URL, strSiteUrl );
        model.put( MARK_RSS_SITE_DESCRIPTION, strRssFileDescription );

        // Find documents by portlet
        //List listDocuments = RssGeneratedFileHome.findDocumentsByPortlet( nIdPortlet );
        //The date must respect RFC-822 date-time
        model.put( MARK_ITEM_LIST, listRssItem );

        Locale locale = new Locale( strRssFileLanguage );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_PUSH_RSS_XML, locale, model );

        return template.getHtml(  );
    }

    /**
     * Creates the pushrss file in the directory
     *
     * @param strRssFileName The file's name that must be deleted
     * @param strRssDocument The content of the new RSS file
     */
    public static void createFileRss( String strRssFileName, String strRssDocument )
    {
        FileWriter fileRssWriter;

        try
        {
            // fetches the pushRss directory path
            String strFolderPath = AppPathService.getPath( RssGeneratorService.PROPERTY_RSS_STORAGE_FOLDER_PATH, "" );

            // Test if the pushRss directory exist and create it if it doesn't exist
            if ( !new File( strFolderPath ).exists(  ) )
            {
                File fileFolder = new File( strFolderPath );
                fileFolder.mkdir(  );
            }

            // Creates a temporary RSS file
            String strFileRss = AppPathService.getPath( RssGeneratorService.PROPERTY_RSS_STORAGE_FOLDER_PATH, "" ) +
                strRssFileName;
            String strFileDirectory = AppPathService.getPath( RssGeneratorService.PROPERTY_RSS_STORAGE_FOLDER_PATH, "" );
            File fileRss = new File( strFileRss );
            File fileRssDirectory = new File( strFileDirectory );
            File fileRssTemp = File.createTempFile( "tmp", null, fileRssDirectory );
            fileRssWriter = new FileWriter( fileRssTemp );
            fileRssWriter.write( strRssDocument );
            fileRssWriter.flush(  );
            fileRssWriter.close(  );

            // Deletes the file if the file exists and renames the temporary file into the file
            if ( new File( strFileRss ).exists(  ) )
            {
                File file = new File( strFileRss );
                file.delete(  );
            }

            fileRssTemp.renameTo( fileRss );
        }
        catch ( IOException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }
        catch ( NullPointerException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }
    }

    /**
     * Deletes the pushrss file in the directory
     *
     * @param strRssFileName The name of the RSS file
     * @param strPluginName The plugin's name
     */
    public static void deleteFileRss( String strRssFileName, String strPluginName )
    {
        try
        {
            // Define pushRss directory
            String strFileRss = AppPathService.getPath( RssGeneratorService.PROPERTY_RSS_STORAGE_FOLDER_PATH, "" ) +
                strRssFileName;

            // Delete the file if file exists
            if ( new File( strFileRss ).exists(  ) )
            {
                File file = new File( strFileRss );
                file.delete(  );
            }
        }
        catch ( NullPointerException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }
    }

    /**
     * Regenerate all Rss files in the file system
     * @return Execution logs
     */
    public static String generateAllRss(  )
    {
        StringBuilder sb = new StringBuilder( "Regenerate all RSS files from the database to the filesystem.\n" );
        List<RssGeneratedFile> list = RssGeneratedFileHome.getRssFileList(  );

        for ( RssGeneratedFile file : list )
        {
            if ( file.getPortletId(  ) != 0 )
            {
                createFileRss( file.getName(  ), createRssDocument( file.getPortletId(  ), file.getDescription(  ), file.getEncoding(  ),
                    file.getFeedType(  ), file.getMaxItems(  ) ) );
                sb.append( "\nFile  " ).append( file.getName(  ) ).append( " regenerated.\n" );
            }
        }

        AppLogService.info( sb.toString(  ) );

        return sb.toString(  );
    }
}
