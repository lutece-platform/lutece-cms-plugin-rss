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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;

import org.junit.jupiter.api.Test;

/**
 * RssFeedParserTest
 */
public class RssFeedParserTest
{
    private static final String FEED = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><rss version=\"2.0\"><channel><title>Feed title</title>"
            + "<link>https://example.org/</link><description>Feed description</description>"
            + "<item><title>First item</title><link>https://example.org/1</link><description>First text</description></item>"
            + "<item><title>Second item</title><link>https://example.org/2</link></item></channel></rss>";

    /**
     * A RSS 2.0 document gives its channel and its items.
     */
    @Test
    public void testParseRss( )
    {
        SyndFeed feed = RssFeedParser.parse( FEED );

        assertNotNull( feed, "feed parsed" );
        assertEquals( "Feed title", feed.getTitle( ), "channel title" );
        assertEquals( 2, feed.getEntries( ).size( ), "number of items" );
        assertEquals( "https://example.org/1", ( (SyndEntry) feed.getEntries( ).get( 0 ) ).getLink( ), "item link" );
    }

    /**
     * An empty or unreadable content gives no feed.
     */
    @Test
    public void testParseInvalid( )
    {
        assertNull( RssFeedParser.parse( null ), "null content" );
        assertNull( RssFeedParser.parse( " " ), "blank content" );
        assertNull( RssFeedParser.parse( "<html><body>not a feed</body></html>" ), "not a feed" );
    }
}
