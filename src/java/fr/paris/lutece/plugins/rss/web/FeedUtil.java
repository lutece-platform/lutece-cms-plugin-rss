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
package fr.paris.lutece.plugins.rss.web;

import com.sun.syndication.feed.WireFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.WireFeedOutput;

import fr.paris.lutece.plugins.rss.service.type.FeedTypeManager;
import fr.paris.lutece.portal.business.rss.IFeedResource;
import fr.paris.lutece.portal.business.rss.IResourceRss;
import fr.paris.lutece.portal.service.util.AppLogService;

import org.apache.commons.lang3.StringUtils;
import jakarta.enterprise.inject.spi.CDI;


/**
 *
 * RSSUtil.
 */
public final class FeedUtil
{
    /**
     * Utility class
     */
    private FeedUtil(  )
    {
        // nothing
    }

    /**
     * Gets an XML for the given resource.
     * @param resource resource
     * @param strFeedType feed type
     * @param strEncoding encoding
     * @param nMaxItems max items
     * @return the xml generated
     */
    public static String getFeed( IFeedResource resource, String strFeedType, String strEncoding, int nMaxItems )
    {
        if ( resource.getItems(  ).isEmpty(  ) )
        {
            return StringUtils.EMPTY;
        }

        // WireFeed does not contain enough data, but can be passed for output generation.
        WireFeed wireFeed = CDI.current( ).select( FeedTypeManager.class ).get( ).getWireFeed( strFeedType, resource, strEncoding, nMaxItems );

        WireFeedOutput output = new WireFeedOutput(  );
        String strXML = StringUtils.EMPTY;

        try
        {
            strXML = output.outputString( wireFeed );
        }
        catch ( IllegalArgumentException | FeedException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }

        return strXML;
    }

    /**
     * Gets an XML for the given resource.
     * @param resourceRSS the resource
     * @return the XML as String, encoding is {@link IResourceRss#getEncoding()}. Returns an empty String if no items.
     * Returns <code>{@link IResourceRss#createHtmlRss()}</code> if {@link IResourceRss#getFeed()} is not implemented yet.
     */
    public static String getFeed( IResourceRss resourceRSS )
    {
        IFeedResource resource = resourceRSS.getFeed(  );

        if ( resource == null )
        {
            AppLogService.error( "The resource rss {} gives no feed", resourceRSS.getName( ) );

            return StringUtils.EMPTY;
        }

        if ( resource.getItems(  ).isEmpty(  ) )
        {
            // an empty feed will fail
            return StringUtils.EMPTY;
        }

        return getFeed( resource, resourceRSS.getFeedType(  ), resourceRSS.getEncoding(  ), resourceRSS.getMaxItems(  ) );
    }
}
