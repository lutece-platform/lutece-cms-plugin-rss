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
package fr.paris.lutece.plugins.rss.business.portlet;

import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * this class provides Data Access methods for RssPortlet objects
 */
public final class RssPortletDAO implements IRssPortletDAO
{
    ////////////////////////////////////////////////////////////////////////////
    // Constants
    private static final String SQL_QUERY_SELECT = "SELECT id_portlet, rss_feed_id FROM rss_portlet WHERE id_portlet = ? ";
    private static final String SQL_QUERY_INSERT = "INSERT INTO rss_portlet ( id_portlet, rss_feed_id ) VALUES ( ?, ? )";
    private static final String SQL_QUERY_DELETE = "DELETE FROM rss_portlet WHERE id_portlet = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE rss_portlet SET id_portlet = ?, rss_feed_id = ? WHERE id_portlet = ? ";
    private static final String SQL_QUERY_LINKED_PORTLET = "SELECT id_portlet FROM rss_portlet WHERE rss_feed_id = ? ";

    ///////////////////////////////////////////////////////////////////////////////////////
    // Access methods to data

    /**
     * Insert a new record in the table.
     *
     * @param portlet The Instance of the Portlet
     */
    public void insert( Portlet portlet )
    {
        RssPortlet p = (RssPortlet) portlet;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT );
        daoUtil.setInt( 1, p.getId(  ) );
        daoUtil.setString( 2, p.getRssFeedId(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Delete record from table
     *
     * @param nPortletId The indentifier of the Portlet
     */
    public void delete( int nPortletId )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE );
        daoUtil.setInt( 1, nPortletId );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Update the record in the table
     *
     * @param portlet The reference of the portlet
     */
    public void store( Portlet portlet )
    {
        RssPortlet p = (RssPortlet) portlet;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE );
        daoUtil.setInt( 1, p.getId(  ) );
        daoUtil.setString( 2, p.getRssFeedId(  ) );
        daoUtil.setInt( 3, p.getId(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * load the data of dbpagePortlet from the table
     * @return portlet The instance of the object portlet
     * @param nIdPortlet The identifier of the portlet
     */
    public Portlet load( int nIdPortlet )
    {
        RssPortlet portlet = new RssPortlet(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT );
        daoUtil.setInt( 1, nIdPortlet );
        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            portlet.setId( daoUtil.getInt( 1 ) );
            portlet.setRssFeedId( daoUtil.getString( 2 ) );
        }

        daoUtil.free(  );

        return portlet;
    }

    /**
     * Checks if a feed is linked to a portlet
     * @return A boolean
     * @param nIdRssFeed The identifier of the Rss feed
     */
    public List<RssPortlet> checkLinkedPortlet( int nIdRssFeed )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_LINKED_PORTLET );
        daoUtil.setInt( 1, nIdRssFeed );
        daoUtil.executeQuery(  );

        List<RssPortlet> listPortlet = new ArrayList<RssPortlet>(  );

        while ( daoUtil.next(  ) )
        {
            RssPortlet portlet = new RssPortlet(  );
            portlet.setId( daoUtil.getInt( 1 ) );
            portlet.setRssFeedId( Integer.toString( nIdRssFeed ) );
            listPortlet.add( portlet );
        }

        daoUtil.free(  );

        return listPortlet;
    }
}
