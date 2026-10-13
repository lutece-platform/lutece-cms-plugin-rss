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


import java.util.Collection;
import java.util.List;


/**
 * IRssGeneratedFileDAO
 */
public interface IRssGeneratedFileDAO
{
    /**
     * Checks if a rssFile file exist for this portlet identifier
     * @param nPortletId The identifier of the portlet
     * @return true if a rssFile file exist for this portlet
     */
    boolean checkExistPushrssByPortlet( int nPortletId );

    /**
     * Tests if a push rss file exist with the same name
     * @param strRssFileName The push RSS file's name
     * @return true if the name already exist
     */
    boolean checkRssFileFileName( String strRssFileName );

    /**
     * Tests if the portlet has not been deleted before update
     * @param nPortletId The portlet identifier for this RSS file
     * @return true il the portlet exist
     */
    boolean checkRssFilePortlet( int nPortletId );

    /**
     * Deletes a record from the table
     * @param nRssFileId The identifier of the rssFile object
     */
    void delete( int nRssFileId );

    /**
     * Inserts a new record in the table rss_generation.
     * @param rssFile The Instance of the object RssFile
     */
    void insert( RssGeneratedFile rssFile );

    /**
     * Loads the data of a rssFile file from the table
     * @param nRssFileId The identifier of the rssFile file
     * @return The RSS file
     */
    RssGeneratedFile load( int nRssFileId );

    /**
     * Returns a collection of all portlets
     * @return the portlets in form of Collection
     */
    Collection selectAllRssPortlets(  );

    /**
     * Returns all the documents of a portlet whose identifier is specified in parameter
     * @param nPortletId the identifier of the portlet
     * @return List of documents
     */
    List selectDocumentsByPortlet( int nPortletId );

    /**
     * Returns the list of the rss_generation files
     * @return the List of rss files
     */
    List<RssGeneratedFile> selectRssFileList(  );

    /**
     * Reads the portlet's name
     * @param nPortletId the identifier of the portlet
     * @return The name of the portlet
     */
    String selectRssFilePortletName( int nPortletId );

    /**
     * Returns a collection of portlets for which there isn't any RSS files
     * @return the portlets in form of Collection
     */
    Collection selectRssPortlets(  );

    /**
     * Updates the record in the table rss_generation
     * @param rssFile The Instance of the object rssFile
     */
    void store( RssGeneratedFile rssFile );

    /**
     * Updates the rssFile's state
     * @param rssFile The Instance of the object rssFile
     */
    void updateState( RssGeneratedFile rssFile );
}
