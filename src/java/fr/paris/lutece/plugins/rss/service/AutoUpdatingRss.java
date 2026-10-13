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

import fr.paris.lutece.plugins.rss.business.RssGeneratedFile;
import fr.paris.lutece.plugins.rss.business.RssGeneratedFileHome;
import fr.paris.lutece.plugins.rss.web.FeedUtil;
import fr.paris.lutece.portal.business.rss.IResourceRss;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.util.filesystem.DirectoryNotFoundException;
import fr.paris.lutece.util.filesystem.FileSystemUtil;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import jakarta.enterprise.inject.spi.CDI;


/**
 * This class provides management methods for auto updating pushrss files
 */
public final class AutoUpdatingRss
{
    ////////////////////////////////////////////////////////////////////////////
    // Constants
    private static final String UPDATING = "Updating rss file";

    /**
     * Constructor
     */
    private AutoUpdatingRss(  )
    {
    }

    /**
     * Processes the Updating RSS's files method
     * @param plugin the plugin
     * @return The result of this process (strLogs)
     */
    public static synchronized String processUpdatingRssFileFile( Plugin plugin )
    {
        // Get all the pushrss files saved in database
        List<RssGeneratedFile> listPushRssDatabase = RssGeneratedFileHome.getRssFileList(  );
        List<String> listFileNames = new ArrayList<String>(  );

        for ( RssGeneratedFile generatedFile : listPushRssDatabase )
        {
            listFileNames.add( generatedFile.getName(  ) );
        }

        //String buffer for building the response page
        StringBuffer strLogs = new StringBuffer(  );

        // fetch the pushRss directory path
        String strFolderPath = AppPathService.getPath( RssGeneratorService.PROPERTY_RSS_STORAGE_FOLDER_PATH, "" );

        // Test if the pushRss directory exist and create it if it doesn't exist
        if ( !new File( strFolderPath ).exists(  ) )
        {
            File fileFolder = new File( strFolderPath );
            fileFolder.mkdir(  );
        }

        List<File> listFiles;

        try
        {
            listFiles = FileSystemUtil.getFiles( strFolderPath, "" );

            if ( listFiles.size(  ) != 0 )
            {
                for ( File file : listFiles )
                {
                    if ( !listFileNames.contains( file.getName(  ) ) )
                    {
                        file.delete(  );
                    }
                }
            }
        }
        catch ( DirectoryNotFoundException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }

        // Create again all the pushrss object saved in the database
        for ( RssGeneratedFile generatedFile : listPushRssDatabase )
        {
            try
            {
                // Call the create method
                String strRss = null;

                if ( ( generatedFile.getPortletId(  ) == 0 ) && ( generatedFile.getTypeResourceRss(  ) != null ) )
                {
                    IResourceRss resourceRss = CDI.current( ).select( RssService.class ).get( ).getResourceRssInstance( generatedFile.getTypeResourceRss(  ),
                            null );
                    resourceRss.setId( generatedFile.getId(  ) );
                    resourceRss.setMaxItems( generatedFile.getMaxItems(  ) );
                    resourceRss.setEncoding( generatedFile.getEncoding(  ) );
                    resourceRss.setFeedType( generatedFile.getFeedType(  ) );

                    strRss = FeedUtil.getFeed( resourceRss );
                }
                else
                {
                    strRss = RssGeneratorService.createRssDocument( generatedFile.getPortletId(  ),
                            generatedFile.getDescription(  ), generatedFile.getEncoding(  ),
                            generatedFile.getFeedType(  ), generatedFile.getMaxItems(  ) );
                }

                // Call the create file method
                RssGeneratorService.createFileRss( generatedFile.getName(  ), strRss );

                // Update the database with the new pushrss object
                RssGeneratedFileHome.update( generatedFile );
                strLogs.append( UPDATING );
            }
            catch ( Exception e )
            {
                AppLogService.error( "Error processing AutoUpdatingRssFile ", e );
            }
        }

        return strLogs.toString(  );
    }
}
