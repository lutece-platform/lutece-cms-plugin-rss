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

import fr.paris.lutece.plugins.document.business.Document;
import fr.paris.lutece.plugins.document.business.portlet.DocumentListPortletHome;
import fr.paris.lutece.portal.business.portlet.PortletImpl;
import fr.paris.lutece.portal.business.stylesheet.StyleSheet;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * This class provides Data Access methods for RSS objects
 */
public final class RssGeneratedFileDAO implements IRssGeneratedFileDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = " SELECT max(id_rss) FROM rss_generation ";
    private static final String SQL_QUERY_INSERT = "  INSERT INTO rss_generation ( id_rss, id_portlet, name, state, date_update, description,workgroup_key, type_resource_rss, max_items, feed_type, feed_encoding )" +
        " VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_QUERY_DELETE = " DELETE FROM rss_generation WHERE id_rss = ?  ";

    /** state update */
    private static final String SQL_QUERY_UPDATE = " UPDATE rss_generation SET state = ?, date_update = ? WHERE id_rss = ? ";
    private static final String SQL_QUERY_RSS_FILE_LIST = "SELECT a.id_rss, a.id_portlet, a.name, " +
        "a.state, a.date_update, a.description, a.workgroup_key,a.type_resource_rss, a.max_items, a.feed_type, a.feed_encoding FROM rss_generation as a " +
        "LEFT JOIN core_portlet as b ON (a.id_portlet=b.id_portlet) WHERE a.id_portlet=b.id_portlet OR a.type_resource_rss IS NOT NULL ORDER BY a.name ASC ";
    private static final String SQL_QUERY_UPDATE_RSS_FILE = " UPDATE rss_generation SET id_portlet = ?, name = ?, state = ?, date_update = ?, description =?, " +
        " workgroup_key =?, type_resource_rss=? , max_items = ? , feed_type = ? , feed_encoding = ? WHERE id_rss = ?";
    private static final String SQL_QUERY_SELECT_GENERATE_FILE = " SELECT id_portlet, name, state, date_update,description,workgroup_key,type_resource_rss, max_items,feed_type,feed_encoding" +
        " FROM rss_generation" + " WHERE id_rss = ?";
    private static final String SQL_QUERY_EXIST_RSS_FILE = " SELECT id_rss, name, state, date_update" +
        " FROM rss_generation" + " WHERE id_portlet = ?";
    private static final String SQL_QUERY_FILE_NAME_EXIST = "SELECT id_rss " + "FROM rss_generation " +
        "WHERE name = ?";
    private static final String SQL_QUERY_SELECT_RSS_PORTLET = " SELECT a.id_portlet, a.name, a.date_update " +
        " FROM portlet a LEFT JOIN rss_generation b ON a.id_portlet=b.id_portlet " +
        " WHERE b.id_portlet IS NULL AND a.id_portlet_type = ? ";
    private static final String SQL_QUERY_SELECT_PORTLET_NAME = " SELECT name " + " FROM portlet " +
        " WHERE id_portlet = ? ";
    private static final String SQL_QUERY_SELECT_ALL_RSS = " SELECT portlet.id_portlet, portlet.name, portlet.date_update " +
        "FROM portlet WHERE  portlet.id_portlet_type = ? ";
    private static final String SQL_QUERY_CHECK_PORTLET_EXISTENCE = "SELECT id_portlet" + " FROM core_portlet" +
        " WHERE id_portlet = ?";
    private static final String SQL_QUERY_SELECT_DOCUMENT_BY_PORTLET = "SELECT a.id_document , a.code_document_type ," +
        "a.date_creation ,a.date_modification, a.title, a.document_summary, a.xml_validated_content " +
        "FROM document a INNER JOIN document_published  b ON a.id_document=b.id_document " +
        "WHERE b.id_portlet = ? AND b.status = 0 ORDER BY b.date_publishing DESC";
    private static final String SQL_QUERY_SELECT_XSL_FILE = " SELECT id_stylesheet , description , file_name, source " +
        " FROM stylesheet " + " WHERE id_stylesheet = ? ";

    /**
     * Calculates a new primary key to add a new record
     * @return The new key.
     */
    private int newPrimaryKey(  )
    {
        int nKey;
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK ) )
        {    
            daoUtil.executeQuery(  );
    
            if ( daoUtil.next(  ) )
            {
                nKey = daoUtil.getInt( 1 ) + 1;
            }
            else
            {
                // If the table is empty
                nKey = 1;
            }
        }

        return nKey;
    }

    /**
     * Inserts a new record in the table rss_generation.
     * @param rssFile The Instance of the object RssFile
     */
    public void insert( RssGeneratedFile rssFile )
    {
        int nNewPrimaryKey = newPrimaryKey(  );
        rssFile.setId( nNewPrimaryKey );

        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT ) )
        {
            daoUtil.setInt( 1, rssFile.getId(  ) );
            daoUtil.setInt( 2, rssFile.getPortletId(  ) );
            daoUtil.setString( 3, rssFile.getName(  ) );
            daoUtil.setInt( 4, rssFile.getState(  ) );
            daoUtil.setTimestamp( 5, new java.sql.Timestamp( new java.util.Date(  ).getTime(  ) ) );
            daoUtil.setString( 6, rssFile.getDescription(  ) );
            daoUtil.setString( 7, rssFile.getWorkgroup(  ) );
            daoUtil.setString( 8, rssFile.getTypeResourceRss(  ) );
            daoUtil.setInt( 9, rssFile.getMaxItems(  ) );
            daoUtil.setString( 10, rssFile.getFeedType(  ) );
            daoUtil.setString( 11, rssFile.getEncoding(  ) );
            daoUtil.executeUpdate(  );
        }
    }

    /**
     * Deletes a record from the table
     * @param nRssFileId The identifier of the rssFile object
     */
    public void delete( int nRssFileId )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE ) )
        {
            daoUtil.setInt( 1, nRssFileId );
            daoUtil.executeUpdate(  );
        }
    }

    /**
     * Updates the record in the table rss_generation
     * @param rssFile The Instance of the object rssFile
     */
    public void store( RssGeneratedFile rssFile )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_RSS_FILE ) )
        {    
            daoUtil.setInt( 1, rssFile.getPortletId(  ) );
            daoUtil.setString( 2, rssFile.getName(  ) );
            daoUtil.setInt( 3, rssFile.getState(  ) );
            daoUtil.setTimestamp( 4, new java.sql.Timestamp( new java.util.Date(  ).getTime(  ) ) );
            daoUtil.setString( 5, rssFile.getDescription(  ) );
            daoUtil.setString( 6, rssFile.getWorkgroup(  ) );
            daoUtil.setString( 7, rssFile.getTypeResourceRss(  ) );
            daoUtil.setInt( 8, rssFile.getMaxItems(  ) );
            daoUtil.setString( 9, rssFile.getFeedType(  ) );
            daoUtil.setString( 10, rssFile.getEncoding(  ) );
            daoUtil.setInt( 11, rssFile.getId(  ) );
    
            daoUtil.executeUpdate(  );
        }
    }

    /**
     * Updates the rssFile's state
     * @param rssFile The Instance of the object rssFile
     */
    public void updateState( RssGeneratedFile rssFile )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE ) )
        {
            daoUtil.setInt( 1, rssFile.getState(  ) );
            //FIXME The date should be rssFile.getUpdateDate(  )
            daoUtil.setTimestamp( 2, new java.sql.Timestamp( new java.util.Date(  ).getTime(  ) ) );
            daoUtil.setInt( 3, rssFile.getId(  ) );
            daoUtil.executeUpdate(  );
        }
    }

    /**
     * Loads the data of a rssFile file from the table
     * @param nRssFileId The identifier of the rssFile file
     * @return The RSS generated file
     */
    public RssGeneratedFile load( int nRssFileId )
    {
        RssGeneratedFile rssFile = null;

        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_GENERATE_FILE ) )
        {
            daoUtil.setInt( 1, nRssFileId );
            daoUtil.executeQuery(  );
    
            if ( daoUtil.next(  ) )
            {
                rssFile = new RssGeneratedFile(  );
                rssFile.setId( nRssFileId );
                rssFile.setPortletId( daoUtil.getInt( 1 ) );
                rssFile.setName( daoUtil.getString( 2 ) );
                rssFile.setState( daoUtil.getInt( 3 ) );
                rssFile.setUpdateDate( daoUtil.getTimestamp( 4 ) );
                rssFile.setDescription( daoUtil.getString( 5 ) );
                rssFile.setWorkgroup( daoUtil.getString( 6 ) );
                rssFile.setTypeResourceRss( daoUtil.getString( 7 ) );
                rssFile.setMaxItems( daoUtil.getInt( 8 ) );
                rssFile.setFeedType( daoUtil.getString( 9 ) );
                rssFile.setEncoding( daoUtil.getString( 10 ) );
            }

        }

        return rssFile;
    }

    /**
     * Checks if a rssFile file exist for this portlet identifier
     * @param nPortletId The identifier of the portlet
     * @return true if a rssFile file exist for this portlet
     */
    public boolean checkExistPushrssByPortlet( int nPortletId )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_EXIST_RSS_FILE ) )
        {
            daoUtil.setInt( 1, nPortletId );
            daoUtil.executeQuery(  );
    
            if ( !daoUtil.next(  ) )
            {
                daoUtil.free(  );
    
                return false;
            }

        }

        return true;
    }

    /**
     * Tests if a push rss file exist with the same name
     * @param strRssFileName The push RSS file's name
     * @return true if the name already exist
     */
    public boolean checkRssFileFileName( String strRssFileName )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FILE_NAME_EXIST ) )
        {
            daoUtil.setString( 1, strRssFileName );
            daoUtil.executeQuery(  );
    
            if ( !daoUtil.next(  ) )
            {
                daoUtil.free(  );
    
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the list of the rss_generation files
     * @return the List of rss files
     */
    public List<RssGeneratedFile> selectRssFileList(  )
    {
        List<RssGeneratedFile> list = new ArrayList<>(  );
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_RSS_FILE_LIST ) )
        {
            daoUtil.executeQuery(  );
    
            while ( daoUtil.next(  ) )
            {
                RssGeneratedFile rssFile = new RssGeneratedFile(  );
                rssFile.setId( daoUtil.getInt( 1 ) );
                rssFile.setPortletId( daoUtil.getInt( 2 ) );
                rssFile.setName( daoUtil.getString( 3 ) );
                rssFile.setState( daoUtil.getInt( 4 ) );
                rssFile.setUpdateDate( daoUtil.getTimestamp( 5 ) );
                rssFile.setDescription( daoUtil.getString( 6 ) );
                rssFile.setWorkgroup( daoUtil.getString( 7 ) );
                rssFile.setTypeResourceRss( daoUtil.getString( 8 ) );
                rssFile.setMaxItems( daoUtil.getInt( 9 ) );
                rssFile.setFeedType( daoUtil.getString( 10 ) );
                rssFile.setEncoding( daoUtil.getString( 11 ) );
    
                list.add( rssFile );
            }
        }

        return list;
    }

    /**
     * Returns a collection of portlets for which there isn't any RSS files
     * @return the portlets in form of Collection
     */
    public Collection<PortletImpl> selectRssPortlets(  )
    {
        ArrayList<PortletImpl> list = new ArrayList<>(  );
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_RSS_PORTLET ) )
        {
            daoUtil.executeQuery(  );
    
            while ( daoUtil.next(  ) )
            {
                PortletImpl portlet = new PortletImpl(  );
                portlet.setId( daoUtil.getInt( 1 ) );
                portlet.setName( daoUtil.getString( 2 ) );
                portlet.setDateUpdate( daoUtil.getTimestamp( 3 ) );
                list.add( portlet );
            }

        }

        return list;
    }

    /**
     * Returns a collection of all portlets
     * @return the portlets in form of Collection
     */
    public Collection<PortletImpl> selectAllRssPortlets(  )
    {
        ArrayList<PortletImpl> list = new ArrayList<>(  );
        String strPortletTypeId = DocumentListPortletHome.getInstance(  ).getPortletTypeId(  );
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ALL_RSS ) )
        {    
            daoUtil.setString( 1, strPortletTypeId );
    
            daoUtil.executeQuery(  );
    
            while ( daoUtil.next(  ) )
            {
                PortletImpl portlet = new PortletImpl(  );
                portlet.setId( daoUtil.getInt( 1 ) );
                portlet.setName( daoUtil.getString( 2 ) );
                portlet.setDateUpdate( daoUtil.getTimestamp( 3 ) );
                list.add( portlet );
            }
        }

        return list;
    }

    /**
     * Reads the portlet's name
     * @param nPortletId the identifier of the portlet
     * @return The name of the portlet
     */
    public String selectRssFilePortletName( int nPortletId )
    {
        String strPortletName = "";
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_PORTLET_NAME ) )
        {
            daoUtil.setInt( 1, nPortletId );
    
            daoUtil.executeQuery(  );
    
            if ( daoUtil.next(  ) )
            {
                strPortletName = daoUtil.getString( 1 );
            }
        }

        return strPortletName;
    }

    /**
     * Tests if the portlet has not been deleted before update
     * @param nPortletId The portlet identifier for this RSS file
     * @return true il the portlet exist
     */
    public boolean checkRssFilePortlet( int nPortletId )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_CHECK_PORTLET_EXISTENCE ) )
        {
            daoUtil.setInt( 1, nPortletId );
    
            daoUtil.executeQuery(  );
    
            if ( !daoUtil.next(  ) )
            {
                daoUtil.free(  );
    
                return false;
            }
        }

        return true;
    }

    /**
     * Returns all the documents of a portlet whose identifier is specified in
     * parameter
     * @param nPortletId the identifier of the portlet
     * @return List of documents
     */
    public List<Document> selectDocumentsByPortlet( int nPortletId )
    {
        List<Document> list = new ArrayList<>(  );
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_DOCUMENT_BY_PORTLET ) )
        {
            daoUtil.setInt( 1, nPortletId );
    
            daoUtil.executeQuery(  );
        
            while ( daoUtil.next(  ) )
            {
                Document document = new Document(  );
                document.setId( daoUtil.getInt( 1 ) );
                document.setCodeDocumentType( daoUtil.getString( 2 ) );
                document.setDateCreation( daoUtil.getTimestamp( 3 ) );
                document.setDateModification( daoUtil.getTimestamp( 4 ) );
                document.setTitle( daoUtil.getString( 5 ) );
                document.setSummary( daoUtil.getString( 6 ) );
                document.setXmlValidatedContent( daoUtil.getString( 7 ) );
                list.add( document );
            }
        }

        return list;
    }

    /**
     * Returns the stylesheet for RSS files
     * @param nStyleSheetId the identifier of the Stylesheet
     * @return the stylesheet
     */
    public StyleSheet selectXslFile( int nStyleSheetId )
    {
        StyleSheet stylesheet = new StyleSheet(  );
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_XSL_FILE ) )
        {
            daoUtil.setInt( 1, nStyleSheetId );
    
            daoUtil.executeQuery(  );
    
            if ( daoUtil.next(  ) )
            {
                stylesheet.setId( daoUtil.getInt( 1 ) );
                stylesheet.setDescription( daoUtil.getString( 2 ) );
                stylesheet.setFile( daoUtil.getString( 3 ) );
                stylesheet.setSource( daoUtil.getBytes( 4 ) );
            }
        }

        return stylesheet;
    }
}
