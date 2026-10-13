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
package fr.paris.lutece.plugins.rss.business;

import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.portal.business.portlet.PortletHome;
import jakarta.enterprise.inject.spi.CDI;

import java.util.Collection;
import java.util.List;


/**
 * This class provides instances management methods (create, find, ...)
 * for rss objects.
 */
public final class RssGeneratedFileHome
{
    // Static variable pointed at the DAO instance
    private static IRssGeneratedFileDAO _dao = CDI.current( ).select( IRssGeneratedFileDAO.class ).get( );

    /**
     * Creates a new RssFileHome object.
     */
    private RssGeneratedFileHome(  )
    {
    }

    /**
     * Loads the data of a pushrss object from the table
     *
     * @return pushRss The instance of an RssFile's object
     * @param nKey The identifier of the pushrss file
     */
    public static RssGeneratedFile findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey );
    }

    /**
     * Updates the record in the rss_generation table
     *
     * @param pushRss The Instance of the object RssFile
     */
    public static void update( RssGeneratedFile pushRss )
    {
        _dao.store( pushRss );
    }

    /**
     * Updates pushrss's state in the table and doesn't modify the update date
     *
     * @param pushRss The Instance of the object RssFile
     */
    public static void updateState( RssGeneratedFile pushRss )
    {
        _dao.updateState( pushRss );
    }

    /**
     * Creates a new push RSS object in the database.
     *
     * @return pushRss The object
     * @param pushRss The Instance of the object RssFile
     */
    public static RssGeneratedFile create( RssGeneratedFile pushRss )
    {
        _dao.insert( pushRss );

        return pushRss;
    }

    /**
     * Removes the push RSS object whose identifier is specified in parameter
     *
     * @param nRssFileId The identifier of the pushRss object
     */
    public static void remove( int nRssFileId )
    {
        _dao.delete( nRssFileId );
    }

    /**
     * Returns the list of the RSS Files
     *
     * @return the list in form of a List object
     */
    public static List<RssGeneratedFile> getRssFileList(  )
    {
        List<RssGeneratedFile> rssGeneratedFileList = _dao.selectRssFileList(  );

        for ( RssGeneratedFile rssGeneratedFile : rssGeneratedFileList )
        {
            if ( rssGeneratedFile.getPortletId(  ) != 0 )
            {
                Portlet portlet = PortletHome.findByPrimaryKey( rssGeneratedFile.getPortletId(  ) );

                if ( portlet != null )
                {
                    rssGeneratedFile.setPortletName( portlet.getName(  ) );
                }
            }
        }

        return rssGeneratedFileList;
    }

    /**
     * Reads the portlet's name
     *
     * @return The name of the portlet
     * @param nPortletId the identifier of the portlet
     */
    public static String getPortletName( int nPortletId )
    {
        return _dao.selectRssFilePortletName( nPortletId );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns a collection of portlets for which there isn't any RSS files
     *
     * @return the portlets in form of Collection
     */
    public static Collection findArticlesPortlets(  )
    {
        return _dao.selectRssPortlets(  );
    }

    /**
     * Returns a collection of portlets for which there isn't any RSS files
     *
     * @return the portlets in form of Collection
     */
    public static Collection findAllArticlesPortlets(  )
    {
        return _dao.selectAllRssPortlets(  );
    }

    /**
     * Returns all the documents of a portlet whose identifier is specified in parameter
     *
     * @param nPortletId the identifier of the portlet
     * @return List of articles objects
     */
    public static List findDocumentsByPortlet( int nPortletId )
    {
        return _dao.selectDocumentsByPortlet( nPortletId );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Controllers

    /**
     * Checks if a pushrss object exist for this portlet identifier
     *
     * @return true if a pushRss file exist for this portlet
     * @param nPortletId The identifier of the portlet
     */
    public static boolean checkExistPushrssByPortlet( int nPortletId )
    {
        return _dao.checkExistPushrssByPortlet( nPortletId );
    }

    /**
     * Tests before create if a push rss object exist with the same name
     *
     * @return true if the name already exists
     * @param strRssFileName The push RSS file's name
     */
    public static boolean checkRssFileFileName( String strRssFileName )
    {
        return _dao.checkRssFileFileName( strRssFileName );
    }

    /**
     * Tests if the portlet has not been deleted
     *
     * @return true if the name already exists
     * @param nPortletId The portlet identifier for this RSS file
     */
    public static boolean checkRssFilePortlet( int nPortletId )
    {
        return _dao.checkRssFilePortlet( nPortletId );
    }
}
