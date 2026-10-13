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

import fr.paris.lutece.plugins.rss.business.portlet.IRssPortletDAO;
import fr.paris.lutece.plugins.rss.business.portlet.RssPortlet;
import jakarta.enterprise.inject.spi.CDI;
import fr.paris.lutece.util.ReferenceList;

import java.util.ArrayList;
import java.util.List;


/**
 * This class provides instances management methods (create, find, ...) for RssFeed objects
 */
public final class RssFeedHome
{
    // Static variable pointed at the DAO instance
    private static IRssFeedDAO _dao = CDI.current( ).select( IRssFeedDAO.class ).get( );
    private static IRssPortletDAO _daoPortlet = CDI.current( ).select( IRssPortletDAO.class ).get( );

    /**
     * Private constructor - this class need not be instantiated
     */
    private RssFeedHome(  )
    {
    }

    /**
     * Creation of an instance of rssFeed
     * @param rssFeed The instance of the rssFeed which contains the informations to store
     * @return The  instance of rssFeed which has been created with its primary key.
     */
    public static RssFeed create( RssFeed rssFeed )
    {
        _dao.insert( rssFeed );

        return rssFeed;
    }

    /**
     * Update of the rssFeed which is specified in parameter
     * @param rssFeed The instance of the rssFeed which contains the data to store
     * @return The instance of the  rssFeed which has been updated
     */
    public static RssFeed update( RssFeed rssFeed )
    {
        _dao.store( rssFeed );

        return rssFeed;
    }

    /**
     * Update of the rssFeed which is specified in parameter
     * @param rssFeed The instance of the rssFeed which contains the data to store
     * @return The instance of the  rssFeed which has been updated
     */
    public static RssFeed updateLastFetchInfos( RssFeed rssFeed )
    {
        _dao.storeLastFetchInfos( rssFeed );

        return rssFeed;
    }

    /**
     * Remove the RssFeed
     * @param rssFeed The rssFeed to remove
     */
    public static void remove( RssFeed rssFeed )
    {
        if ( rssFeed.getIsActive(  ) )
        {
            int nMaxOrder = _dao.newPrimaryKey( true ) - 1;
            rssFeed = updateOrder( rssFeed, nMaxOrder );
        }

        _dao.delete( rssFeed );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a rssFeed whose identifier is specified in parameter
     * @param nKey The Primary key of the rssFeed
     * @param bActive <code>true</code> if the rssFeed is active, <code>false</code> otherwise
     * @return An instance of rssFeed
     */
    public static RssFeed findByPrimaryKey( int nKey, boolean bActive )
    {
        return _dao.load( nKey, bActive );
    }

    /**
     * Returns a list of rss feeds
     * @return A List of rssFeeds
     */
    public static List<RssFeed> getRssFeeds(  )
    {
        return _dao.selectRssFeeds( true );
    }

    /**
     * Returns a list of rss feeds
     * @param bActive <code>true</code> for active feeds
     * @return A List of rssFeeds
     */
    public static List<RssFeed> getRssFeeds( boolean bActive )
    {
        return _dao.selectRssFeeds( bActive );
    }

    /**
     * Returns a list of rssFeeds objects
     * @return A list of rssFeeds
     */
    public static ReferenceList getRssFeedsReferenceList(  )
    {
        return _dao.selectRssFeedReferenceList( true );
    }

    /**
     * Returns a list of rssFeeds objects
     * @param bActive <code>true</code> for active feeds
     * @return A list of rssFeeds
     */
    public static ReferenceList getRssFeedsReferenceList( boolean bActive )
    {
        return _dao.selectRssFeedReferenceList( bActive );
    }

    /**
     * Checks whether the a url is found in one of the external feeds
     * @param strUrl The url to be tested
     * @return The boolean result
     */
    public static boolean checkUrlNotUsed( String strUrl )
    {
        return _dao.checkUrlNotUsed( strUrl );
    }

    /**
     * Updates an active feed to a new order and shifts in-between feeds consequently.
     * Linked portlets get updated as well.
     *
     * @param rssFeed The feed to update order
     * @param nNewOrder the new order for the feed
     * @return the feed updated to the new order or <code>null</code> if the feed is inactive
     * @throws IndexOutOfBoundsException if  the new order parameter is less than 1 or greater than the current max order
     */
    public static RssFeed updateOrder( RssFeed rssFeed, int nNewOrder )
        throws IndexOutOfBoundsException
    {
        if ( !rssFeed.getIsActive(  ) )
        {
            return null;
        }

        int nMaxOrder = _dao.newPrimaryKey( true ) - 1;
        int nOldOrder = rssFeed.getId(  );

        if ( ( nNewOrder < 1 ) || ( nNewOrder > nMaxOrder ) )
        {
            throw new IndexOutOfBoundsException(  );
        }

        if ( nNewOrder == nOldOrder )
        {
            return rssFeed;
        }

        List<RssPortlet> listLinkedPortlet = new ArrayList<RssPortlet>(  );

        for ( RssFeed currentRssFeed : getRssFeeds( true ) )
        {
            int nCurrentOrder = currentRssFeed.getId(  );

            if ( ( nOldOrder > nNewOrder ) && ( nCurrentOrder < nOldOrder ) && ( nCurrentOrder >= nNewOrder ) )
            {
                currentRssFeed.setId( nCurrentOrder + 1 );
                _dao.store( currentRssFeed );
                _dao.storeLastFetchInfos( currentRssFeed );

                for ( RssPortlet linkedPortlet : _daoPortlet.checkLinkedPortlet( nCurrentOrder ) )
                {
                    linkedPortlet.setRssFeedId( Integer.toString( nCurrentOrder + 1 ) );
                    listLinkedPortlet.add( linkedPortlet );
                }
            }
            else if ( ( nOldOrder < nNewOrder ) && ( nCurrentOrder > nOldOrder ) && ( nCurrentOrder <= nNewOrder ) )
            {
                currentRssFeed.setId( nCurrentOrder - 1 );
                _dao.store( currentRssFeed );
                _dao.storeLastFetchInfos( currentRssFeed );

                for ( RssPortlet linkedPortlet : _daoPortlet.checkLinkedPortlet( nCurrentOrder ) )
                {
                    linkedPortlet.setRssFeedId( Integer.toString( nCurrentOrder - 1 ) );
                    listLinkedPortlet.add( linkedPortlet );
                }
            }
        }

        rssFeed.setId( nNewOrder );
        _dao.store( rssFeed );
        _dao.storeLastFetchInfos( rssFeed );

        for ( RssPortlet linkedPortlet : _daoPortlet.checkLinkedPortlet( nOldOrder ) )
        {
            linkedPortlet.setRssFeedId( Integer.toString( nNewOrder ) );
            listLinkedPortlet.add( linkedPortlet );
        }

        for ( RssPortlet portletToUpdate : listLinkedPortlet )
        {
            _daoPortlet.store( portletToUpdate );
        }

        return rssFeed;
    }

    /**
     * De/activate a feed
     * @param rssFeed the feed to de/activate
     * @param bActive <code>true</code> to activate the feed, <code>false</code> otherwise
     * @return the updated feed
     */
    public static RssFeed setActive( RssFeed rssFeed, boolean bActive )
    {
        remove( rssFeed );
        rssFeed.setIsActive( bActive );

        return create( rssFeed );
    }
}
