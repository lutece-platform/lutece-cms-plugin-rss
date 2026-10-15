/*
 * Copyright (c) 2002-2026, Mairie de Paris
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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.rss.business.RssFeed;
import fr.paris.lutece.plugins.rss.business.RssFeedHome;
import fr.paris.lutece.test.LuteceTestCase;

public class RssContentLoaderTest extends LuteceTestCase
{
    private static final String UNREACHABLE_URL = "http://localhost:1/rss-content-loader-test.xml";

    @AfterEach
    public void removeTestFeeds( )
    {
        for ( boolean bActive : new boolean [ ] { true, false } )
        {
            RssFeedHome.getRssFeeds( bActive ).stream( ).filter( f -> UNREACHABLE_URL.equals( f.getUrl( ) ) ).forEach( RssFeedHome::remove );
        }
    }

    @Test
    public void testFailureIsCounted( ) throws Exception
    {
        RssFeed rss = createFeed( 0 );

        RssContentLoader.fetchRssFeed( rss.getId( ) );

        RssFeed stored = RssFeedHome.findByPrimaryKey( rss.getId( ), true );
        assertNotNull( stored, "feed still active" );
        assertEquals( 1, stored.getNbFailures( ), "one failure counted" );
    }

    @Test
    public void testDeactivatedAtThreshold( ) throws Exception
    {
        RssFeed rss = createFeed( 9 );

        RssContentLoader.fetchRssFeed( rss.getId( ) );

        assertNull( RssFeedHome.findByPrimaryKey( rss.getId( ), true ), "feed no longer active" );
        assertEquals( 1, RssFeedHome.getRssFeeds( false ).stream( ).filter( f -> UNREACHABLE_URL.equals( f.getUrl( ) ) ).count( ), "feed now inactive" );
    }

    private static RssFeed createFeed( int nNbFailures )
    {
        RssFeed rss = new RssFeed( );
        rss.setName( "test" );
        rss.setUrl( UNREACHABLE_URL );
        rss.setWorkgroup( "all" );
        rss.setIdIncludeStyle( -1 );
        rss.setIsActive( true );
        RssFeedHome.create( rss );

        rss.setNbFailures( nNbFailures );
        RssFeedHome.updateLastFetchInfos( rss );

        return rss;
    }
}
