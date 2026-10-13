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
package fr.paris.lutece.plugins.rss.service.type;

import com.sun.syndication.feed.WireFeed;

import fr.paris.lutece.plugins.rss.service.type.atom.DefaultAtomFeedTypeProvider;
import fr.paris.lutece.plugins.rss.service.type.rss.DefaultRssFeedTypeProvider;
import fr.paris.lutece.portal.business.rss.IFeedResource;

import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;


/**
 *
 * FeedTypeManager
 *
 */
@ApplicationScoped
public class FeedTypeManager
{
    @Inject
    private Instance<IFeedTypeProvider> _providers;

    @Inject
    private DefaultRssFeedTypeProvider _defaultRssProvider;

    @Inject
    private DefaultAtomFeedTypeProvider _defaultAtomProvider;

    private List<IFeedTypeProvider> _listDefaultProviders;

    /**
     * Builds the list of the default providers, used when no other provider handles a feed type
     */
    @PostConstruct
    void init(  )
    {
        _listDefaultProviders = List.of( _defaultRssProvider, _defaultAtomProvider );
    }

    /**
     * Gets the {@link WireFeed}
     * @param strFeedType the feed type
     * @param resource the resource
     * @param strEncoding the encoding
     * @param nMaxItems the max items
     * @return the {@link WireFeed}, <code>null</code> if no provider is found.
     */
    public WireFeed getWireFeed( String strFeedType, IFeedResource resource, String strEncoding, int nMaxItems )
    {
        for ( IFeedTypeProvider provider : _providers )
        {
            if ( provider.isInvoked( strFeedType ) )
            {
                return provider.getWireFeed( strFeedType, resource, strEncoding, nMaxItems );
            }
        }

        for ( IFeedTypeProvider defaultProvider : _listDefaultProviders )
        {
            if ( defaultProvider.isInvoked( strFeedType ) )
            {
                return defaultProvider.getWireFeed( strFeedType, resource, strEncoding, nMaxItems );
            }
        }

        return null;
    }
}
