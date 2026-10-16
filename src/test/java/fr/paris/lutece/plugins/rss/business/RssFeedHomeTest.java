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
package fr.paris.lutece.plugins.rss.business;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import fr.paris.lutece.test.LuteceTestCase;

public class RssFeedHomeTest extends LuteceTestCase
{
    private static final String URL_PREFIX = "http://localhost/rss-feed-home-test/";

    @Override
    protected void tearDown( ) throws Exception
    {
        for ( String strName : new String [ ] { "a", "b", "c", "d", "e" } )
        {
            for ( boolean bActive : new boolean [ ] { true, false } )
            {
                RssFeedHome.getRssFeeds( bActive ).stream( ).filter( f -> ( URL_PREFIX + strName ).equals( f.getUrl( ) ) ).findFirst( ).ifPresent( RssFeedHome::remove );
            }
        }
        super.tearDown( );
    }

    public void testActivationKeepsActiveIdsContiguous( )
    {
        int nActive = RssFeedHome.getRssFeeds( true ).size( );
        RssFeed a = createFeed( "a", 0 );
        createFeed( "b", 0 );

        RssFeedHome.setActive( a, false );
        createFeed( "c", 0 );
        RssFeedHome.setActive( find( "a", false ), true );

        List<Integer> listActiveIds = RssFeedHome.getRssFeeds( true ).stream( ).map( RssFeed::getId ).sorted( ).collect( Collectors.toList( ) );
        assertEquals( "active ids from 1 to N", IntStream.rangeClosed( 1, nActive + 3 ).boxed( ).collect( Collectors.toList( ) ), listActiveIds );
        assertEquals( "b moved up", nActive + 1, find( "b", true ).getId( ) );
        assertEquals( "c after b", nActive + 2, find( "c", true ).getId( ) );
        assertEquals( "a reactivated last", nActive + 3, find( "a", true ).getId( ) );
    }

    public void testUpdateOrderKeepsFailures( )
    {
        RssFeed d = createFeed( "d", 0 );
        RssFeed e = createFeed( "e", 5 );

        RssFeedHome.updateOrder( d, e.getId( ) );

        assertEquals( "shifted feed keeps its failures", 5, find( "e", true ).getNbFailures( ) );
        assertEquals( "moved feed keeps its failures", 0, find( "d", true ).getNbFailures( ) );
    }

    private static RssFeed createFeed( String strName, int nNbFailures )
    {
        RssFeed rss = new RssFeed( );
        rss.setName( strName );
        rss.setUrl( URL_PREFIX + strName );
        rss.setWorkgroup( "all" );
        rss.setIdIncludeStyle( -1 );
        rss.setIsActive( true );
        RssFeedHome.create( rss );

        rss.setNbFailures( nNbFailures );
        RssFeedHome.updateLastFetchInfos( rss );

        return rss;
    }

    private static RssFeed find( String strName, boolean bActive )
    {
        return RssFeedHome.getRssFeeds( bActive ).stream( ).filter( f -> ( URL_PREFIX + strName ).equals( f.getUrl( ) ) ).findFirst( ).orElseThrow( );
    }
}
